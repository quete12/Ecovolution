/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import static ch.schaermedia.ecovolution.environment.chem.CompoundMix.STATIC_PRESSURE_kPa;
import static ch.schaermedia.ecovolution.environment.chem.CompoundMix.STATIC_VOLUME_L;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Quentin
 */
public class PhaseMix {

    private final Phase phase;

    private double heatCapacitySum;
    private double amount_mol;
    private double volume_L;
    private double pressure_kPa;

    private double temperature_K;

    private final int horizontalSpreadSize;
    private final Map<String, Compound> composition;
    private final List<PhaseMix> neighbours;

    public PhaseMix(Phase phase, int horizontalSpreadSize)
    {
        this.phase = phase;
        this.horizontalSpreadSize = horizontalSpreadSize;
        this.composition = new HashMap<>();
        this.neighbours = new ArrayList<>(horizontalSpreadSize);
    }

    public int numberOfCompounds()
    {
        return composition.size();
    }

    public List<Compound> getAndRemoveCompoundsOnPhaseChange()
    {
        List<Compound> changed = new ArrayList();
        for (Compound compound : composition.values())
        {
            if (compound.getPhase() == phase)
            {
                continue;
            }
            changed.add(compound);
        }
        for (Compound changedCompound : changed)
        {
            composition.remove(changedCompound.getCode());
        }
        return changed;
    }

    public void importCompounds(List<Compound> compounds)
    {
        for (Compound compound : compounds)
        {
            importCompound(compound);
        }
    }

    public void importCompound(Compound compound)
    {
        if (composition.containsKey(compound.getCode()))
        {
            Compound existing = composition.get(compound.getCode());
            existing.importCompound(compound);
        } else
        {
            composition.put(compound.getCode(), compound);
        }
    }

    public void updateTemperatureAndPhase(double totalPressure_kPa)
    {
        double temperatureSum = 0;
        temperatureSum = composition.values().stream().map((compound) ->
        {
            compound.updateTemperatureAndPhase(totalPressure_kPa);
            return compound;
        }).map((compound) -> compound.getTemperature_K()).reduce(temperatureSum, (accumulator, _item) -> accumulator + _item);
        temperature_K = (!composition.isEmpty()) ? temperatureSum / composition.size() : 0;
    }

    /**
     * Updates the interal stats.
     */
    public void updateStats()
    {
        amount_mol = 0;
        heatCapacitySum = 0;
        volume_L = 0;
        pressure_kPa = 0;
        for (Compound compound : composition.values())
        {
            compound.importBuffers();
            amount_mol += compound.getAmount_mol();
            heatCapacitySum += compound.getTotalHeatCapacity();

            volume_L += compound.volume_L(STATIC_PRESSURE_kPa);
            pressure_kPa += compound.pressure_kPa(STATIC_VOLUME_L);
        }
    }

    public void add(String code, double amount_mol, double energy_kj)
    {
        Compound compound = composition.get(code);
        if (compound == null)
        {
            compound = new Compound(CompoundProperties.getPropertiesFromCode(code));
            composition.put(code, compound);
        }
        compound.addAmount(amount_mol);
        compound.addEnergy(energy_kj);
    }

    public void addEnergy(double energy_kj)
    {
        for (Compound compound : composition.values())
        {
            if (compound == null)
            {
                continue;
            }
            double percent = compound.getTotalHeatCapacity() / heatCapacitySum;
            compound.addEnergy(energy_kj * percent);
        }
    }

    public void spreadPercentage(PhaseMix spreadTo, double precentage)
    {
        composition.values().forEach((value) ->
        {
            double splitEnergy = value.splitEnergy(precentage);
            double splitMoles = value.splitMoles(precentage);
            spreadTo.add(value.getCode(), splitMoles, splitEnergy);
        });
    }

    public void spreadHorizontal()
    {
        double percentage = 1.0 / horizontalSpreadSize;
        for (PhaseMix neighbour : neighbours)
        {
            spreadPercentage(neighbour, percentage);
        }
    }

    public Compound getCompound(String code)
    {
        return composition.get(code);
    }

    public Phase getPhase()
    {
        return phase;
    }

    public double getHeatCapacitySum()
    {
        return heatCapacitySum;
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

    public void addAsNeighbour(PhaseMix phaseMix)
    {
        this.neighbours.add(phaseMix);
    }
}
