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
import ch.schaermedia.ecovolution.general.math.BigDouble;

/**
 *
 * @author Quentin
 */
public class Compound extends AtmosphericEnity {

    private final CompoundProperties properties;

    private BigDouble amount_mol_buffer = new BigDouble();
    private BigDouble energy_kj_buffer = new BigDouble();
    private Phase phase = Phase.SOLID;

    private boolean phaseChanged = false;

    public Compound(CompoundProperties properties)
    {
        this.properties = properties;
    }

    public Compound(CompoundProperties properties, Phase phase)
    {
        this.properties = properties;
        this.phase = phase;
    }

    public BigDouble splitTo(Compound other, BigDouble percentage)
    {
        if (percentage.isNegative())
        {
            throw new RuntimeException("Negative percentage split!!");
        }
        BigDouble amountDiff = amount_mol.mul(percentage, new BigDouble());
        amount_mol_buffer.sub(amountDiff);
        BigDouble energyDiff = energy_kj.mul(percentage, new BigDouble());
        energy_kj_buffer.sub(energyDiff);
        other.add(amountDiff, energyDiff);
        return amountDiff;
    }

    public void add(BigDouble amount_mol, BigDouble energy_kj)
    {
        amount_mol_buffer.add(amount_mol);
        energy_kj_buffer.add(energy_kj);
    }

    @Override
    public void updateStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L)
    {
        importBuffers();
        amount_mol.mul(properties.getSpecificHeatCapacity_kj_mol_K(), heatCapacity_kj_K);
        if (amount_mol.isPositive())
        {
            updateThermodynamicStats(externalPressure_kPa, totalVolume_L);
        }
    }

    public void importBuffers()
    {
        amount_mol.add(amount_mol_buffer);
        energy_kj.add(energy_kj_buffer);
        amount_mol_buffer.clear();
        energy_kj_buffer.clear();
    }

    private void updateThermodynamicStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L)
    {
        PhaseDiagram_Energy_Pressure diag = properties.getEnergy_Pressure_Diagram();
        BigDouble energyPerMol = energy_kj.div(amount_mol, new BigDouble());
        System.out.println("Energy per mol: " + energyPerMol.toDoubleString() + " external Pressure: " + externalPressure_kPa + " total Volume: " + totalVolume_L);
        Phase phaseAt = diag.getPhaseAt(energyPerMol, externalPressure_kPa);
        phaseChanged = phaseAt.idx != phase.idx;
        phase = phaseAt;
        temperature_k = diag.getTemperature_K_at(energyPerMol, externalPressure_kPa, phase);
        if (temperature_k.isNegative())
        {
            throw new RuntimeException("Negative temperature with: " + this + " temperature: " + temperature_k);
        }
        ChemUtilities.volume_L(externalPressure_kPa, amount_mol, temperature_k, volume_L);
        ChemUtilities.pressure_kPa(totalVolume_L, amount_mol, temperature_k, pressure_kPa);
        if (pressure_kPa.isNegative())
        {
            throw new RuntimeException("negative Pressure! Calculated from volume: " + totalVolume_L + " moles: " + amount_mol + " temperature: " + temperature_k + " phase: " + phase);
        }
    }

    public boolean hasPhaseChanged()
    {
        return phaseChanged;
    }

    public Phase getPhase()
    {
        return phase;
    }

    public String getCode()
    {
        return properties.getCode();
    }

    @Override
    public String toString()
    {
        return "Compound{" + "code=" + properties.getCode() + ", amount_mol_buffer=" + amount_mol_buffer + ", energy_kj_buffer=" + energy_kj_buffer + ", amount_mol=" + amount_mol + ", energy_kj=" + energy_kj + ", phase=" + phase + ", phaseChanged=" + phaseChanged + '}';
    }

}
