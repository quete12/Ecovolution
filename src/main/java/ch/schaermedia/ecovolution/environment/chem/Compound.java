/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

/**
 *
 * @author Quentin
 */
public class Compound {

    private final CompoundProperties properties;
    private double amount_mol = 0;
    private double amountBuffer_mol = 0;

    private double energy_kj = 0;
    private double energyBuffer_kj = 0;

    private double temperature_K = 0;
    private Phase phase;

    public Compound(CompoundProperties properties)
    {
        this.properties = properties;
        this.phase = Phase.SOLID;
    }

    public double volume_L(double pressure_kPa)
    {
        return ChemUtilities.volume_L(pressure_kPa, amount_mol, temperature_K);
    }

    public double pressure_kPa(double volume_L)
    {
        return ChemUtilities.pressure_kPa(volume_L, amount_mol, temperature_K);
    }

    public void addEnergy(double add_kj)
    {
        if (Double.isNaN(add_kj))
        {
            throw new RuntimeException("Adding NaN Energy");
        }
        if (add_kj < 0)
        {
            //TODO: add proper error handling
            //Maybe even allow removeing energy this way ??
            throw new RuntimeException("Negative Energy Add!");
        }
        energyBuffer_kj += add_kj;
    }

    public void addAmount(double add_mol)
    {
        if (Double.isNaN(add_mol))
        {
            throw new RuntimeException("Adding NaN Moles");
        }
        if (add_mol < 0)
        {
            //TODO: add proper error handling
            //Maybe even allow removeing moles this way ??
            throw new RuntimeException("Negative Moles Add!");
        }
        amountBuffer_mol += add_mol;
    }
    
    public void directImportCompound(Compound com){
        amount_mol += (com.getAmount_mol() + com.getAmountBuffer_mol());
        energy_kj += (com.getEnergy_kj() + com.getEnergyBuffer_kj());
    }

    public void importCompound(Compound com)
    {
        amountBuffer_mol += (com.getAmount_mol() + com.getAmountBuffer_mol());
        energyBuffer_kj += (com.getEnergy_kj() + com.getEnergyBuffer_kj());
    }

    public double splitDirectMoles(double percentage)
    {
        //maybe we should consider the buffer in this calculation even though it sould be 0 by the time this method gets called, just to avoid negative values
        double remove = amount_mol * percentage;
        amount_mol -= remove;
        return remove;
    }

    public double splitDirectEnergy(double percentage)
    {
        //maybe we should consider the buffer in this calculation even though it sould be 0 by the time this method gets called, just to avoid negative values
        double remove = energy_kj * percentage;
        energy_kj -= remove;
        return remove;
    }

    public double splitMoles(double percentage)
    {
        double remove = amount_mol * percentage;
        amountBuffer_mol -= remove;
        return remove;
    }

    public double splitEnergy(double percentage)
    {
        double remove = energy_kj * percentage;
        energyBuffer_kj -= remove;
        return remove;
    }

    public double removeAmount(double moles)
    {
        double tmp_amount = amount_mol + amountBuffer_mol - moles;
        if (tmp_amount < 0)
        {
            double diff = moles - (amount_mol + amountBuffer_mol);
            if (diff < 0)
            {
                return 0;
            }
            amountBuffer_mol -= diff;
            return diff;
        }
        return moles;
    }

    public void importBuffers()
    {
        this.amount_mol += amountBuffer_mol;
        this.energy_kj += energyBuffer_kj;
        amountBuffer_mol = 0;
        energyBuffer_kj = 0;
    }

    public void updateTemperatureAndPhase(double mixturePressure)
    {
        double energy_kj_mol = energy_kj / amount_mol;
        updatePhase(energy_kj_mol, mixturePressure);
        updateTemperature(energy_kj_mol, mixturePressure);
    }

    private void updatePhase(double energy_kj_mol, double mixturePressure)
    {
        phase = properties.getEnergy_Pressure_Diagram().phaseAt(energy_kj_mol, mixturePressure);
    }

    private void updateTemperature(double energy_kj_mol, double mixturePressure)
    {
        if (amount_mol == 0)
        {
            temperature_K = 0;
            return;
        }
        temperature_K = properties.getEnergy_Pressure_Diagram().temperatureAt(energy_kj_mol, mixturePressure, phase);
    }

    public double getAmount_mol()
    {
        return amount_mol;
    }

    public void setAmount_mol(double amount_mol)
    {
        this.amount_mol = amount_mol;
    }

    public CompoundProperties getProperties()
    {
        return properties;
    }

    public double getAmountBuffer_mol()
    {
        return amountBuffer_mol;
    }

    public double getEnergyBuffer_kj()
    {
        return energyBuffer_kj;
    }

    @Override
    public String toString()
    {
        return "Compound{" + "amount_mol=" + amount_mol + ", energy_j=" + energy_kj + '}';
    }

    public double getEnergy_kj()
    {
        return energy_kj;
    }

    public void setEnergy_kj(double energy_kj)
    {
        this.energy_kj = energy_kj;
    }

    public double getTemperature_K()
    {
        return temperature_K;
    }

    public void setTemperature_K(double temperature_K)
    {
        this.temperature_K = temperature_K;
    }

    public Phase getPhase()
    {
        return phase;
    }

    public void setPhase(Phase phase)
    {
        this.phase = phase;
    }

    public String getName()
    {
        return properties.getName();
    }

    public String getCode()
    {
        return properties.getCode();
    }

    public double getSpecificHeatCapacity()
    {
        return properties.getSpecificHeatCapacity_kj_mol_K();
    }

    public double getTotalHeatCapacity()
    {
        return properties.getSpecificHeatCapacity_kj_mol_K() * amount_mol;
    }
}
