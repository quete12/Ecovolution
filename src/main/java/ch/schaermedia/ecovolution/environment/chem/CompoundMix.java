/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Quentin
 */
public class CompoundMix {

    public static final double STATIC_PRESSURE_kPa = 101.325;
    public static final double STATIC_VOLUME_L = 100000;

    // Map<code, Compound>[phase_idx]
    private final PhaseMix[] mix;

    private final int x, y, z;

    private double heatCapacitySum;
    private double amount_mol;
    private double volume_L;
    private double pressure_kPa;

    private int compoundCount;

    private CompoundMix lower;
    private CompoundMix higher;

    //TODO find a way to prevent negative temperature since this could really screw up other calculations (resulting in nevative volume, pressure etc)
    private double temperature_K;

    public CompoundMix(int x, int y, int z, int horizontalSpreadSize)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mix = new PhaseMix[Phase.values().length];
        initMix(horizontalSpreadSize);
    }

    public void addAsNeibour(CompoundMix neighbour)
    {
        for (PhaseMix phaseMix : mix)
        {
            phaseMix.addAsNeighbour(neighbour.getMixForPhase(phaseMix.getPhase().idx));
        }
    }

    private void initMix(int horizontalSpreadSize)
    {
        for (Phase value : Phase.values())
        {
            mix[value.idx] = new PhaseMix(value, horizontalSpreadSize);
        }
    }

    public Compound[] getPhasesByCode(String code)
    {
        Compound[] result = new Compound[mix.length];
        for (int i = 0; i < mix.length; i++)
        {
            result[i] = mix[i].getCompound(code);

        }
        return result;
    }

    public void addEnergy(double energy_kj)
    {
        double added = 0;
        for (PhaseMix phaseMix : mix)
        {
            double phasePercentage = phaseMix.getHeatCapacitySum() / heatCapacitySum;
            if (phasePercentage == 0)
            {
                continue;
            }
            double toAdd = energy_kj * phasePercentage;
            added += toAdd;
            phaseMix.addEnergy(toAdd);
        }

        if (Math.abs(added - energy_kj) > 0.00001)
        {
            throw new RuntimeException("Lost Energy while adding! added: " + added + " expected: " + energy_kj);
        }
    }

    public void add(String code, int phase, double amount_mol, double energy_kj)
    {
        mix[phase].add(code, amount_mol, energy_kj);
    }

    public void spreadHorizontal()
    {
        for (PhaseMix phaseMix : mix)
        {
            phaseMix.spreadHorizontal();
        }
    }

    public void spreadToLower()
    {
        if (!hasLower())
        {
            return;
        }
        double toSpread = lower.molesUnderVolume();
        if (toSpread == 0)
        {
            return;
        }
        for (int i = 0; i < mix.length; i++)
        {
            if (Math.abs(toSpread) < 0.000005)
            {
                break;
            }
            if (toSpread < 0)
            {
                throw new RuntimeException("Spread too much!!" + toSpread);
            }
            PhaseMix phaseMix = mix[i];
            if (phaseMix.getAmount_mol() == 0)
            {
                continue;
            }
            double percentage = toSpread / phaseMix.getAmount_mol();
            if (percentage > 1)
            {
                percentage = 1;
            }
            PhaseMix lowerPhaseMix = lower.getMixForPhase(i);
            phaseMix.spreadPercentage(lowerPhaseMix, percentage);
            toSpread -= phaseMix.getAmount_mol() * percentage;
        }
    }

    public void spreadToHigher()
    {
        if (!hasHigher())
        {
            return;
        }
        double toSpread = molesOverVolume();
        if (toSpread == 0)
        {
            return;
        }
        for (int i = mix.length - 1; i >= 0; i--)
        {
            if (Math.abs(toSpread) < 0.000005)
            {
                break;
            }
            if (toSpread < 0)
            {
                throw new RuntimeException("Spread too much!!" + toSpread);
            }
            PhaseMix phaseMix = mix[i];
            if (phaseMix.getAmount_mol() == 0)
            {
                continue;
            }
            double percentage = toSpread / phaseMix.getAmount_mol();
            if (percentage > 0.9)
            {
                percentage = 0.9;
            }
            PhaseMix higherPhaseMix = higher.getMixForPhase(i);
            phaseMix.spreadPercentage(higherPhaseMix, percentage);
            toSpread -= phaseMix.getAmount_mol() * percentage;
        }
    }

    private List<Compound>[] sortByPhase(List<Compound> unsorted)
    {
        List<Compound>[] results = new List[Phase.values().length];
        for (Compound compound : unsorted)
        {
            int phaseIdx = compound.getPhase().idx;
            if (results[phaseIdx] == null)
            {
                results[phaseIdx] = new ArrayList();
            }
            results[phaseIdx].add(compound);
        }
        return results;
    }

    public void updateTemperatureAndPhaseChanges()
    {
        List<Compound> changedCompounds = new ArrayList<>();
        double temperatureSum = 0;
        for (PhaseMix phaseMix : mix)
        {
            phaseMix.updateTemperatureAndPhase(pressure_kPa);
            temperatureSum += phaseMix.getTemperature_K();
            List<Compound> compounds = phaseMix.getAndRemoveCompoundsOnPhaseChange();
            changedCompounds.addAll(compounds);
        }
        temperature_K = (compoundCount > 0) ? temperatureSum / compoundCount : 0;
        List<Compound>[] sorted = sortByPhase(changedCompounds);
        for (int i = 0; i < sorted.length; i++)
        {
            List<Compound> compounds = sorted[i];
            //TODO: solve null properly
            if (compounds == null)
            {
                continue;
            }
            mix[i].importCompounds(compounds);
        }
    }

    public void updateStats()
    {
        amount_mol = 0;
        heatCapacitySum = 0;
        volume_L = 0;
        pressure_kPa = 0;
        compoundCount = 0;

        for (PhaseMix phaseMix : mix)
        {
            phaseMix.updateStats();
            amount_mol += phaseMix.getAmount_mol();
            heatCapacitySum += phaseMix.getHeatCapacitySum();
            volume_L += phaseMix.getVolume_L();
            pressure_kPa += phaseMix.getPressure_kPa();

            compoundCount += phaseMix.numberOfCompounds();
        }
    }

    public PhaseMix getMixForPhase(int phase)
    {
        return mix[phase];
    }

    public double molesUnderPressure()
    {
        double diffPressure = STATIC_PRESSURE_kPa - pressure_kPa;
        if (diffPressure <= 0)
        {
            return 0;
        }
        //Use static volume because the original pressure was calculated using static volume. If we would use our calculated volume the result would be way off!
        return ChemUtilities.moles(diffPressure, STATIC_VOLUME_L, temperature_K);
    }

    public double molesOverPressure()
    {
        double diffPressure = pressure_kPa - STATIC_PRESSURE_kPa;
        if (diffPressure <= 0)
        {
            return 0;
        }
        //Use static volume because the original pressure was calculated using static volume. If we would use our calculated volume the result would be way off!
        return ChemUtilities.moles(diffPressure, STATIC_VOLUME_L, temperature_K);
    }

    public double molesOverVolume()
    {
        double diffVolume = volume_L - STATIC_VOLUME_L;
        if (diffVolume <= 0)
        {
            return 0;
        }
        //Use static pressure because the original volume was calculated using static pressure. If we would use our calculated pressure the result would be way off!
        return ChemUtilities.moles(STATIC_PRESSURE_kPa, diffVolume, temperature_K);
    }

    public double molesUnderVolume()
    {
        double diffVolume = STATIC_VOLUME_L - volume_L;
        if (diffVolume <= 0)
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

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public void setHigher(CompoundMix higher)
    {
        this.higher = higher;
    }

    public void setLower(CompoundMix lower)
    {
        this.lower = lower;
    }

    public boolean hasLower()
    {
        return lower != null;
    }

    public boolean hasHigher()
    {
        return higher != null;
    }

}
