/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.properties;

import ch.schaermedia.ecovolution.environment.chem.compound.Phase;
import ch.schaermedia.ecovolution.environment.chem.phasediagram.PhaseDiagram_Energy_Pressure;
import ch.schaermedia.ecovolution.general.math.BigDouble;
import ch.schaermedia.ecovolution.general.math.Consts;
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

    private BigDouble specificHeatCapacity_kj_mol_K;

    private BigDouble meltingPoint_K;
    private BigDouble boilingPoint_K;
    private BigDouble fusionHeat_kj;
    private BigDouble vaporizationHeat_kj;

    private BigDouble triplePointHeat_K;
    private BigDouble triplePointPressure_kPa;
    private BigDouble criticalPointHeat_K;
    private BigDouble criticalPointPressure_kPa;

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
        if (!meetsDiagramConditions())
        {
            System.out.println("No Diagram Initialized for: " + this);
            return;
        }
        energy_Pressure_Diagram = new PhaseDiagram_Energy_Pressure(this);
    }

    private boolean meetsDiagramConditions()
    {
        return triplePointHeat_K.isNotZero()
                && triplePointPressure_kPa.isNotZero()
                && criticalPointHeat_K.isNotZero()
                && criticalPointPressure_kPa.isNotZero()
                && meltingPoint_K.isNotZero()
                && boilingPoint_K.isNotZero()
                && fusionHeat_kj.isNotZero()
                && vaporizationHeat_kj.isNotZero();
    }

    private void readProperties(JSONObject object)
    {
        name = object.getString("name");
        code = object.getString("symbol");
        orderNumber = object.optInt("number");
        defaultPhase = Phase.valueOf(object.getString("phase").toUpperCase());
        specificHeatCapacity_kj_mol_K = new BigDouble(object.getDouble("specificHeatCapacity"));
        meltingPoint_K = new BigDouble(object.optDouble("meltingPoint"));
        boilingPoint_K = new BigDouble(object.optDouble("boilingPoint"));
        fusionHeat_kj = new BigDouble(object.optDouble("fusionHeat"));
        vaporizationHeat_kj = new BigDouble(object.optDouble("vaporizationHeat"));
        triplePointHeat_K = new BigDouble(object.optDouble("triplePointHeat"));
        triplePointPressure_kPa = new BigDouble(object.optDouble("triplePointPressure"));
        criticalPointHeat_K = new BigDouble(object.optDouble("criticalPointHeat"));
        criticalPointPressure_kPa = new BigDouble(object.optDouble("criticalPointPressure"));
    }

    public void map()
    {
        BY_CODE.put(code, this);
    }

    @Override
    public String toString()
    {
        return "ElementProperties{" + "name=" + name + ", code=" + code + ", orderNumber=" + orderNumber + ", defaultPhase=" + defaultPhase + ", specificHeatCapacity_kj_mol_K=" + specificHeatCapacity_kj_mol_K + ", meltingPoint_K=" + meltingPoint_K + ", boilingPoint_K=" + boilingPoint_K + ", fusionHeat_kj=" + fusionHeat_kj + ", vaporizationHeat_kj=" + vaporizationHeat_kj + ", triplePointHeat_K=" + triplePointHeat_K + ", triplePointPressure_kPa=" + triplePointPressure_kPa + ", criticalPointHeat_K=" + criticalPointHeat_K + ", criticalPointPressure_kPa=" + criticalPointPressure_kPa + ", energy_Pressure_Diagram=" + energy_Pressure_Diagram + '}';
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
        return boilingPoint_K.compareTo(triplePointHeat_K) < 0 && Consts.STANDARD_PRESSURE_kPa.compareTo(triplePointPressure_kPa) < 0;
    }

    public boolean isMeltingPointUnderTriplePoint()
    {
        return meltingPoint_K.compareTo(triplePointHeat_K) < 0 && Consts.STANDARD_PRESSURE_kPa.compareTo(triplePointPressure_kPa) < 0;
    }

    public BigDouble getSpecificHeatCapacity_kj_mol_K()
    {
        return specificHeatCapacity_kj_mol_K;
    }

    public BigDouble getMeltingPoint_K()
    {
        return meltingPoint_K;
    }

    public BigDouble getBoilingPoint_K()
    {
        return boilingPoint_K;
    }

    public BigDouble getFusionHeat_kj()
    {
        return fusionHeat_kj;
    }

    public BigDouble getVaporizationHeat_kj()
    {
        return vaporizationHeat_kj;
    }

    public BigDouble getTriplePointHeat_K()
    {
        return triplePointHeat_K;
    }

    public BigDouble getTriplePointPressure_kPa()
    {
        return triplePointPressure_kPa;
    }

    public BigDouble getCriticalPointHeat_K()
    {
        return criticalPointHeat_K;
    }

    public BigDouble getCriticalPointPressure_kPa()
    {
        return criticalPointPressure_kPa;
    }

    public PhaseDiagram_Energy_Pressure getEnergy_Pressure_Diagram()
    {
        return energy_Pressure_Diagram;
    }

    public BigDouble minCriticalEnergy()
    {
        return criticalPointHeat_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble()).add(fusionHeat_kj);
    }

    public BigDouble maxCriticalEnergy()
    {
        return minCriticalEnergy().add(vaporizationHeat_kj);
    }

    public BigDouble minMeltingPointEnergy()
    {
        return meltingPoint_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble());
    }

    public BigDouble maxMeltingPointEnergy()
    {
        return minMeltingPointEnergy().add(fusionHeat_kj);
    }

    public BigDouble minBoilingEnergy()
    {
        return boilingPoint_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble()).add(fusionHeat_kj);
    }

    public BigDouble maxBoilingEnergy()
    {
        return minBoilingEnergy().add(vaporizationHeat_kj);
    }

    public BigDouble minTriplePointEnergy()
    {
        return triplePointHeat_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble());
    }

    public BigDouble maxTriplePointVaporizationEnergy()
    {
        return minTriplePointEnergy().add(fusionHeat_kj).add(vaporizationHeat_kj);
    }

    public BigDouble maxTriplePointMeltingEnergy()
    {
        return minTriplePointEnergy().add(fusionHeat_kj);
    }

    public BigDouble maxTriplePointSublimationEnergy()
    {
        return minTriplePointEnergy().add(fusionHeat_kj).add(vaporizationHeat_kj);
    }
}
