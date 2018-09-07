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

/**
 *
 * @author Quentin
 */
public class VaporizationBorder extends PhaseBorder {

    private LinearFunction[] vaporizationMin;
    private LinearFunction[] vaporizationMax;

    public VaporizationBorder(CompoundProperties properties)
    {
        super(!properties.isBoilingPointUnderTriplePoint());
        initVaporizationBorders(properties);
    }

    private void initVaporizationBorders(CompoundProperties properties)
    {
        if (hasDualFunction)
        {
            vaporizationMin = new LinearFunction[2];
            vaporizationMax = new LinearFunction[2];
            initVaporizationBordersTripleToBoiling(properties);
            initVaporizationBordersBoilingToCritical(properties);
        } else
        {
            vaporizationMin = new LinearFunction[1];
            vaporizationMax = new LinearFunction[1];
            initVaporizationBordersTripleToCritical(properties);
        }
    }

    private void initVaporizationBordersBoilingToCritical(CompoundProperties properties)
    {
        vaporizationMin[1] = new LinearFunction(
                properties.minBoilingEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.minCriticalEnergy(),
                properties.getCriticalPointPressure_kPa(),
                true);
        vaporizationMax[1] = new LinearFunction(
                properties.maxBoilingEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.maxCriticalEnergy(),
                properties.getCriticalPointPressure_kPa(),
                true);
    }

    private void initVaporizationBordersTripleToBoiling(CompoundProperties properties)
    {
        vaporizationMin[0] = new LinearFunction(
                properties.minBoilingEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.minTriplePointEnergy().add(properties.getFusionHeat_kj(), new BigDouble()),
                properties.getTriplePointPressure_kPa(),
                true);
        vaporizationMax[0] = new LinearFunction(
                properties.maxBoilingEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.maxTriplePointVaporizationEnergy(),
                properties.getTriplePointPressure_kPa(),
                true);
    }

    private void initVaporizationBordersTripleToCritical(CompoundProperties properties)
    {
        vaporizationMin[0] = new LinearFunction(
                properties.minTriplePointEnergy().add(properties.getFusionHeat_kj(), new BigDouble()),
                properties.getTriplePointPressure_kPa(),
                properties.minCriticalEnergy(),
                properties.getCriticalPointPressure_kPa(),
                true);
        vaporizationMax[0] = new LinearFunction(
                properties.maxTriplePointVaporizationEnergy(),
                properties.getTriplePointPressure_kPa(),
                properties.maxCriticalEnergy(),
                properties.getCriticalPointPressure_kPa(),
                true);
    }

    public boolean isVaporized(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        if (!hasDualFunction)
        {
            return vaporizationMax[0].isPointUnder(energy_kj_mol, pressure_kPa);
        }

        if (isSteepFirst(vaporizationMax))
        {
            return vaporizationMax[0].isPointUnder(energy_kj_mol, pressure_kPa)
                    && vaporizationMax[1].isPointUnder(energy_kj_mol, pressure_kPa);
        } else
        {
            return vaporizationMax[0].isPointUnder(energy_kj_mol, pressure_kPa)
                    || vaporizationMax[1].isPointUnder(energy_kj_mol, pressure_kPa);
        }
    }

    public boolean isVaporizing(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        if (isVaporized(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (!hasDualFunction)
        {
            return vaporizationMin[0].isPointUnder(energy_kj_mol, pressure_kPa);
        }

        if (isSteepFirst(vaporizationMin))
        {
            return vaporizationMin[0].isPointUnder(energy_kj_mol, pressure_kPa)
                    && vaporizationMin[1].isPointUnder(energy_kj_mol, pressure_kPa);
        } else
        {
            return vaporizationMin[0].isPointUnder(energy_kj_mol, pressure_kPa)
                    || vaporizationMin[1].isPointUnder(energy_kj_mol, pressure_kPa);
        }
    }

    @Override
    public BigDouble getMinEnergy_kj_mol(BigDouble pressure_kPa)
    {
        if (!hasDualFunction)
        {
            return vaporizationMin[0].x(pressure_kPa);
        }
        if (isSteepFirst(vaporizationMin))
        {
            return BigDouble.max(vaporizationMin[0].x(pressure_kPa), vaporizationMin[1].x(pressure_kPa));
        } else
        {
            return BigDouble.min(vaporizationMin[0].x(pressure_kPa), vaporizationMin[1].x(pressure_kPa));
        }
    }

    public LinearFunction[] getVaporizationMin()
    {
        return vaporizationMin;
    }

    public LinearFunction[] getVaporizationMax()
    {
        return vaporizationMax;
    }
}
