/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.compound;

import ch.schaermedia.ecovolution.environment.chem.AtmosphericEnity;
import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.general.math.Consts;
import java.util.List;

/**
 *
 * @author Quentin
 */
public class LayerMixture extends AtmosphericEnity {

    private final PhaseMixture[] phases;
    private final long layerVolume_L;

    private LayerMixture higher;
    private LayerMixture lower;

    private final int layerIdx;

    public LayerMixture(long layerVolume_L, int layerIdx)
    {
        this.layerVolume_L = layerVolume_L;
        this.layerIdx = layerIdx;
        phases = new PhaseMixture[Phase.values().length];
        initPhaseMixes();
    }

    private void initPhaseMixes()
    {
        for (int i = 0; i < phases.length; i++)
        {
            phases[i] = new PhaseMixture(i);
        }
    }

    public void setHigher(LayerMixture higher)
    {
        this.higher = higher;
        for (int i = 0; i < phases.length; i++)
        {
            phases[i].setHigher(higher.getMixtureForPhase(i));
        }
    }

    public void setLower(LayerMixture lower)
    {
        this.lower = lower;
        for (int i = 0; i < phases.length; i++)
        {
            phases[i].setLower(lower.getMixtureForPhase(i));
        }
    }

    public void addAsNeighbour(LayerMixture neighbour)
    {
        for (int i = 0; i < phases.length; i++)
        {
            phases[i].addAsNeighbour(neighbour.getMixtureForPhase(i));
        }
    }

    public void spreadHorizontal()
    {
        phases[Phase.LIQUID.idx].spreadHorizontal();
        phases[Phase.GAS.idx].spreadHorizontal();
        phases[Phase.SUPERCRITICAL_FLUID.idx].spreadHorizontal();
    }

    public void spreadToLower(long amount_mol)
    {
        //Only gases can be spread
        PhaseMixture gases = phases[Phase.GAS.idx];
        double percentage = (double) amount_mol / (double) gases.getAmount_mol();
        percentage = Math.min(percentage, 1.0);
        gases.spreadToLower(percentage);
    }

    public void spreadToHigher(long amount_mol)
    {
        //Only allow gases to expand upwards
        PhaseMixture gases = phases[Phase.GAS.idx];
        double percentage = (double) amount_mol / (double) gases.getAmount_mol();
        percentage = Math.min(percentage, 1.0);
        gases.spreadToHigher(percentage);
    }

    public PhaseMixture getMixtureForPhase(Phase phase)
    {
        return getMixtureForPhase(phase.idx);
    }

    public PhaseMixture getMixtureForPhase(int phase)
    {
        return phases[phase];
    }

    public void update()
    {
        updateStats(pressure_kPa, Consts.toLong(layerVolume_L));
        updateCompoundPhases();
        rainAndHail();
    }

    private void updateCompoundPhases()
    {
        List<Compound> phaseChanged = null;
        for (PhaseMixture phase : phases)
        {
            if (phaseChanged == null)
            {
                phaseChanged = phase.getAndRemovePhaseChanged();
            } else
            {
                phaseChanged.addAll(phase.getAndRemovePhaseChanged());
            }
        }
        if(phaseChanged == null){
            System.out.println("Escapeing phasechange!");
            return;
        }
        for (Compound compound : phaseChanged)
        {
            System.out.println("Performing Phasechange! " + compound);
            phases[compound.getPhase().idx].add(compound);
        }
    }

    private void rainAndHail()
    {
        if (layerIdx == 0)
        {
            //No hail or rain from the lowest layer
            return;
        }
        phases[Phase.SOLID.idx].spreadToLower(1.0);
        phases[Phase.LIQUID.idx].spreadToLower(1.0);
    }

    public long molesOverVolume()
    {
        if (temperature_k == 0)
        {
            return 0;
        }
        long diff = volume_L - layerVolume_L;
        if (diff <= 0)
        {
            return 0;
        }
        return ChemUtilities.moles(Consts.STANDARD_PRESSURE_kPa, diff, temperature_k);
    }

    public long molesUnderVolume()
    {
        if (temperature_k == 0)
        {
            return 0;
        }
        long diff = layerVolume_L - volume_L;
        if (diff <= 0)
        {
            return 0;
        }
        return ChemUtilities.moles(Consts.STANDARD_PRESSURE_kPa, diff, temperature_k);
    }

    @Override
    public void updateStats(long externalPressure_kPa, long totalVolume_L)
    {
        clearStats();
        long temperatureSum = 0;
        for (PhaseMixture phaseMix : phases)
        {
            phaseMix.updateStats(externalPressure_kPa, totalVolume_L);
            amount_mol += phaseMix.getAmount_mol();
            energy_kj += phaseMix.getEnergy_kj();
            pressure_kPa += phaseMix.getPressure_kPa();
            volume_L += phaseMix.getVolume_L();
            heatCapacity_kj_K += phaseMix.getHeatCapacity_kj_K();
            temperatureSum += phaseMix.getTemperature_k();
        }
        temperature_k = temperatureSum / phases.length;
        if (pressure_kPa < 0)
        {
            throw new RuntimeException("negative Pressure!");
        }
    }

    public long addEnergy(long energy_kj)
    {
        long added = 0;
        for (PhaseMixture phase : phases)
        {
            double percentage = (double) phase.getHeatCapacity_kj_K() / (double) heatCapacity_kj_K;
            long energyToAdd = (long) (energy_kj * percentage);
            added -= phase.addEnergy(energyToAdd);
            added += energyToAdd;
        }
        return energy_kj - added;
    }

    public LayerMixture getHigher()
    {
        return higher;
    }

    public LayerMixture getLower()
    {
        return lower;
    }

    public boolean hasHigher()
    {
        return higher != null;
    }

    public boolean hasLower()
    {
        return lower != null;
    }

    public int getLayerIdx()
    {
        return layerIdx;
    }
}
