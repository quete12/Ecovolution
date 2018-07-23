/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.compound;

import ch.schaermedia.ecovolution.environment.chem.AtmosphericEnity;
import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.general.math.Consts;

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
        for (PhaseMixture phase : phases)
        {
            phase.spreadHorizontal();
        }
    }

    public void spreadToLower(int phase, double percentage)
    {
        phases[phase].spreadToLower(percentage);
    }

    public void spreadToHigher(int phase, double percentage)
    {
        phases[phase].spreadToHigher(percentage);
    }

    public void spreadToLower(double percentage)
    {
        for (PhaseMixture phase : phases)
        {
            phase.spreadToLower(percentage);
        }
    }

    public void spreadToHigher(double percentage)
    {
        for (PhaseMixture phase : phases)
        {
            phase.spreadToHigher(percentage);
        }
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
        updateStats(pressure_kPa, layerVolume_L);
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
            temperatureSum += phaseMix.getTemperature_k();
        }
        temperature_k = temperatureSum / phases.length;
    }

    @Override
    public void importBuffers()
    {
        for (PhaseMixture phase : phases)
        {
            phase.importBuffers();
        }
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
