/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import ch.schaermedia.ecovolution.environment.chem.CompoundProperties;
import ch.schaermedia.ecovolution.general.math.LinearFunction;

/**
 *
 * @author Quentin
 */
public class SublimationBorder extends PhaseBorder{
    private LinearFunction[] sublimationMin;
    private LinearFunction[] sublimationMax;

    public SublimationBorder(CompoundProperties properties)
    {
        super(properties.isBoilingPointUnderTriplePoint());
        initBorders(properties);
    }

    private void initBorders(CompoundProperties properties)
    {
        if (hasDualFunction)
        {
            sublimationMin = new LinearFunction[2];
            sublimationMax = new LinearFunction[2];
            initBordersZeroToBoiling(properties);
            initBordersBoilingToTriple(properties);
        } else
        {
            sublimationMin = new LinearFunction[1];
            sublimationMax = new LinearFunction[1];
            initBordersZeroToTriple(properties);
        }
    }

    private void initBordersZeroToTriple(CompoundProperties properties)
    {
        sublimationMin[0] = new LinearFunction(
                0,
                0,
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
        sublimationMax[0] = new LinearFunction(
                0,
                0,
                properties.maxTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());

    }

    private void initBordersZeroToBoiling(CompoundProperties properties)
    {
        sublimationMin[0] = new LinearFunction(
                0,
                0,
                properties.minBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa);
        sublimationMax[0] = new LinearFunction(
                0,
                0,
                properties.maxBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa);
    }

    private void initBordersBoilingToTriple(CompoundProperties properties)
    {
        sublimationMin[1] = new LinearFunction(
                properties.minBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
        sublimationMax[1] = new LinearFunction(
                properties.maxBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                properties.maxTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
    }

    public boolean isSublimated(double energy_kj_mol, double pressure_kPa)
    {
        if (!hasDualFunction)
        {
            return sublimationMax[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        }

        if (isSteepFirst(sublimationMax))
        {
            return sublimationMax[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa)
                    && sublimationMax[1].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        } else
        {
            return sublimationMax[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa)
                    || sublimationMax[1].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        }
    }

    public boolean isSublimating(double energy_kj_mol, double pressure_kPa)
    {
        if (isSublimated(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (!hasDualFunction)
        {
            return sublimationMin[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        }

        if (isSteepFirst(sublimationMin))
        {
            return sublimationMin[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa)
                    && sublimationMin[1].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        } else
        {
            return sublimationMin[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa)
                    || sublimationMin[1].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        }
    }
}
