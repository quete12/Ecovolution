/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.compound;

import ch.schaermedia.ecovolution.environment.chem.AtmosphericEnity;
import ch.schaermedia.ecovolution.environment.chem.properties.CompoundProperties;
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

    public PhaseMixture(int phase)
    {
        this.phase = phase;
        composition = new HashMap<>();
        neighbours = new ArrayList();
    }

    public long spreadToHigher(double percentage, long target)
    {
        return spreadToHigher(percentage, target, true);
    }

    public long spreadToHigher(double percentage)
    {
        return spreadToHigher(percentage, -1, false);
    }

    private long spreadToHigher(double percentage, long target, boolean limitToTarget)
    {
        if (higher == null)
        {
            return 0;
        }
        long totalSpread = 0;
        for (Map.Entry<String, Compound> entry : composition.entrySet())
        {
            Compound other = higher.getCompound(entry.getKey());
            Compound compound = entry.getValue();
            totalSpread += compound.splitTo(other, percentage);
            if (limitToTarget && totalSpread >= target)
            {
                break;
            }
        }
        return totalSpread;
    }

    public Compound getCompound(String code)
    {
        Compound result;
        if (composition.containsKey(code))
        {
            result = composition.get(code);
        } else
        {
            result = new Compound(CompoundProperties.getPropertiesFromCode(code));
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
        long temperatureSum = 0;
        for (Compound compound : composition.values())
        {
            compound.updateStats(externalPressure_kPa, totalVolume_L);
            amount_mol += compound.getAmount_mol();
            energy_kj += compound.getEnergy_kj();
            pressure_kPa += compound.getPressure_kPa();
            volume_L += compound.getVolume_L();
            temperatureSum += compound.getTemperature_k();
        }
        temperature_k = temperatureSum / composition.size();
    }
}
