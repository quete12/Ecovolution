/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.compound;

import ch.schaermedia.ecovolution.environment.chem.AtmosphericEnity;
import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.phasediagram.PhaseDiagram_Energy_Pressure;
import ch.schaermedia.ecovolution.environment.chem.properties.CompoundProperties;

/**
 *
 * @author Quentin
 */
public class Compound extends AtmosphericEnity {

    private final CompoundProperties properties;

    private long amount_mol_buffer;
    private long energy_kj_buffer;
    private Phase phase = Phase.SOLID;

    private boolean phaseChanged = false;

    public Compound(CompoundProperties properties)
    {
        this.properties = properties;
    }

    public long splitTo(Compound other, double percentage)
    {
        if (percentage < 0)
        {
            throw new RuntimeException("Negative percentage split!!");
        }
        long amountDiff = (long) (amount_mol * percentage);
        amount_mol_buffer -= amountDiff;
        long energyDiff = (long) (energy_kj * percentage);
        energy_kj_buffer -= energyDiff;
        other.add(amountDiff, energyDiff);
        return amountDiff;
    }

    public void add(long amount_mol, long energy_kj)
    {
        String error = "";
        if (amount_mol < 0)
        {
            error += "Trying to remove moles: " + amount_mol + "\n";
        }
        if (energy_kj < 0)
        {
            error += "Trying to remove energy: " + energy_kj + "\n";
        }
        if (!error.isEmpty())
        {
            throw new RuntimeException(error);
        }
        amount_mol_buffer += amount_mol;
        energy_kj_buffer += energy_kj;
    }

    @Override
    public void updateStats(long externalPressure_kPa, long totalVolume_L)
    {
        importBuffers();
        if (amount_mol > 0)
        {
            updateThermodynamicStats(externalPressure_kPa, totalVolume_L);
        }
    }

    @Override
    public void importBuffers()
    {
        //prev is for debugging purposes and should be removed once the Error with negatives is solved
        String prev = this.toString();
        amount_mol += amount_mol_buffer;
        energy_kj += energy_kj_buffer;
        if (amount_mol < 0 || energy_kj < 0)
        {
            System.out.println("Error when updating: " + prev + "\nto: " + this);
            throw new RuntimeException("Negative Moles or Energy!!");
        }
        amount_mol_buffer = 0;
        energy_kj_buffer = 0;
    }

    private void updateThermodynamicStats(long externalPressure_kPa, long totalVolume_L)
    {
        PhaseDiagram_Energy_Pressure diag = properties.getEnergy_Pressure_Diagram();
        Phase phaseAt = diag.getPhaseAt(energy_kj, externalPressure_kPa);
        phaseChanged = phaseAt.idx != phase.idx;
        phase = phaseAt;
        temperature_k = diag.getTemperature_K_at(energy_kj, externalPressure_kPa, phase);
        if (temperature_k < 0)
        {
            throw new RuntimeException("Negative temperature with: " + this + " temperature: " + temperature_k);
        }
        volume_L = ChemUtilities.volume_L(externalPressure_kPa, amount_mol, temperature_k);
        pressure_kPa = ChemUtilities.pressure_kPa(totalVolume_L, amount_mol, temperature_k);
    }

    public boolean hasPhaseChanged()
    {
        return phaseChanged;
    }

    public Phase getPhase()
    {
        return phase;
    }

    @Override
    public String toString()
    {
        return "Compound{" + "code=" + properties.getCode() + ", amount_mol_buffer=" + amount_mol_buffer + ", energy_kj_buffer=" + energy_kj_buffer + ", amount_mol=" + amount_mol + ", energy_kj=" + energy_kj + ", phase=" + phase + ", phaseChanged=" + phaseChanged + '}';
    }

}
