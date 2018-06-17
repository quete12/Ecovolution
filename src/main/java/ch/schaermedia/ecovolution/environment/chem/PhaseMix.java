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

    private final Map<String, Compound> composition;

    private PhaseMix higher;
    private PhaseMix lower;

    private List<PhaseMix> neighbours;

    public PhaseMix(Phase phase)
    {
        this.phase = phase;
        this.composition = new HashMap<>();
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
        //TODO: check if compound really belongs to this Phase
        if (composition.containsKey(compound.getCode()))
        {
            Compound existing = composition.get(compound.getCode());
            existing.directImportCompound(compound);
        } else
        {
            composition.put(compound.getCode(), compound);
        }
    }

    public void updateTemperature(double totalPressure_kPa)
    {
        double temperatureSum = 0;
        for (Compound compound : composition.values())
        {
            compound.updateTemperatureAndPhase(totalPressure_kPa);
            temperatureSum += compound.getTemperature_K();
        }
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

    public boolean hasHigher()
    {
        return higher != null;
    }

    public boolean hasLower()
    {
        return lower != null;
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

    public void setHigher(PhaseMix higher)
    {
        this.higher = higher;
    }

    public void setLower(PhaseMix lower)
    {
        this.lower = lower;
    }

    public void addAsNeighbour(PhaseMix phaseMix){
        this.neighbours.add(this);
    }
}
