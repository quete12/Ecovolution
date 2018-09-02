/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.properties.ElementProperties;
import ch.schaermedia.ecovolution.general.math.BigDouble;
import ch.schaermedia.ecovolution.general.math.Consts;
import ch.schaermedia.ecovolution.general.math.LinearFunction;

/**
 *
 * @author Quentin
 */
public class SublimationBorder extends PhaseBorder {

    private LinearFunction[] sublimationMin;
    private LinearFunction[] sublimationMax;

    public SublimationBorder(ElementProperties properties)
    {
        super(properties.isBoilingPointUnderTriplePoint());
        initBorders(properties);
    }

    private void initBorders(ElementProperties properties)
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

    private void initBordersZeroToTriple(ElementProperties properties)
    {
        sublimationMin[0] = new LinearFunction(
                BigDouble.ZERO,
                BigDouble.ZERO,
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
        sublimationMax[0] = new LinearFunction(
                properties.getFusionHeat_kj().add(properties.getVaporizationHeat_kj(),new BigDouble()),
                BigDouble.ZERO,
                properties.maxTriplePointSublimationEnergy(),
                properties.getTriplePointPressure_kPa());

    }

    private void initBordersZeroToBoiling(ElementProperties properties)
    {
        sublimationMin[0] = new LinearFunction(
                BigDouble.ZERO,
                BigDouble.ZERO,
                properties.minBoilingEnergy().sub(properties.getFusionHeat_kj(),new BigDouble()),
                Consts.STANDARD_PRESSURE_kPa);
        sublimationMax[0] = new LinearFunction(
                properties.getFusionHeat_kj().add(properties.getVaporizationHeat_kj(),new BigDouble()),
                BigDouble.ZERO,
                properties.maxBoilingEnergy(),
                Consts.STANDARD_PRESSURE_kPa);
    }

    private void initBordersBoilingToTriple(ElementProperties properties)
    {
        sublimationMin[1] = new LinearFunction(
                properties.minBoilingEnergy().sub(properties.getFusionHeat_kj(),new BigDouble()),
                Consts.STANDARD_PRESSURE_kPa,
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
        sublimationMax[1] = new LinearFunction(
                properties.maxBoilingEnergy(),
                Consts.STANDARD_PRESSURE_kPa,
                properties.maxTriplePointSublimationEnergy(),
                properties.getTriplePointPressure_kPa());
    }

    public boolean isSublimated(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        if (!hasDualFunction)
        {
            return sublimationMax[0].isPointUnder(energy_kj_mol, pressure_kPa);
        }

        if (isSteepFirst(sublimationMax))
        {
            return sublimationMax[0].isPointUnder(energy_kj_mol, pressure_kPa)
                    && sublimationMax[1].isPointUnder(energy_kj_mol, pressure_kPa);
        } else
        {
            return sublimationMax[0].isPointUnder(energy_kj_mol, pressure_kPa)
                    || sublimationMax[1].isPointUnder(energy_kj_mol, pressure_kPa);
        }
    }

    public boolean isSublimating(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        if (isSublimated(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (!hasDualFunction)
        {
            return sublimationMin[0].isPointUnder(energy_kj_mol, pressure_kPa);
        }

        if (isSteepFirst(sublimationMin))
        {
            return sublimationMin[0].isPointUnder(energy_kj_mol, pressure_kPa)
                    && sublimationMin[1].isPointUnder(energy_kj_mol, pressure_kPa);
        } else
        {
            return sublimationMin[0].isPointUnder(energy_kj_mol, pressure_kPa)
                    || sublimationMin[1].isPointUnder(energy_kj_mol, pressure_kPa);
        }
    }

    @Override
    public BigDouble getMinEnergy_kj_mol(BigDouble pressure_kPa)
    {
        if (!hasDualFunction)
        {
            return sublimationMin[0].x(pressure_kPa);
        }
        if (isSteepFirst(sublimationMin))
        {
            return BigDouble.max(sublimationMin[0].x(pressure_kPa), sublimationMin[1].x(pressure_kPa));
        } else
        {
            return BigDouble.min(sublimationMin[0].x(pressure_kPa), sublimationMin[1].x(pressure_kPa));
        }
    }
}
