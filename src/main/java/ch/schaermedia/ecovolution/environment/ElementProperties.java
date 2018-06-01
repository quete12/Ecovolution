/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Quentin
 */
public class ElementProperties {
    private static final Map<String, ElementProperties> BY_CODE = new HashMap<>();

    public static ElementProperties getPropertiesFromCode(String code) {
        return BY_CODE.get(code);
    }

    protected String name;
    protected String code;
    protected int orderNumber;
    protected Phase defaultPhase;

    protected double specificHeatCapacity;

    protected double meltingPoint;
    protected double boilingPoint;
    protected double fusionHeat;
    protected double vaporizationHeat;

    private int[] electronsPerShell;

    public ElementProperties() {
    }

    public ElementProperties(JSONObject object) {
        name = object.getString("name");
        code = object.getString("symbol");
        orderNumber = object.optInt("number");
        specificHeatCapacity = object.getDouble("specificHeatCapacity");
        meltingPoint = object.optDouble("meltingPoint");
        boilingPoint = object.optDouble("boilingPoint");
        fusionHeat = object.optDouble("fusionHeat");
        vaporizationHeat = object.optDouble("vaporizationHeat");
    }

    public void map(){
        BY_CODE.put(code, this);
    }
}
