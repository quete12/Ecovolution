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

    protected double specificHeatCapacity_kj_mol_K;

    protected double meltingPoint_K;
    protected double boilingPoint_K;
    protected double fusionHeat_kj;
    protected double vaporizationHeat_kj;

    public ElementProperties() {
    }

    public ElementProperties(JSONObject object) {
        name = object.getString("name");
        code = object.getString("symbol");
        orderNumber = object.optInt("number");
        specificHeatCapacity_kj_mol_K = object.getDouble("specificHeatCapacity");
        meltingPoint_K = object.optDouble("meltingPoint");
        boilingPoint_K = object.optDouble("boilingPoint");
        fusionHeat_kj = object.optDouble("fusionHeat");
        vaporizationHeat_kj = object.optDouble("vaporizationHeat");
    }

    public void map(){
        BY_CODE.put(code, this);
    }

    @Override
    public String toString() {
        return "ElementProperties{" + "name=" + name + ", code=" + code + ", orderNumber=" + orderNumber + ", defaultPhase=" + defaultPhase + ", specificHeatCapacity_kj_mol_K=" + specificHeatCapacity_kj_mol_K + ", meltingPoint_K=" + meltingPoint_K + ", boilingPoint_K=" + boilingPoint_K + ", fusionHeat_kj=" + fusionHeat_kj + ", vaporizationHeat_kj=" + vaporizationHeat_kj + '}';
    }
}
