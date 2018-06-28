/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import ch.schaermedia.ecovolution.environment.chem.phasediagram.PhaseDiagram_Energy_Pressure;
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

    private String name;
    private String code;
    private int orderNumber;
    private Phase defaultPhase;

    private double specificHeatCapacity_kj_mol_K;

    private double meltingPoint_K;
    private double boilingPoint_K;
    private double fusionHeat_kj;
    private double vaporizationHeat_kj;

    private double triplePointHeat_K;
    private double triplePointPressure_kPa;
    private double criticalPointHeat_K;
    private double criticalPointPressure_kPa;

    private PhaseDiagram_Energy_Pressure energy_Pressure_Diagram;

    public ElementProperties()
    {
    }

    public ElementProperties(JSONObject object)
    {
        readProperties(object);
        initDiagrams();
    }

    private void initDiagrams()
    {
        energy_Pressure_Diagram = new PhaseDiagram_Energy_Pressure(this);
    }

    private void readProperties(JSONObject object)
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

    public String getName()
    {
        return name;
    }

    public String getCode()
    {
        return code;
    }

    public boolean isBoilingPointUnderTriplePoint()
    {
        return boilingPoint_K < triplePointHeat_K && CompoundMix.STATIC_PRESSURE_kPa < triplePointPressure_kPa;
    }

    public boolean isMeltingPointUnderTriplePoint()
    {
        return meltingPoint_K < triplePointHeat_K && CompoundMix.STATIC_PRESSURE_kPa < triplePointPressure_kPa;
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

    public PhaseDiagram_Energy_Pressure getEnergy_Pressure_Diagram()
    {
        return energy_Pressure_Diagram;
    }

    public double minCriticalEnergy()
    {
        return criticalPointHeat_K * specificHeatCapacity_kj_mol_K + fusionHeat_kj;
    }

    public double maxCriticalEnergy()
    {
        return minCriticalEnergy() + vaporizationHeat_kj;
    }

    public double minMeltingPointEnergy()
    {
        return meltingPoint_K * specificHeatCapacity_kj_mol_K;
    }

    public double maxMeltingPointEnergy()
    {
        return minMeltingPointEnergy() + fusionHeat_kj;
    }

    public double minBoilingEnergy()
    {
        return boilingPoint_K * specificHeatCapacity_kj_mol_K + fusionHeat_kj;
    }

    public double maxBoilingEnergy()
    {
        return minBoilingEnergy() + vaporizationHeat_kj;
    }

    public double minTriplePointEnergy()
    {
        return triplePointHeat_K * specificHeatCapacity_kj_mol_K;
    }

    public double maxTriplePointVaporizationEnergy()
    {
        return minTriplePointEnergy() + fusionHeat_kj + vaporizationHeat_kj;
    }

    public double maxTriplePointMeltingEnergy()
    {
        return minTriplePointEnergy() + fusionHeat_kj;
    }

    public double maxTriplePointSublimationEnergy()
    {
        return minTriplePointEnergy() + fusionHeat_kj + vaporizationHeat_kj;
    }
}
