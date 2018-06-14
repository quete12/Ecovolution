/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Quentin
 */
public class CompoundProperties extends ElementProperties {

    private static final Map<String, CompoundProperties> BY_CODE = new HashMap<>();

    public static CompoundProperties getPropertiesFromCode(String code)
    {
        return BY_CODE.get(code);
    }

    private final Map<ElementProperties, Integer> composition;

    public CompoundProperties()
    {
        this.composition = new HashMap<>();
    }

    public CompoundProperties(JSONObject object)
    {
        super(object);
        this.composition = new CompoundDecoder(code).getComposition();
    }


    @Override
    public void map()
    {
        BY_CODE.put(code, this);
    }

    public String getName()
    {
        return name;
    }

    public String getCode()
    {
        return code;
    }

    public double getSpecificHeatCapacity_kj_mol_K()
    {
        return specificHeatCapacity_kj_mol_K;
    }

    public double getMeltingPoint_K()
    {
        return meltingPoint_K;
    }

    public double getBoilingPoint_K()
    {
        return boilingPoint_K;
    }

    public double getFusionHeat_kj()
    {
        return fusionHeat_kj;
    }

    public double getVaporizationHeat_kj()
    {
        return vaporizationHeat_kj;
    }

    public double getTriplePointHeat_K()
    {
        return triplePointHeat_K;
    }

    public double getTriplePointPressure_kPa()
    {
        return triplePointPressure_kPa;
    }

    public double getCriticalPointHeat_K()
    {
        return criticalPointHeat_K;
    }

    public double getCriticalPointPressure_kPa()
    {
        return criticalPointPressure_kPa;
    }

    @Override
    public String toString()
    {
        return super.toString() + "\n\tCompoundProperties{" + "composition=" + composition + '}';
    }
}
