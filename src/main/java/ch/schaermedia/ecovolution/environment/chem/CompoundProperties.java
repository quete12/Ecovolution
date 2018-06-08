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

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public int getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public double getSpecificHeatCapacity()
    {
        return specificHeatCapacity_kj_mol_K;
    }

    public void setSpecificHeatCapacity(double specificHeatCapacity)
    {
        this.specificHeatCapacity_kj_mol_K = specificHeatCapacity;
    }

    public double getMeltingPoint()
    {
        return meltingPoint_K;
    }

    public void setMeltingPoint(double meltingPoint)
    {
        this.meltingPoint_K = meltingPoint;
    }

    public double getBoilingPoint()
    {
        return boilingPoint_K;
    }

    public void setBoilingPoint(double boilingPoint)
    {
        this.boilingPoint_K = boilingPoint;
    }

    public double getFusionHeat()
    {
        return fusionHeat_kj;
    }

    public void setFusionHeat(double fusionHeat)
    {
        this.fusionHeat_kj = fusionHeat;
    }

    public double getVaporizationHeat()
    {
        return vaporizationHeat_kj;
    }

    public void setVaporizationHeat(double vaporizationHeat)
    {
        this.vaporizationHeat_kj = vaporizationHeat;
    }

    @Override
    public String toString()
    {
        return super.toString() + "\n\tCompoundProperties{" + "composition=" + composition + '}';
    }
}
