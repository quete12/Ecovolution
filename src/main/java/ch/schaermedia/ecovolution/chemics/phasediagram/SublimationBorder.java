/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.math.LinearFunction;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class SublimationBorder extends PhaseBorder {

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
                BigDouble.ZERO,
                BigDouble.ZERO,
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa(),
                true);
        sublimationMax[0] = new LinearFunction(
                properties.getFusionHeat_kj().add(properties.getVaporizationHeat_kj(), new BigDouble()),
                BigDouble.ZERO,
                properties.maxTriplePointSublimationEnergy(),
                properties.getTriplePointPressure_kPa(),
                true);

    }

    private void initBordersZeroToBoiling(CompoundProperties properties)
    {
        sublimationMin[0] = new LinearFunction(
                BigDouble.ZERO,
                BigDouble.ZERO,
                properties.minMeltingPointEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                true);
        sublimationMax[0] = new LinearFunction(
                properties.getFusionHeat_kj().add(properties.getVaporizationHeat_kj(), new BigDouble()),
                BigDouble.ZERO,
                properties.maxBoilingEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                true);
    }

    private void initBordersBoilingToTriple(CompoundProperties properties)
    {
        sublimationMin[1] = new LinearFunction(
                properties.minMeltingPointEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa(),
                true);
        sublimationMax[1] = new LinearFunction(
                properties.maxBoilingEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.maxTriplePointSublimationEnergy(),
                properties.getTriplePointPressure_kPa(),
                true);
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

    public LinearFunction[] getSublimationMin()
    {
        return sublimationMin;
    }

    public LinearFunction[] getSublimationMax()
    {
        return sublimationMax;
    }

    @Override
    public void render(PGraphics g, BigDouble maxYValue, BigDouble maxXValue)
    {
        if (hasDualFunction)
        {
            g.stroke(0, 0, 255);
            sublimationMin[0].render(g, maxYValue,maxXValue);
            sublimationMin[1].render(g, maxYValue,maxXValue);
            g.stroke(255, 0, 0);
            sublimationMax[0].render(g, maxYValue,maxXValue);
            sublimationMax[1].render(g, maxYValue,maxXValue);
        } else
        {
            g.stroke(0, 0, 255);
            sublimationMin[0].render(g, maxYValue,maxXValue);
            g.stroke(255, 0, 0);
            sublimationMax[0].render(g, maxYValue,maxXValue);
        }
    }
}
