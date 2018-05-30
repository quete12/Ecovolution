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
public class Element {

    protected static final Map<String, Element> BY_NAME = new HashMap<>();
    protected static final Map<String, Element> BY_CODE = new HashMap<>();

    public static Element getByName(String name) {
        return BY_NAME.get(name);
    }

    public static Element getByCode(String code) {
        return BY_CODE.get(code);
    }

    private String name;
    private String code;
    private int index;

    private double fusionHeat;
    private double vaporizationHeat;
    private double molarHeatCapacity;

    private double meltingPoint;
    private double boilingPoint;
    private double sublimationPoint;

    public Element() {
    }

    public Element(JSONObject object) {
        name = object.getString("name");
        code = object.getString("symbol");
        fusionHeat = object.getDouble("fusionHeat");
        vaporizationHeat = object.optDouble("vaporizationHeat");
        molarHeatCapacity = object.getDouble("molarHeatCapacity");
        meltingPoint = object.optDouble("meltingPoint");
        boilingPoint = object.optDouble("boilingPoint");
        sublimationPoint = object.optDouble("sublimationPoint");
    }

    public void map() {
        if (name != null && !name.isEmpty()) {
            BY_NAME.put(name, this);
        }
        BY_CODE.put(code, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getFusionHeat() {
        return fusionHeat;
    }

    public void setFusionHeat(double fusionHeat) {
        this.fusionHeat = fusionHeat;
    }

    public double getVaporizationHeat() {
        return vaporizationHeat;
    }

    public void setVaporizationHeat(double vaporizationHeat) {
        this.vaporizationHeat = vaporizationHeat;
    }

    public double getMolarHeatCapacity() {
        return molarHeatCapacity;
    }

    public void setMolarHeatCapacity(double molarHeatCapacity) {
        this.molarHeatCapacity = molarHeatCapacity;
    }

    public double getMeltingPoint() {
        return meltingPoint;
    }

    public void setMeltingPoint(double meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    public double getBoilingPoint() {
        return boilingPoint;
    }

    public void setBoilingPoint(double boilingPoint) {
        this.boilingPoint = boilingPoint;
    }

    public double getSublimationPoint() {
        return sublimationPoint;
    }

    public void setSublimationPoint(double sublimationPoint) {
        this.sublimationPoint = sublimationPoint;
    }
}
