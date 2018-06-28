/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import ch.schaermedia.ecovolution.environment.chem.ElementProperties;
import ch.schaermedia.ecovolution.general.math.LinearFunction;

/**
 *
 * @author Quentin
 */
public class VaporizationBorder extends PhaseBorder{

    private LinearFunction[] vaporizationMin;
    private LinearFunction[] vaporizationMax;

    public VaporizationBorder(ElementProperties properties)
    {
        super(!properties.isBoilingPointUnderTriplePoint());
        initVaporizationBorders(properties);
    }

    private void initVaporizationBorders(ElementProperties properties)
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

    private void initVaporizationBordersBoilingToCritical(ElementProperties properties)
    {
        vaporizationMin[1] = new LinearFunction(
                properties.minBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                properties.minCriticalEnergy(),
                properties.getCriticalPointPressure_kPa());
        vaporizationMax[1] = new LinearFunction(
                properties.maxBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                properties.maxCriticalEnergy(),
                properties.getCriticalPointPressure_kPa());
    }

    private void initVaporizationBordersTripleToBoiling(ElementProperties properties)
    {
        vaporizationMin[0] = new LinearFunction(
                properties.minBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa());
        vaporizationMax[0] = new LinearFunction(
                properties.maxBoilingEnergy(),
                CompoundMix.STATIC_PRESSURE_kPa,
                properties.maxTriplePointVaporizationEnergy(),
                properties.getTriplePointPressure_kPa());
    }

    private void initVaporizationBordersTripleToCritical(ElementProperties properties)
    {
        vaporizationMin[0] = new LinearFunction(
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa(),
                properties.minCriticalEnergy(),
                properties.getCriticalPointPressure_kPa());
        vaporizationMax[0] = new LinearFunction(
                properties.maxTriplePointVaporizationEnergy(),
                properties.getTriplePointPressure_kPa(),
                properties.maxCriticalEnergy(),
                properties.getCriticalPointPressure_kPa());
    }

    public boolean isVaporized(double energy_kj_mol, double pressure_kPa)
    {
        if (!hasDualFunction)
        {
            return vaporizationMax[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        }

        if (isSteepFirst(vaporizationMax))
        {
            return vaporizationMax[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa)
                    && vaporizationMax[1].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        } else
        {
            return vaporizationMax[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa)
                    || vaporizationMax[1].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        }
    }

    public boolean isVaporizing(double energy_kj_mol, double pressure_kPa)
    {
        if (isVaporized(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (!hasDualFunction)
        {
            return vaporizationMin[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        }

        if (isSteepFirst(vaporizationMin))
        {
            return vaporizationMin[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa)
                    && vaporizationMin[1].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        } else
        {
            return vaporizationMin[0].isPointOnOrUnder(energy_kj_mol, pressure_kPa)
                    || vaporizationMin[1].isPointOnOrUnder(energy_kj_mol, pressure_kPa);
        }
    }

    @Override
    public double getMinEnergy_kj_mol(double pressure_kPa)
    {
        if(!hasDualFunction){
            return vaporizationMin[0].x(pressure_kPa);
        }
        if(isSteepFirst(vaporizationMin)){
            return Math.max(vaporizationMin[0].x(pressure_kPa), vaporizationMin[1].x(pressure_kPa));
        }else{
            return Math.min(vaporizationMin[0].x(pressure_kPa), vaporizationMin[1].x(pressure_kPa));
        }
    }
}
