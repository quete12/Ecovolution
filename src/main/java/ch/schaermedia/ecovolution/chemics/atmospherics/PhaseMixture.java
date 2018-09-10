/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.atmospherics;

import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.world.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Quentin
 */
public class PhaseMixture extends AtmosphericEnity {

    private final Map<String, Compound> composition;
    private final int phase;
    private PhaseMixture higher;
    private PhaseMixture lower;
    private List<PhaseMixture> neighbours;
    private List<Compound> phaseChanged;

    public PhaseMixture(int phase)
    {
        this.phase = phase;
        composition = new HashMap<>();
        neighbours = new ArrayList();
        phaseChanged = new ArrayList();
    }

    public void spreadToHigher(BigDouble percentage)
    {
        spreadTo(higher, percentage);
    }

    public void spreadToLower(BigDouble percentage)
    {
        spreadTo(lower, percentage);
    }

    public void spreadHorizontal()
    {
        neighbours.forEach((neighbour) ->
        {
            spreadTo(neighbour, World.NEIGHBOUR_SPREAD_PERCENTAGE);
        });
    }

    private BigDouble spreadTo(PhaseMixture spreadTo, BigDouble percentage)
    {
        if (spreadTo == null)
        {
            return new BigDouble();
        }
        BigDouble totalSpread = new BigDouble();
        for (Map.Entry<String, Compound> entry : composition.entrySet())
        {
            Compound other = spreadTo.getCompound(entry.getKey());
            Compound compound = entry.getValue();
            totalSpread.add(compound.splitTo(other, percentage));
        }
        return totalSpread;
    }

    /**
     *
     * @param code
     * @return the compound for the given code if present, else a new Compound
     * is created and linked with this phaseMixture.
     */
    public Compound getCompound(String code)
    {
        Compound result;
        if (composition.containsKey(code))
        {
            result = composition.get(code);
        } else
        {
            result = new Compound(CompoundProperties.getPropertiesFromCode(code), Phase.fromIdx(phase));
            composition.put(code, result);
        }
        return result;
    }

    public List<Compound> getCompounds()
    {
        return new ArrayList<>(composition.values());
    }

    public void setHigher(PhaseMixture higher)
    {
        this.higher = higher;
    }

    public void setLower(PhaseMixture lower)
    {
        this.lower = lower;
    }

    public void addAsNeighbour(PhaseMixture neighbour)
    {
        neighbours.add(neighbour);
    }

    public int getPhase()
    {
        return phase;
    }

    @Override
    public void updateStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L)
    {
        clearStats();
        phaseChanged.clear();
        if (composition.isEmpty())
        {
            return;
        }
        for (Compound compound : composition.values())
        {
            compound.updateStats(externalPressure_kPa, totalVolume_L);
            if (compound.hasPhaseChanged())
            {
                phaseChanged.add(compound);
            }
            amount_mol.add(compound.getAmount_mol());
            pressure_kPa[internal].add(compound.getPressure_kPa());
            volume_L[internal].add(compound.getVolume_L());
            heatCapacity_kj_K[internal].add(compound.getHeatCapacity_kj_K());
            temperature_k[internal].add(compound.getTemperature_k());
        }
        temperature_k[internal].div(new BigDouble(composition.size(), 0));
        if (pressure_kPa[internal].isNegative())
        {
            throw new RuntimeException("negative Pressure!");
        }
        swap();
    }

    private void removeCompound(Compound comp)
    {
        composition.remove(comp.getCode());
    }

    public List<Compound> getAndRemovePhaseChanged()
    {
        phaseChanged.forEach((compound) ->
        {
            removeCompound(compound);
        });
        return phaseChanged;
    }

    public void add(Compound compound)
    {
        getCompound(compound.getCode()).add(compound.getAmount_mol(),compound.getEnergy_kj());
    }

    public BigDouble addEnergy(BigDouble energy_kj)
    {
        if (getHeatCapacity_kj_K().isZero())
        {
            return new BigDouble();
        }
        BigDouble added = new BigDouble();
        for (Compound compound : composition.values())
        {
            BigDouble percentage = compound.getHeatCapacity_kj_K().div(getHeatCapacity_kj_K(), new BigDouble());
            BigDouble energyToAdd = energy_kj.mul(percentage, new BigDouble());
            compound.add(new BigDouble(),energyToAdd);
            added.add(energyToAdd);
        }
        return energy_kj.sub(added, added);
    }

    public void spreadEnergy(BigDouble energyToSpread){
        for (Compound compound : composition.values())
        {
            compound.spreadEnergy(energyToSpread);
        }
    }

}
