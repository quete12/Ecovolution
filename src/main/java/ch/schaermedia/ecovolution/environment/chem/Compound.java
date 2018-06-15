/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import ch.schaermedia.ecovolution.general.LinearFunction;

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
        initFunctions();
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

    public double energy_kj_AtTeperature(double targetTemperature_K)
    {
        double energy = 0;
        if (targetTemperature_K > properties.getMeltingPoint_K())
        {
            energy += (properties.getFusionHeat_kj() * amount_mol);
        }
        if (targetTemperature_K > properties.getBoilingPoint_K())
        {
            energy += (properties.getVaporizationHeat_kj() * amount_mol);
        }
        energy += (targetTemperature_K * properties.specificHeatCapacity_kj_mol_K * amount_mol);
        return energy;
    }

    /**
     *
     * @param targetTemperature_K
     * @return the amount of energy to heat up this compount from 0 Kelvin to
     * given temperature.
     */
    public double maxEnergy_kj_AtTeperature(double targetTemperature_K)
    {
        double energy = 0;
        if (targetTemperature_K >= properties.getMeltingPoint_K())
        {
            energy += (properties.getFusionHeat_kj() * amount_mol);
        }
        if (targetTemperature_K >= properties.getBoilingPoint_K())
        {
            energy += (properties.getVaporizationHeat_kj() * amount_mol);
        }
        energy += (targetTemperature_K * properties.specificHeatCapacity_kj_mol_K * amount_mol);
        return energy;
    }
    private LinearFunction sublimationMinEnergy;
    private LinearFunction sublimationMaxEnergy;
    private LinearFunction meltingMinEnergy;
    private LinearFunction meltingMaxEnergy;
    private LinearFunction vaporizationMinEnergy;
    private LinearFunction vaporizationMaxEnergy;

    private void initFunctions()
    {
        sublimationMinEnergy = new LinearFunction(
                0,
                0,
                triplePointMinEnergy(),
                properties.getTriplePointPressure_kPa());
        double sublimationEnergy = properties.getFusionHeat_kj() + properties.getVaporizationHeat_kj();
        sublimationMaxEnergy = sublimationMinEnergy.shiftRight(sublimationEnergy);

        meltingMinEnergy = new LinearFunction(
                energyAtMeltingPoint(),
                CompoundMix.STATIC_PRESSURE_kPa,
                triplePointMinEnergy(),
                properties.getTriplePointPressure_kPa());
        meltingMaxEnergy = meltingMinEnergy.shiftRight(properties.getFusionHeat_kj());

        vaporizationMinEnergy = new LinearFunction(
                triplePointMinEnergy(),
                properties.getTriplePointPressure_kPa(),
                criticalMinEnergy(),
                properties.getCriticalPointPressure_kPa());
        vaporizationMaxEnergy = vaporizationMinEnergy.shiftRight(properties.getVaporizationHeat_kj());
    }

    public void updateTemperatureAndPhase(double mixturePressure)
    {
        updatePhase(mixturePressure);
        updateTemperature(mixturePressure);
    }

    private void updateTemperature(double mixturePressure)
    {
        if(amount_mol == 0){
            temperature_K = 0;
            return;
        }
        double energy_kj_mol = energy_kj / amount_mol;
        switch (phase)
        {
            case SOLID:
                if (sublimationMinEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure)
                        && meltingMinEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure))
                {
                    temperature_K = (energy_kj / amount_mol) / properties.getSpecificHeatCapacity_kj_mol_K();
                } else if (mixturePressure > properties.getTriplePointPressure_kPa())
                {
                    temperature_K = properties.getMeltingBorder().x(mixturePressure);
                } else
                {
                    temperature_K = properties.getSublimationBorder().x(mixturePressure);
                }
                break;
            case LIQUID:
                if (vaporizationMinEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure))
                {
                    temperature_K = ((energy_kj / amount_mol) - properties.getFusionHeat_kj()) / properties.getSpecificHeatCapacity_kj_mol_K();
                } else
                {
                    temperature_K = properties.getVaporizationBorder().x(mixturePressure);
                }
                break;
            case GAS:
            case SUPERCRITICAL_FLUID:
                temperature_K = ((energy_kj / amount_mol) - properties.getFusionHeat_kj() - properties.getVaporizationHeat_kj()) / properties.getSpecificHeatCapacity_kj_mol_K();
                break;
            default:
                throw new AssertionError(phase.name());
        }
    }

    private void updatePhase(double mixturePressure)
    {
        double energy_kj_mol = energy_kj / amount_mol;
        if (mixturePressure > properties.getCriticalPointPressure_kPa()
                && energy_kj_mol > criticalMinEnergy() + properties.getVaporizationHeat_kj())
        {
            phase = Phase.SUPERCRITICAL_FLUID;
        } else if (sublimationMaxEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure)
                && meltingMaxEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure))
        {
            phase = Phase.SOLID;
        } else if (!meltingMaxEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure)
                && vaporizationMaxEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure))
        {
            phase = Phase.LIQUID;
        } else
        {
            phase = Phase.GAS;
        }
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

    private double energyAtMeltingPoint()
    {
        return properties.getMeltingPoint_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private double triplePointMinEnergy()
    {
        return properties.getTriplePointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private double criticalMinEnergy()
    {
        return energyAtMeltingPoint()
                + properties.getFusionHeat_kj()
                + properties.getCriticalPointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }
}
