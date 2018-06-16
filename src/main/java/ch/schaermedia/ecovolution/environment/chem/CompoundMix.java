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

    //TODO find a way to prevent negative temperature since this could really screw up other calculations (resulting in nevative volume, pressure etc)
    private double temperature_K;

    public CompoundMix(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mix = new PhaseMix[Phase.values().length];
        initMix();
    }

    private void initMix()
    {
        for (Phase value : Phase.values())
        {
            mix[value.idx] = new PhaseMix(value);
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
            result[i] = mix[i].getCompound(code);

        }
        return result;
    }

    public void addEnergy(double energy_kj)
    {
        for (PhaseMix phaseMix : mix)
        {
            double phasePercentage = phaseMix.getHeatCapacitySum() / heatCapacitySum;
            if(phasePercentage == 0){
                continue;
            }
            double toAdd = energy_kj * phasePercentage;
            phaseMix.addEnergy(toAdd);
        }
    }

    public void add(String code, int phase, double amount_mol, double energy_kj)
    {
        mix[phase].add(code, amount_mol, energy_kj);
    }

    private void spread(List<CompoundMix> layer, int sqareSize)
    {
        double percentage = (double) (1.0 / sqareSize);
        for (PhaseMix phaseMix : mix)
        {
            int phase_idx = phaseMix.getPhase().idx;
            List<PhaseMix> phases = new ArrayList<>();
            for (CompoundMix compoundMix : layer)
            {
                phases.add(compoundMix.getMixForPhase(phase_idx));
            }
            phaseMix.spread(phases, percentage);
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
        for (PhaseMix phaseMix : mix)
        {
            PhaseMix toMix = spreadTo.getMixForPhase(phaseMix.getPhase().idx);
            phaseMix.spread(toMix, percentage);
        }
    }

    public void update()
    {
        updateStats();
        updateTemperatureAndPhaseChanges();
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

    private void updateTemperatureAndPhaseChanges()
    {
        List<Compound> changedCompounds = new ArrayList<>();
        double temperatureSum = 0;
        for (PhaseMix phaseMix : mix)
        {
            phaseMix.updateTemperature(pressure_kPa);
            List<Compound> compounds = phaseMix.getAndRemoveCompoundsOnPhaseChange();
            changedCompounds.addAll(compounds);
        }
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
        temperature_K = (compoundCount > 0) ? temperatureSum / compoundCount : 0;
    }

    private void updateStats()
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
