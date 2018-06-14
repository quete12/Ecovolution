/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import ch.schaermedia.ecovolution.general.LinearFunction;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Quentin
 */
public class ElementProperties {

    private static final Map<String, ElementProperties> BY_CODE = new HashMap<>();

    public static ElementProperties getPropertiesFromCode(String code)
    {
        return BY_CODE.get(code);
    }

    protected String name;
    protected String code;
    protected int orderNumber;
    protected Phase defaultPhase;

    protected double specificHeatCapacity_kj_mol_K;

    protected double meltingPoint_K;
    protected double boilingPoint_K;
    protected double fusionHeat_kj;
    protected double vaporizationHeat_kj;

    protected double triplePointHeat_K;
    protected double triplePointPressure_kPa;
    protected double criticalPointHeat_K;
    protected double criticalPointPressure_kPa;

    private LinearFunction sublimationBorder;
    private LinearFunction meltingBorder;
    private LinearFunction vaporizationBorder;

    public ElementProperties()
    {
    }

    public ElementProperties(JSONObject object)
    {
        name = object.getString("name");
        code = object.getString("symbol");
        orderNumber = object.optInt("number");
        specificHeatCapacity_kj_mol_K = object.getDouble("specificHeatCapacity");
        meltingPoint_K = object.optDouble("meltingPoint");
        boilingPoint_K = object.optDouble("boilingPoint");
        fusionHeat_kj = object.optDouble("fusionHeat");
        vaporizationHeat_kj = object.optDouble("vaporizationHeat");
        triplePointHeat_K = object.optDouble("triplePointHeat");
        triplePointPressure_kPa = object.optDouble("triplePointPressure");
        criticalPointHeat_K = object.optDouble("criticalPointHeat");
        criticalPointPressure_kPa = object.optDouble("criticalPointPressure");

        sublimationBorder = new LinearFunction(
                0,
                0,
                triplePointHeat_K,
                triplePointPressure_kPa);
        meltingBorder = new LinearFunction(
                meltingPoint_K,
                CompoundMix.STATIC_PRESSURE_kPa,
                triplePointHeat_K,
                triplePointPressure_kPa);
        vaporizationBorder = new LinearFunction(
                triplePointHeat_K,
                triplePointPressure_kPa,
                criticalPointHeat_K,
                criticalPointPressure_kPa);
    }

    public void map()
    {
        BY_CODE.put(code, this);
    }

    @Override
    public String toString()
    {
        return "ElementProperties{" + "name=" + name + ", code=" + code + ", orderNumber=" + orderNumber + ", defaultPhase=" + defaultPhase + ", specificHeatCapacity_kj_mol_K=" + specificHeatCapacity_kj_mol_K + ", meltingPoint_K=" + meltingPoint_K + ", boilingPoint_K=" + boilingPoint_K + ", fusionHeat_kj=" + fusionHeat_kj + ", vaporizationHeat_kj=" + vaporizationHeat_kj + '}';
    }

    public Phase getPhase(double temperature_K, double pressure_kPa)
    {
        if (pressure_kPa > criticalPointPressure_kPa
                && temperature_K > criticalPointHeat_K)
        {
            return Phase.SUPERCRITICAL_FLUID;
        } else if (sublimationBorder.isPointLeftOrOn(temperature_K, pressure_kPa)
                && meltingBorder.isPointLeftOrOn(temperature_K, pressure_kPa))
        {
            return Phase.SOLID;
        } else if (!meltingBorder.isPointLeftOrOn(temperature_K, pressure_kPa)
                && vaporizationBorder.isPointLeftOrOn(temperature_K, pressure_kPa))
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
