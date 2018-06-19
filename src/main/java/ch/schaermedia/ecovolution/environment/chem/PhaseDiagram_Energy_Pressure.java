/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import ch.schaermedia.ecovolution.general.math.LinearFunction;

/**
 *
 * @author Quentin
 */
public class PhaseDiagram_Energy_Pressure {

    private final ElementProperties properties;

    private LinearFunction sublimationMinEnergy;
    private LinearFunction sublimationMaxEnergy;
    private LinearFunction meltingMinEnergy;
    private LinearFunction meltingMaxEnergy;
    private LinearFunction vaporizationMinEnergy;
    private LinearFunction vaporizationMaxEnergy;

    public PhaseDiagram_Energy_Pressure(ElementProperties properties)
    {
        this.properties = properties;
        initFunctions();
    }

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

    public Phase phaseAt(double energy_kj_mol, double mixturePressure)
    {
        if (mixturePressure > properties.getCriticalPointPressure_kPa()
                && energy_kj_mol > criticalMinEnergy() + properties.getVaporizationHeat_kj())
        {
            return Phase.SUPERCRITICAL_FLUID;
        } else if (sublimationMaxEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure)
                && meltingMaxEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure))
        {
            return Phase.SOLID;
        } else if (!meltingMaxEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure)
                && vaporizationMaxEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure))
        {
            return Phase.LIQUID;
        } else
        {
            return Phase.GAS;
        }
    }

    public double temperatureAt(double energy_kj_mol, double mixturePressure)
    {
        return temperatureAt(energy_kj_mol, mixturePressure, phaseAt(energy_kj_mol, mixturePressure));
    }

    public double temperatureAt(double energy_kj_mol, double mixturePressure, Phase phase)
    {
        PhaseDiagram_Temperature_Pressure tPDia = properties.getTemperature_Pressure_Diagram();
        switch (phase)
        {
            case SOLID:
                if (sublimationMinEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure)
                        && meltingMinEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure))
                {
                    return energy_kj_mol / properties.getSpecificHeatCapacity_kj_mol_K();
                } else if (mixturePressure > properties.getTriplePointPressure_kPa())
                {
                    return tPDia.getMeltingBorder().x(mixturePressure);
                } else
                {
                    return tPDia.getSublimationBorder().x(mixturePressure);
                }
            case LIQUID:
                if (vaporizationMinEnergy.isPointLeftOrOn(energy_kj_mol, mixturePressure))
                {
                    return (energy_kj_mol - properties.getFusionHeat_kj()) / properties.getSpecificHeatCapacity_kj_mol_K();
                } else
                {
                    return tPDia.getVaporizationBorder().x(mixturePressure);
                }
            case GAS:
            case SUPERCRITICAL_FLUID:
                return (energy_kj_mol - properties.getFusionHeat_kj() - properties.getVaporizationHeat_kj()) / properties.getSpecificHeatCapacity_kj_mol_K();
            default:
                throw new AssertionError(phase.name());
        }
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
