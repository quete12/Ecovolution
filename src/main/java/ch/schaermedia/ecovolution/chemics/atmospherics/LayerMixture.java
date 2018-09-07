/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.atmospherics;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.math.BigDouble;
import java.util.List;

/**
 *
 * @author Quentin
 */
public class LayerMixture extends AtmosphericEnity {

    private final PhaseMixture[] phases;
    private final BigDouble layerVolume_L;

    private LayerMixture higher;
    private LayerMixture lower;

    private BigDouble energyBuffer_kj = new BigDouble();

    private final int layerIdx;

    public LayerMixture(BigDouble layerVolume_L, int layerIdx)
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
        if (phases[Phase.SOLID.idx].pressure_kPa[internal].compareTo(ChemUtilities.STANDARD_PRESSURE_kPa.mul(10, 0)) > 0)
        {
            phases[Phase.SOLID.idx].spreadHorizontal();
        }
        phases[Phase.LIQUID.idx].spreadHorizontal();
        phases[Phase.GAS.idx].spreadHorizontal();
        phases[Phase.SUPERCRITICAL_FLUID.idx].spreadHorizontal();
    }

    public void spreadToLower(BigDouble amount_mol)
    {
        //Only gases can be spread
        PhaseMixture gases = phases[Phase.GAS.idx];
        if (gases.amount_mol[internal].isZero())
        {
            return;
        }
        if (gases.amount_mol[internal].isNegative())
        {
            throw new RuntimeException("Negative amount of Gases!!");
        }
        BigDouble percentage = amount_mol.div(gases.amount_mol[internal], new BigDouble());
        percentage.limitHigh(BigDouble.ONE);
        gases.spreadToLower(percentage);
    }

    public void spreadToHigher(BigDouble amount_mol)
    {
        //Only allow gases to expand upwards
        PhaseMixture gases = phases[Phase.GAS.idx];
        if (gases.amount_mol[internal].isZero())
        {
            return;
        }
        BigDouble percentage = amount_mol.div(gases.amount_mol[internal], new BigDouble());
        percentage.limitHigh(BigDouble.ONE);
        if (percentage.isNegative())
        {
            return;
        }
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
        updateStats(getPressure_kPa(), layerVolume_L);
        updateCompoundPhases();
        rainAndHail();
        swap();
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
        if (phaseChanged == null)
        {
            return;
        }
        for (Compound compound : phaseChanged)
        {
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
        phases[Phase.SOLID.idx].spreadToLower(BigDouble.ONE);
        phases[Phase.LIQUID.idx].spreadToLower(BigDouble.ONE);
    }

    public BigDouble molesOverVolume()
    {
        if (temperature_k[internal].isZero())
        {
            return new BigDouble();
        }
        BigDouble diff = volume_L[internal].sub(layerVolume_L, new BigDouble());
        if (diff.isNegative() || diff.isZero())
        {
            return new BigDouble();
        }
        return ChemUtilities.moles(ChemUtilities.STANDARD_PRESSURE_kPa, diff, temperature_k[internal]);
    }

    public BigDouble molesUnderVolume()
    {
        if (temperature_k[internal].isZero())
        {
            return new BigDouble();
        }
        BigDouble diff = layerVolume_L.sub(volume_L[internal], new BigDouble());
        if (diff.isNegative() || diff.isZero())
        {
            return new BigDouble();
        }
        return ChemUtilities.moles(ChemUtilities.STANDARD_PRESSURE_kPa, diff, temperature_k[internal]);
    }

    @Override
    public void updateStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L)
    {
        clearStats();
        for (PhaseMixture phaseMix : phases)
        {
            phaseMix.updateStats(externalPressure_kPa, totalVolume_L);
            amount_mol[internal].add(phaseMix.getAmount_mol());
            pressure_kPa[internal].add(phaseMix.getPressure_kPa());
            volume_L[internal].add(phaseMix.getVolume_L());
            heatCapacity_kj_K[internal].add(phaseMix.getHeatCapacity_kj_K());
            temperature_k[internal].add(phaseMix.getTemperature_k());
        }
        temperature_k[internal].div(new BigDouble(phases.length, 0));
        if (pressure_kPa[internal].isNegative())
        {
            throw new RuntimeException("negative Pressure!");
        }
        addEnergy();
    }

    private void addEnergy()
    {
        if (getHeatCapacity_kj_K().isZero())
        {
            return;
        }
        BigDouble added = new BigDouble();
        for (PhaseMixture phase : phases)
        {
            BigDouble percentage = phase.getHeatCapacity_kj_K().div(getHeatCapacity_kj_K(), new BigDouble());
            BigDouble energyToAdd = energyBuffer_kj.mul(percentage, new BigDouble());
            added.sub(phase.addEnergy(energyToAdd));
            added.add(energyToAdd);
        }
        energyBuffer_kj.sub(added);
    }

    public BigDouble percentageInPhase(Phase phase)
    {
        return phases[phase.idx].getAmount_mol().div(getAmount_mol(), new BigDouble());
    }

    public BigDouble[] getPhasePercentages(){
        BigDouble amountCopy = new BigDouble(getAmount_mol());
        if(amountCopy.isZero()){
            return null;
        }
        BigDouble[] results = new BigDouble[phases.length];
        for (int i = 0; i < phases.length; i++)
        {
            results[i] = phases[i].getAmount_mol().div(amountCopy, new BigDouble());
        }
        return results;
    }

    public void addEnergy(BigDouble energy_kj)
    {
        this.energyBuffer_kj.add(energy_kj);
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
