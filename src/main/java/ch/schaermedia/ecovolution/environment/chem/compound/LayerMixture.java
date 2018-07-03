/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.compound;

import ch.schaermedia.ecovolution.environment.chem.AtmosphericEnity;

/**
 *
 * @author Quentin
 */
public class LayerMixture extends AtmosphericEnity {

    private final PhaseMixture[] phases;

    public LayerMixture()
    {
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
        for (int i = 0; i < phases.length; i++)
        {
            phases[i].setHigher(higher.getMixtureForPhase(i));
        }
    }

    public void setLower(LayerMixture lower)
    {
        for (int i = 0; i < phases.length; i++)
        {
            phases[i].setLower(lower.getMixtureForPhase(i));
        }
    }

    public void addAsNeighbour(LayerMixture neighbour){
        for (int i = 0; i < phases.length; i++)
        {
            phases[i].addAsNeighbour(neighbour.getMixtureForPhase(i));
        }
    }

    public PhaseMixture getMixtureForPhase(Phase phase){
        return getMixtureForPhase(phase.idx);
    }
    public PhaseMixture getMixtureForPhase(int phase){
        return phases[phase];
    }

    public void update(long totalVolume_L)
    {
        updateStats(pressure_kPa,totalVolume_L);
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
}
