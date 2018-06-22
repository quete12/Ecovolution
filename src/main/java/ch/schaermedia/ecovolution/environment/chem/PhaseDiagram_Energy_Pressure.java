/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import ch.schaermedia.ecovolution.general.math.Function;
import ch.schaermedia.ecovolution.general.math.LinearFunction;
import ch.schaermedia.ecovolution.general.math.QuadraticFunction;

/**
 *
 * @author Quentin
 */
public class PhaseDiagram_Energy_Pressure {

    private final ElementProperties properties;

    private Function sublimationMinEnergy;
    private Function sublimationMaxEnergy;
    private Function meltingMinEnergy;
    private Function meltingMaxEnergy;
    private Function vaporizationMinEnergy;
    private Function vaporizationMaxEnergy;

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
        sublimationMaxEnergy = new LinearFunction(
                sublimationEnergy,
                0,
                triplePointMinEnergy() + sublimationEnergy,
                properties.getTriplePointPressure_kPa());

        meltingMinEnergy = new LinearFunction(
                energyAtMeltingPoint(),
                CompoundMix.STATIC_PRESSURE_kPa,
                triplePointMinEnergy(),
                properties.getTriplePointPressure_kPa());
        meltingMaxEnergy = new LinearFunction(
                energyAtMeltingPoint() + properties.getFusionHeat_kj(),
                CompoundMix.STATIC_PRESSURE_kPa,
                triplePointMinEnergy() + properties.getFusionHeat_kj(),
                properties.getTriplePointPressure_kPa());

        vaporizationMinEnergy = new QuadraticFunction(
                triplePointMinEnergy(),
                properties.getTriplePointPressure_kPa(),
                boilingPointMinEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                criticalMinEnergy(),
                properties.getCriticalPointPressure_kPa());
        vaporizationMaxEnergy = new QuadraticFunction(
                triplePointMinEnergy() + properties.getVaporizationHeat_kj(),
                properties.getTriplePointPressure_kPa(),
                boilingPointMaxEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                criticalMinEnergy() + properties.getVaporizationHeat_kj(),
                properties.getCriticalPointPressure_kPa());
    }

    public Phase phaseAt(double energy_kj_mol, double mixturePressure)
    {
        if (mixturePressure > properties.getCriticalPointPressure_kPa()
                && energy_kj_mol > criticalMinEnergy() + properties.getVaporizationHeat_kj())
        {
            return Phase.SUPERCRITICAL_FLUID;
        } else if (sublimationMaxEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure)
                && meltingMaxEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure))
        {
            return Phase.SOLID;
        } else if (!meltingMaxEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure)
                && vaporizationMaxEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure))
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
                if (sublimationMinEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure)
                        && meltingMinEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure))
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
                if (vaporizationMinEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure))
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

    private double boilingPointMinEnergy(){
        return properties.getBoilingPoint_K()*properties.getSpecificHeatCapacity_kj_mol_K()+properties.getFusionHeat_kj();
    }

    private double boilingPointMaxEnergy(){
        return properties.getBoilingPoint_K()*properties.getSpecificHeatCapacity_kj_mol_K()+properties.getFusionHeat_kj()+properties.getVaporizationHeat_kj();
    }

    private double criticalMinEnergy()
    {
        return energyAtMeltingPoint()
                + properties.getFusionHeat_kj()
                + properties.getCriticalPointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }
}
