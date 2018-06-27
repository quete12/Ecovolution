/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import ch.schaermedia.ecovolution.general.math.Function;
import ch.schaermedia.ecovolution.general.math.LinearFunction;

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
        sublimationMaxEnergy = new LinearFunction(
                0,
                0,
                triplePointMaxEnergy(),
                properties.getTriplePointPressure_kPa());

        meltingMinEnergy = new LinearFunction(
                energyAtMeltingPoint(),
                CompoundMix.STATIC_PRESSURE_kPa,
                triplePointMinEnergy(),
                properties.getTriplePointPressure_kPa());
        meltingMaxEnergy = new LinearFunction(
                energyAtMeltingPoint() + properties.getFusionHeat_kj(),
                CompoundMix.STATIC_PRESSURE_kPa,
                triplePointMaxEnergy(),
                properties.getTriplePointPressure_kPa());

        vaporizationMinEnergy = new LinearFunction(
                triplePointMinEnergy(),
                properties.getTriplePointPressure_kPa(),
                criticalMinEnergy(),
                properties.getCriticalPointPressure_kPa());
        vaporizationMaxEnergy = new LinearFunction(
                triplePointMaxEnergy(),
                properties.getTriplePointPressure_kPa(),
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
        double energyTemp;
        switch (phase)
        {
            case SOLID:
                if (sublimationMinEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure)
                        && meltingMinEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure))
                {
                    energyTemp = energy_kj_mol;
                } else if (mixturePressure > properties.getTriplePointPressure_kPa())
                {
                    energyTemp = meltingMinEnergy.x(mixturePressure);
                } else
                {
                    energyTemp = sublimationMinEnergy.x(mixturePressure);
                }
                break;
            case LIQUID:
                if (vaporizationMinEnergy.isPointOnOrLeft(energy_kj_mol, mixturePressure))
                {
                    energyTemp = energy_kj_mol - properties.getFusionHeat_kj();
                } else
                {
                    energyTemp = vaporizationMinEnergy.x(mixturePressure);
                }
                break;
            case GAS:
            case SUPERCRITICAL_FLUID:
                energyTemp = energy_kj_mol - properties.getFusionHeat_kj() - properties.getVaporizationHeat_kj();
                break;
            default:
                throw new AssertionError(phase.name());
        }
        return energyTemp / properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private double energyAtMeltingPoint()
    {
        return properties.getMeltingPoint_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private double triplePointMinEnergy()
    {
        return properties.getTriplePointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private double triplePointMaxEnergy()
    {
        return properties.getTriplePointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K() + properties.getVaporizationHeat_kj() + properties.getFusionHeat_kj();
    }

    private double criticalMinEnergy()
    {
        return energyAtMeltingPoint()
                + properties.getFusionHeat_kj()
                + properties.getCriticalPointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }

    public Function getSublimationMinEnergy()
    {
        return sublimationMinEnergy;
    }

    public Function getMeltingMinEnergy()
    {
        return meltingMinEnergy;
    }

    public Function getVaporizationMinEnergy()
    {
        return vaporizationMinEnergy;
    }
}
