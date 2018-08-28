/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.compound;

import ch.schaermedia.ecovolution.environment.chem.AtmosphericEnity;
import ch.schaermedia.ecovolution.environment.chem.properties.CompoundProperties;
import ch.schaermedia.ecovolution.environment.world.World;
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

    public void spreadToHigher(double percentage)
    {
        spreadTo(higher, percentage);
    }

    public void spreadToLower(double percentage)
    {
        spreadTo(lower, percentage);
    }

    public void spreadHorizontal()
    {
        neighbours.forEach((neighbour) ->
        {
            spreadTo(neighbour, World.HORIZONTAL_SPREAD_PERCENTAGE);
        });
    }

    private long spreadTo(PhaseMixture spreadTo, double percentage)
    {
        if (spreadTo == null)
        {
            return 0;
        }
        long totalSpread = 0;
        for (Map.Entry<String, Compound> entry : composition.entrySet())
        {
            Compound other = spreadTo.getCompound(entry.getKey());
            Compound compound = entry.getValue();
            totalSpread += compound.splitTo(other, percentage);
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
    public void updateStats(long externalPressure_kPa, long totalVolume_L)
    {
        clearStats();
        phaseChanged.clear();
        if (composition.isEmpty())
        {
            return;
        }
        long temperatureSum = 0;
        for (Compound compound : composition.values())
        {
            compound.updateStats(externalPressure_kPa, totalVolume_L);
            if (compound.hasPhaseChanged())
            {
                phaseChanged.add(compound);
            }
            amount_mol += compound.getAmount_mol();
            energy_kj += compound.getEnergy_kj();
            pressure_kPa += compound.getPressure_kPa();
            volume_L += compound.getVolume_L();
            heatCapacity_kj_K += compound.getHeatCapacity_kj_K();
            temperatureSum += compound.getTemperature_k();
        }
        temperature_k = temperatureSum / composition.size();
        if (pressure_kPa < 0)
        {
            throw new RuntimeException("negative Pressure!");
        }
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
        getCompound(compound.getCode()).add(compound.getAmount_mol(), compound.getEnergy_kj());
    }

    public long addEnergy(long energy_kj)
    {
        long added = 0;
        for (Compound compound : composition.values())
        {
            double percentage = (double) compound.getHeatCapacity_kj_K() / (double) heatCapacity_kj_K;
            long energyToAdd = (long) (energy_kj * percentage);
            compound.add(0, energyToAdd);
            added += energyToAdd;
        }
        return energy_kj - added;
    }

}
