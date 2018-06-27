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
public class PhaseDiagram_Energy_Pressure2 {

    private Function[] sublimationMin;
    private Function[] sublimationMax;
    private Function meltingMin;
    private Function meltingMax;
    private Function[] vaporizationMin;
    private Function[] vaporizationMax;

    private final CompoundProperties properties;

    public PhaseDiagram_Energy_Pressure2(CompoundProperties properties)
    {
        this.properties = properties;
        initSublimationBorders();
        initMeltingBorders();
        initVaporizationBorders();
    }

    private void initVaporizationBorders()
    {
        if (properties.isBoilingPointUnderTriplePoint())
        {
            vaporizationMin = new Function[1];
            vaporizationMax = new Function[1];
            initVaporizationBordersTripleToCritical();
        } else
        {
            vaporizationMin = new Function[2];
            vaporizationMax = new Function[2];
            initVaporizationBordersTripleToBoiling();
            initVaporizationBordersBoilingToCritical();
        }
    }

    private void initVaporizationBordersBoilingToCritical()
    {
        vaporizationMin[1] = new LinearFunction(
                minBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                minCriticalEnergy(),
                properties.getCriticalPointPressure_kPa());
        vaporizationMax[1] = new LinearFunction(
                maxBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                maxCriticalEnergy(),
                properties.getCriticalPointPressure_kPa());
    }

    private void initVaporizationBordersTripleToBoiling()
    {
        vaporizationMin[0] = new LinearFunction(
                minBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
        vaporizationMax[0] = new LinearFunction(
                maxBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                maxTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
    }

    private void initVaporizationBordersTripleToCritical()
    {
        vaporizationMin[0] = new LinearFunction(
                minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa(),
                minCriticalEnergy(),
                properties.getCriticalPointPressure_kPa());
        vaporizationMax[0] = new LinearFunction(
                maxTriplePointEnergy(),
                properties.getTriplePointPressure_kPa(),
                maxCriticalEnergy(),
                properties.getCriticalPointPressure_kPa());
    }

    private void initMeltingBorders()
    {
        meltingMin = new LinearFunction(
                minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa(),
                minMeltingPointEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa);
        if (properties.isBoilingPointUnderTriplePoint())
        {
            meltingMax = new LinearFunction(
                    maxTriplePointEnergy(),
                    properties.getTriplePointPressure_kPa(),
                    maxBoilingEnergy(),
                    CompoundMix.STATIC_PRESSURE_kPa);
        } else
        {
            meltingMax = new LinearFunction(
                    maxTriplePointEnergy(),
                    properties.getTriplePointPressure_kPa(),
                    maxMeltingPointEnergy(),
                    CompoundMix.STATIC_PRESSURE_kPa);
        }
    }

    private void initSublimationBorders()
    {
        if (properties.isBoilingPointUnderTriplePoint())
        {
            sublimationMin = new Function[2];
            sublimationMax = new Function[2];
            initSublimationBordersZeroToBoiling();
            initSublimationBordersBoilingToTriple();
        } else
        {
            sublimationMin = new Function[1];
            sublimationMax = new Function[1];
            initSublimationBordersZeroToTriple();
        }
    }

    private void initSublimationBordersZeroToTriple()
    {
        sublimationMin[0] = new LinearFunction(
                0,
                0,
                minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
        sublimationMax[0] = new LinearFunction(
                0,
                0,
                maxTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());

    }

    private void initSublimationBordersZeroToBoiling()
    {
        sublimationMin[0] = new LinearFunction(
                0,
                0,
                minBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa);
        sublimationMax[0] = new LinearFunction(
                0,
                0,
                maxBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa);
    }

    private void initSublimationBordersBoilingToTriple()
    {
        sublimationMin[1] = new LinearFunction(
                minBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
        sublimationMax[1] = new LinearFunction(
                maxBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                maxTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
    }

    private boolean isSolid(double energy_kj_mol, double pressure_kPa)
    {
        if (isMelted(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        return !isSublimated(energy_kj_mol, pressure_kPa);
    }

    private boolean isLiquid(double energy_kj_mol, double pressure_kPa)
    {
        if (isCritical(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (isVaporized(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        return isMelted(energy_kj_mol, pressure_kPa);
    }

    private boolean isGas(double energy_kj_mol, double pressure_kPa)
    {
        if (isCritical(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        return isVaporized(energy_kj_mol, pressure_kPa)
                || isSublimated(energy_kj_mol, pressure_kPa);
    }

    private boolean isSupercriticalFluid(double energy_kj_mol, double pressure_kPa){
        return isCritical(energy_kj_mol, pressure_kPa);
    }

    private boolean isMelted(double energy_kj_mol, double pressure_kPa)
    {
        return meltingMax.isPointOnOrRight(energy_kj_mol, pressure_kPa);
    }

    private boolean isSublimated(double energy_kj_mol, double pressure_kPa)
    {
        for (Function sublimationBorder : sublimationMax)
        {
            if (sublimationBorder.isPointUnder(energy_kj_mol, pressure_kPa))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isCritical(double energy_kj_mol, double pressure_kPa)
    {
        return pressure_kPa > properties.getCriticalPointPressure_kPa()
                && energy_kj_mol > maxCriticalEnergy();
    }

    private boolean isVaporized(double energy_kj_mol, double pressure_kPa)
    {
        for (Function vaporizationBorder : vaporizationMax)
        {
            if (vaporizationBorder.isPointUnder(energy_kj_mol, pressure_kPa))
            {
                return true;
            }
        }
        return false;
    }

    private double minCriticalEnergy()
    {
        return properties.getCriticalPointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K() + properties.getFusionHeat_kj();
    }

    private double maxCriticalEnergy()
    {
        return minCriticalEnergy() + properties.getVaporizationHeat_kj();
    }

    private double minMeltingPointEnergy()
    {
        return properties.getMeltingPoint_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private double maxMeltingPointEnergy()
    {
        return minMeltingPointEnergy() + properties.getFusionHeat_kj();
    }

    private double minBoilingEnergy()
    {
        return properties.getBoilingPoint_K() * properties.getSpecificHeatCapacity_kj_mol_K() + properties.getFusionHeat_kj();
    }

    private double maxBoilingEnergy()
    {
        return minBoilingEnergy() + properties.getVaporizationHeat_kj();
    }

    private double minTriplePointEnergy()
    {
        return properties.getTriplePointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private double maxTriplePointEnergy()
    {
        return (properties.getTriplePointHeat_K() * properties.getSpecificHeatCapacity_kj_mol_K()) + properties.getFusionHeat_kj() + properties.getVaporizationHeat_kj();
    }
}
