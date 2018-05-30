/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Quentin
 */
public class ElementProperties {
    private static final Map<String, ElementProperties> BY_CODE = new HashMap<>();
    private static final Map<String, ElementProperties> BY_NAME = new HashMap<>();

    public static ElementProperties getElementFromCode(String code) {
        return BY_CODE.get(code);
    }

    public static ElementProperties getElementFromName(String name) {
        return BY_NAME.get(name);
    }

    protected String name;
    protected String code;
    protected int orderNumber;

    protected double fusionHeat;
    protected double vaporizationHeat;
    protected double molarHeatCapacity;

    protected double meltingPoint;
    protected double boilingPoint;
    protected double sublimationPoint;

    private double triplePointHeat;
    private double triplePointPressure;
    private double criticalPointHeat;
    private double criticalPointPressure;

    private int[] electronsPerShell;

    public ElementProperties() {
    }

    public ElementProperties(JSONObject object) {
        name = object.getString("name");
        code = object.getString("code");
        orderNumber = object.optInt("order");
        fusionHeat = object.getDouble("fusionHeat");
        vaporizationHeat = object.optDouble("vaporizationHeat");
        molarHeatCapacity = object.getDouble("molarHeatCapacity");
        meltingPoint = object.optDouble("meltingPoint");
        boilingPoint = object.optDouble("boilingPoint");
        sublimationPoint = object.optDouble("sublimationPoint");
        triplePointHeat = object.optDouble("triplePointHeat");
        triplePointPressure = object.optDouble("triplePointPressure");
        criticalPointHeat = object.optDouble("criticalPointHeat");
        criticalPointPressure = object.optDouble("criticalPointPressure");
        JSONArray electronsArray = object.getJSONArray("electronsPerShell");
        electronsPerShell = new int[electronsArray.length()];
        for (int i = 0; i < electronsArray.length(); i++) {
            electronsPerShell[i] = electronsArray.getInt(i);
        }
    }
}
