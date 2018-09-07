/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.atmospherics;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.phasediagram.PhaseDiagram_Energy_Pressure;
import ch.schaermedia.ecovolution.math.BigDouble;

/**
 *
 * @author Quentin
 */
public class Compound extends AtmosphericEnity {

    private final CompoundProperties properties;

    private BigDouble amount_mol_buffer = new BigDouble();
    private BigDouble energy_kj_buffer = new BigDouble();

    private BigDouble energy_kj = new BigDouble();
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
            throw new RuntimeException("Negative percentage split!!\nFrom: " + this + " \nTo: " + other + "\nPercentage: " + percentage.toDoubleString());
        }
        BigDouble amountDiff = amount_mol[internal].mul(percentage, new BigDouble());
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
        if (amount_mol[internal].isPositive())
        {
            amount_mol[internal].mul(properties.getSpecificHeatCapacity_kj_mol_K(), heatCapacity_kj_K[internal]);
            updateThermodynamicStats(externalPressure_kPa, totalVolume_L);
        }
    }

    public void importBuffers()
    {
        amount_mol[internal].add(amount_mol_buffer);
        energy_kj.add(energy_kj_buffer);
        amount_mol_buffer.clear();
        energy_kj_buffer.clear();
    }

    private void updateThermodynamicStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L)
    {
        PhaseDiagram_Energy_Pressure diag = properties.getEnergy_Pressure_Diagram();
        BigDouble energyPerMol = energy_kj.div(amount_mol[internal], new BigDouble());
        Phase phaseAt = diag.getPhaseAt(energyPerMol, externalPressure_kPa);
        phaseChanged = phaseAt.idx != phase.idx;
        phase = phaseAt;
        temperature_k[internal]=diag.getTemperature_K_at(energyPerMol, externalPressure_kPa, phase);
        if (temperature_k[internal].isNegative())
        {
            throw new RuntimeException("Negative temperature with: " + this
                    + "\ntemperature: " + temperature_k[internal]
                    +"\nexternamPressure: " + externalPressure_kPa.toDoubleString()
                    +"\ntotalVolume: " + totalVolume_L.toDoubleString());
        }
        ChemUtilities.volume_L(externalPressure_kPa, amount_mol[internal], temperature_k[internal], volume_L[internal]);
        ChemUtilities.pressure_kPa(totalVolume_L, amount_mol[internal], temperature_k[internal], pressure_kPa[internal]);
        if (pressure_kPa[internal].isNegative())
        {
            throw new RuntimeException("negative Pressure! Calculated from volume: " + totalVolume_L + " moles: " + amount_mol[internal] + " temperature: " + temperature_k[internal] + " phase: " + phase);
        }
        swap();
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

    public BigDouble getEnergy_kj()
    {
        return energy_kj;
    }

    @Override
    public String toString()
    {
        return "Compound{" + "code=" + properties.getCode() + ", amount_mol_buffer=" + amount_mol_buffer + ", energy_kj_buffer=" + energy_kj_buffer + ", amount_mol=" + amount_mol + ", energy_kj=" + energy_kj + ", phase=" + phase + ", phaseChanged=" + phaseChanged + '}';
    }

}