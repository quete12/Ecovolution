/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Quentin
 */
public class CompoundMix {

    public static final double STATIC_PRESSURE_kPa = 101.325;
    public static final double STATIC_VOLUME_L = 100000;

    // Map<code, Compound[phase_idx]>
    private final Map<String, Compound>[] mix;

    private final int x, y, z;

    private double heatCapacitySum;
    private double amount_mol;
    private double volume_L;
    private double pressure_kPa;

    private int compoundCount;

    //TODO find a way to prevent negative temperature since this could really screw up other calculations (resulting in nevative volume, pressure etc)
    private double temperature_K;

    public CompoundMix(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mix = new HashMap[Phase.values().length];
        initMix();
    }

    private void initMix()
    {
        for (int i = 0; i < mix.length; i++)
        {
            mix[i] = new HashMap();
        }
    }

    public void spread(List<CompoundMix> layer, CompoundMix higher, CompoundMix lower, int range)
    {
        if (amount_mol == 0)
        {
            return;
        }
        boolean hasLower = lower != null;
        boolean hasHigher = higher != null;
        double tmp_volume_L = volume_L;
        double tmp_pressure_kPa = pressure_kPa;
        if (hasLower)
        {
            double spreadPercentage = spreadToLower(lower);
            tmp_volume_L -= tmp_volume_L * spreadPercentage;
            tmp_pressure_kPa -= tmp_pressure_kPa * spreadPercentage;
        }
        if (hasHigher)
        {
            spreadToHigher(higher, tmp_volume_L, tmp_pressure_kPa);
        }
        int side = 2 * range + 1;
        spread(layer, side * side);
    }

    public Compound[] getPhasesByCode(String code)
    {
        Compound[] result = new Compound[mix.length];
        for (int i = 0; i < mix.length; i++)
        {
            result[i] = mix[i].get(code);

        }
        return result;
    }

    public void addEnergy(double energy_kj)
    {
        for (Map<String, Compound> value : mix)
        {
            for (Compound compound : value.values())
            {
                if (compound == null)
                {
                    continue;
                }
                double percent = compound.getTotalHeatCapacity() / heatCapacitySum;
                compound.addEnergy(energy_kj * percent);
            }
        }
    }

    public void add(String code, int phase, double amount_mol, double energy_kj)
    {
        Map<String, Compound> phaseMix = mix[phase];
        Compound compound = phaseMix.get(code);
        if (compound == null)
        {
            compound = new Compound(CompoundProperties.getPropertiesFromCode(code));
            phaseMix.put(code, compound);
        }
        compound.addAmount(amount_mol);
        compound.addEnergy(energy_kj);
    }

    private void spread(List<CompoundMix> layer, int sqareSize)
    {
        double percentage = (double) (1.0 / sqareSize);
        double totalSpreadPercentage = percentage * layer.size();
        //to avoid any conflics with previous calculations we invert compound and layer spreading in comparison to spreadByPercentage()
        for (Map<String, Compound> value : mix)
        {
            for (Compound compound : value.values())
            {
                if (compound == null)
                {
                    continue;
                }

                double splitMoles = compound.splitMoles(totalSpreadPercentage);
                double splitEnergy = compound.splitEnergy(totalSpreadPercentage);
                if (splitMoles < 0)
                {
                    System.out.println("x: " + x + " y: " + y + " z: " + z);
                    System.out.println(compound);
                }
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
        if (percentage > 1)
        {
            percentage = 1;
        }
        spreadByPercentage(lower, percentage);
        return percentage;
    }

    private double spreadToHigher(CompoundMix higher, double currentVolume, double currentPressure)
    {
        if (currentVolume <= STATIC_VOLUME_L || currentPressure <= higher.getPressure_kPa())
        {
            return 0;
        }
        double molesOverVolume = molesOverVolume(currentVolume);
        double percentage = molesOverVolume / amount_mol;
        if (percentage > .9)
        {
            percentage = .9;
        }
        spreadByPercentage(higher, percentage);
        return percentage;
    }

    private void spreadByPercentage(CompoundMix spreadTo, double percentage)
    {
        for (int i = 0; i < mix.length; i++)
        {
            Map<String, Compound> value = mix[i];
            for (Map.Entry<String, Compound> entry : value.entrySet())
            {
                String key = entry.getKey();
                Compound compound = entry.getValue();
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
        updateStats();
        updateTemperatureAndPhaseChanges();
    }

    private void updateTemperatureAndPhaseChanges()
    {
        double temperatureSum = 0;
        for (int i = 0; i < mix.length; i++)
        {
            Map<String, Compound> value = mix[i];
            List<String> toRemove = new ArrayList();
            for (Compound compound : value.values())
            {
                compound.updateTemperatureAndPhase(pressure_kPa);
                temperatureSum += compound.getTemperature_K();
                int phaseIdx = compound.getPhase().idx;
                if (phaseIdx != i)
                {
                    if (mix[phaseIdx].containsKey(compound.getCode()))
                    {
                        mix[phaseIdx].get(compound.getCode()).importCompound(compound);
                    } else
                    {
                        mix[phaseIdx].put(compound.getCode(), compound);
                    }
                    toRemove.add(compound.getCode());
                }
            }
            for (String string : toRemove)
            {
                mix[i].remove(string);
            }
        }
        temperature_K = (compoundCount > 0) ? temperatureSum / compoundCount : 0;
    }

    private void updateStats()
    {
        amount_mol = 0;
        heatCapacitySum = 0;
        volume_L = 0;
        pressure_kPa = 0;
        compoundCount = 0;

        for (Map<String, Compound> value : mix)
        {
            for (Compound compound : value.values())
            {
                if (compound == null)
                {
                    continue;
                }
                compound.importBuffers();
                amount_mol += compound.getAmount_mol();
                heatCapacitySum += compound.getTotalHeatCapacity();

                volume_L += compound.volume_L(STATIC_PRESSURE_kPa);
                pressure_kPa += compound.pressure_kPa(STATIC_VOLUME_L);

                compoundCount++;
            }
        }
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
