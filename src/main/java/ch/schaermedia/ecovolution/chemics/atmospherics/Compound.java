/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.atmospherics;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.phasediagram.PhaseDiagram;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.world.World;

/**
 *
 * @author Quentin
 */
public class Compound extends AtmosphericEnity {

    private static final BigDouble ENERGY_SPREAD_PERCENTAGE = new BigDouble(0.02).setImmutable();

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

    public void init(BigDouble amount, BigDouble temperature_k, Phase phase)
    {
        BigDouble energyForTemp = properties.getSpecificHeatCapacity_kj_mol_K().mul(temperature_k, new BigDouble());
        switch (phase)
        {
            case SUPERCRITICAL_FLUID:
            case GAS:
                energyForTemp.add(properties.getVaporizationHeat_kj());
                energyForTemp.add(properties.getFusionHeat_kj());
                break;
            case LIQUID:
                energyForTemp.add(properties.getFusionHeat_kj());
                break;
            case SOLID:
                break;
            default:
                throw new AssertionError(phase.name());
        }
        energyForTemp.mul(amount);
        add(amount, energyForTemp);
        importBuffers();
        updateThermodynamicStats(ChemUtilities.STANDARD_PRESSURE_kPa, Tile.LAYER_VOLUME_L);
    }

    public BigDouble splitTo(Compound other, BigDouble percentage)
    {
        if (percentage.isNegative())
        {
            throw new RuntimeException("Negative percentage split!!\nFrom: " + this + " \nTo: " + other + "\nPercentage: " + percentage.toDoubleString());
        }
        if (energy_kj.isNegative() || amount_mol.isNegative())
        {
            throw new RuntimeException("Negative energy or moles whileSplitting!"
                    + "\n" + this
                    + "\nLast Spread: " + World.updateState
                    + "\nAmount: " + amount_mol + " buffer: " + amount_mol_buffer
                    + "\nEnergy: " + energy_kj + " buffer: " + energy_kj_buffer);
        }
        BigDouble amountDiff = amount_mol.mul(percentage, new BigDouble());
        amount_mol_buffer.sub(amountDiff);
        BigDouble energyDiff = energy_kj.mul(percentage, new BigDouble());
        energy_kj_buffer.sub(energyDiff);
        other.add(amountDiff, energyDiff);
        return amountDiff;
    }

    public void spreadEnergy(BigDouble energyToSpread)
    {
        BigDouble added;
//        if (amount_mol.isZero())
//        {
//            added = new BigDouble(energy_kj);
//        } else
//        {
//            added = energy_kj.mul(ENERGY_SPREAD_PERCENTAGE, new BigDouble());
//        }
        added = energy_kj.mul(ENERGY_SPREAD_PERCENTAGE, new BigDouble());
        energy_kj_buffer.sub(added);
        energyToSpread.add(added);
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
        if (amount_mol.isPositive())
        {
            amount_mol.mul(properties.getSpecificHeatCapacity_kj_mol_K(), heatCapacity_kj_K[internal]);
            updateThermodynamicStats(externalPressure_kPa, totalVolume_L);
        }
        swap();
    }

    public void importBuffers()
    {
        amount_mol.add(amount_mol_buffer);
        energy_kj.add(energy_kj_buffer);
        if (energy_kj.isNegative() || amount_mol.isNegative())
        {
            throw new RuntimeException("Negative energy or moles after import!"
                    + "\n" + this
                    + "\nLast Spread: " + World.updateState
                    + "\nAmount: " + amount_mol + " buffer: " + amount_mol_buffer
                    + "\nEnergy: " + energy_kj + " buffer: " + energy_kj_buffer);
        }
        amount_mol_buffer.clear();
        energy_kj_buffer.clear();
    }

    private void updateThermodynamicStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L)
    {
        PhaseDiagram diag = properties.getPhaseDiagram();
        BigDouble energyPerMol = energy_kj.div(amount_mol, new BigDouble());
        Phase phaseAt = diag.getPhase(energyPerMol, externalPressure_kPa);
        phaseChanged = phaseAt.idx != phase.idx;
        phase = phaseAt;
        temperature_k[internal] = diag.getTemperature(energyPerMol, externalPressure_kPa, phase);
        if (temperature_k[internal].isNegative())
        {
            throw new RuntimeException("Negative temperature with: " + this
                    + "\ntemperature: " + temperature_k[internal]
                    + "\nexternalPressure: " + externalPressure_kPa.toDoubleString()
                    + "\ntotalVolume: " + totalVolume_L.toDoubleString());
        }
        ChemUtilities.volume_L(externalPressure_kPa, amount_mol, temperature_k[internal], volume_L[internal]);
        ChemUtilities.pressure_kPa(totalVolume_L, amount_mol, temperature_k[internal], pressure_kPa[internal]);
        if (pressure_kPa[internal].isNegative())
        {
            throw new RuntimeException("negative Pressure! Calculated from volume: " + totalVolume_L + " moles: " + amount_mol + " temperature: " + temperature_k[internal] + " phase: " + phase);
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
