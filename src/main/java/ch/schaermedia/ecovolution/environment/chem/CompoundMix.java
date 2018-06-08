/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Quentin
 */
public class CompoundMix {
    //Equivalent to 1 Atm
    public static final double STATIC_PRESSURE_kPa = 101.325;
    //10 * 10 * 100
    public static final double STATIC_VOLUME_L = 10000;

    // Map<code, Compound[phase_idx]>
    private final Map<String, Compound[]> mix;

    private final int x, y, z;

    private double heatCapacitySum;
    private double amount_mol;
    private double volume_L;
    private double pressure_kPa;
    //TODO find a way to prevent negative temperature since this could really screw up other calculations (resulting in nevative volume, pressure etc)
    private double temperature_K;

    public CompoundMix(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mix = new HashMap<>();
    }

    public void spread(List<CompoundMix> layer, CompoundMix higher, CompoundMix lower, int range)
    {
        if(amount_mol == 0){
            return;
        }
        boolean hasLower = lower != null;
        boolean hasHigher = higher != null;
        double tmp_volume_L = volume_L;
        if (hasLower)
        {
            double spreadPercentage = spreadToLower(lower);
            tmp_volume_L -= tmp_volume_L * spreadPercentage;
        }
        if (hasHigher)
        {
            spreadToHigher(higher, tmp_volume_L);
        }
        int side = 2 * range + 1;
        spread(layer, side * side);
    }

    public Compound[] getPhasesByCode(String code){
        return mix.get(code);
    }

    public void addEnergy(double energy_kj)
    {
        for (Compound[] value : mix.values())
        {
            for (Compound compound : value)
            {
                if (compound == null)
                {
                    continue;
                }
                double percent = compound.getSpecificHeatCapacity() / heatCapacitySum;
                compound.addEnergy(energy_kj * percent);
            }
        }
    }

    public void add(String code, int phase, double amount_mol, double energy_kj)
    {
        Compound[] compounds = mix.get(code);
        if (compounds == null)
        {
            compounds = new Compound[3];
            mix.put(code, compounds);
        }
        Compound compound = compounds[phase];
        if (compound == null)
        {
            compound = new Compound(CompoundProperties.getPropertiesFromCode(code));
            compounds[phase] = compound;
        }
        compound.addAmount(amount_mol);
        compound.addEnergy(energy_kj);
    }

    private void spread(List<CompoundMix> layer, int sqareSize)
    {
        /*
        Adding one (represents our Mix).
        It's important to include this mix in the calculation of average to keep a basevalue in this mix.
        If we would spread the full value we would get a wierd flickering going on.
         */
        double percentage = (double) (1.0 / sqareSize);
        double totalSpreadPercentage = percentage * layer.size();
        //to avoid any conflics with previous calculations we invert compound and layer spreading in comparison to spreadByPercentage()
        for (Compound[] value : mix.values())
        {
            for (Compound compound : value)
            {
                if (compound == null)
                {
                    continue;
                }
                //get the total amount to spread
                double splitMoles = compound.splitMoles(totalSpreadPercentage);
                double splitEnergy = compound.splitEnergy(totalSpreadPercentage);
                if (splitMoles < 0)
                {
                    System.out.println("x: " + x + " y: " + y + " z: " + z);
                    System.out.println(compound);
                }

                //devide it to each mix
                double splitMolesPerMix = splitMoles / layer.size();
                double splitEnergyPerMix = splitEnergy / layer.size();

                for (CompoundMix compoundMix : layer)
                {
                    compoundMix.add(compound.getCode(), compound.getPhase().idx, splitMolesPerMix, splitEnergyPerMix);
                }
            }
        }
    }

    /**
     * Tries to fill the lower mix untill lower mix has reached StaticPressure
     * (1 atm)
     *
     * @param lower
     */
    private double spreadToLower(CompoundMix lower)
    {
        if (lower.getPressure_kPa() >= STATIC_PRESSURE_kPa)
        {
            return 0;
        }
        //TODO: solids fall down
        //TODO: liquids rain down
        //TODO: if there is still space in mix below fill with Gases

        //Untill beforementionned features are implemented: spread a percentage of each compound and phase
        double molesToPressurize = lower.molesToPressurize();
        double percentage = molesToPressurize / amount_mol;
        if (percentage <= 0)
        {
            return 0;
        }
        //TODO: Test if using 100% will result in flickering.
        if (percentage > 1.0)
        {
            percentage = 1.0;
            //to safe CPU cycles: add the complete Compound here and return
        }
        spreadByPercentage(lower, percentage);
        return percentage;
    }

    /**
     * If the mixture takes up more volume than StaticVolume excess volume flows
     * to higher mixture
     *
     * @param higher
     */
    private double spreadToHigher(CompoundMix higher, double currentVolume)
    {
        if (currentVolume <= STATIC_VOLUME_L)
        {
            return 0;
        }
        double molesOverVolume = molesOverVolume(currentVolume);
        if(Double.isNaN(molesOverVolume)){
            System.out.println("NAN");
        }
        double percentage = molesOverVolume / amount_mol;
        //TODO: Test if using 100% will result in flickering.
        if (percentage > 1.0)
        {
            percentage = 1.0;
        }
        spreadByPercentage(higher, percentage);
        return percentage;
    }

    private void spreadByPercentage(CompoundMix spreadTo, double percentage)
    {
        for (Map.Entry<String, Compound[]> entry : mix.entrySet())
        {
            String key = entry.getKey();
            Compound[] value = entry.getValue();
            for (int i = 0; i < value.length; i++)
            {
                Compound compound = value[i];
                if (compound == null)
                {
                    continue;
                }
                spreadTo.add(key, i, compound.splitDirectMoles(percentage), compound.splitDirectEnergy(percentage));
            }
        }
    }

    public void update()
    {
        amount_mol = 0;
        heatCapacitySum = 0;
        volume_L = 0;
        pressure_kPa = 0;
        int compounds = 0;
        double temperatureSum = 0;
        for (Compound[] cl : mix.values())
        {
            for (int i = 0; i < cl.length; i++)
            {
                Compound compound = cl[i];
                if (compound == null)
                {
                    continue;
                }
                compound.update();
                int phaseIdx = compound.getPhase().idx;
                if (phaseIdx != i)
                {
                    if (cl[phaseIdx] == null)
                    {
                        cl[phaseIdx] = compound;
                    } else
                    {
                        cl[phaseIdx].importCompound(compound);
                    }
                    cl[i] = null;
                }
                amount_mol += compound.getAmount_mol();
                heatCapacitySum += compound.getTotalHeatCapacity();

                volume_L += compound.volume_L(STATIC_PRESSURE_kPa);
                pressure_kPa += compound.pressure_kPa(STATIC_VOLUME_L);

                temperatureSum += compound.getTemperature_K();
                if(Double.isNaN(volume_L)){
                    System.out.println("FIXME!!!!!");
                }
                compounds++;
            }
        }
        //for now we average the temperature of all individual compounds to get the mixture temperature
        temperature_K = (compounds > 0) ? temperatureSum / compounds : 0;
    }

    public double molesToPressurize()
    {
        double diffPressure = STATIC_PRESSURE_kPa - pressure_kPa;
        if (diffPressure < 0)
        {
            return 0;
        }
        //Use static volume because the original pressure was calculated using static volume. If we would use our calculated volume the result would be way off!
        return ChemUtilities.moles(diffPressure, STATIC_VOLUME_L, temperature_K);
    }

    public double molesOverVolume(double baseVolume)
    {
        double diffVolume = baseVolume - STATIC_VOLUME_L;
        if (diffVolume < 0)
        {
            return 0;
        }
        //Use static pressure because the original volume was calculated using static pressure. If we would use our calculated pressure the result would be way off!
        return ChemUtilities.moles(STATIC_PRESSURE_kPa, diffVolume, temperature_K);
    }

    public double getAmount_mol()
    {
        return amount_mol;
    }

    public double getVolume_L()
    {
        return volume_L;
    }

    public double getPressure_kPa()
    {
        return pressure_kPa;
    }

    public double getTemperature_K()
    {
        return temperature_K;
    }

}
