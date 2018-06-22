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
public class PhaseDiagram_Temperature_Pressure {

    private final ElementProperties properties;

    private LinearFunction sublimationBorder;
    private LinearFunction meltingBorder;
    private LinearFunction vaporizationBorder;

    public PhaseDiagram_Temperature_Pressure(ElementProperties properties)
    {
        this.properties = properties;
        initFunctions();
    }

    private void initFunctions(){
        sublimationBorder = new LinearFunction(
                0,
                0,
                properties.getTriplePointHeat_K(),
                properties.getTriplePointPressure_kPa());
        meltingBorder = new LinearFunction(
                properties.getMeltingPoint_K(),
                CompoundMix.STATIC_PRESSURE_kPa,
                properties.getTriplePointHeat_K(),
                properties.getTriplePointPressure_kPa());
        vaporizationBorder = new LinearFunction(
                properties.getTriplePointHeat_K(),
                properties.getTriplePointPressure_kPa(),
                properties.getCriticalPointHeat_K(),
                properties.getCriticalPointPressure_kPa());
    }
    public Phase getPhase(double temperature_K, double pressure_kPa)
    {
        if (pressure_kPa > properties.getCriticalPointPressure_kPa()
                && temperature_K > properties.getCriticalPointHeat_K())
        {
            return Phase.SUPERCRITICAL_FLUID;
        } else if (sublimationBorder.isPointOnOrLeft(temperature_K, pressure_kPa)
                && meltingBorder.isPointOnOrLeft(temperature_K, pressure_kPa))
        {
            return Phase.SOLID;
        } else if (!meltingBorder.isPointOnOrLeft(temperature_K, pressure_kPa)
                && vaporizationBorder.isPointOnOrLeft(temperature_K, pressure_kPa))
        {
            return Phase.LIQUID;
        } else
        {
            return Phase.GAS;
        }
    }

    public LinearFunction getSublimationBorder()
    {
        return sublimationBorder;
    }

    public LinearFunction getMeltingBorder()
    {
        return meltingBorder;
    }

    public LinearFunction getVaporizationBorder()
    {
        return vaporizationBorder;
    }

}
