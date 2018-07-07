/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.properties;

import ch.schaermedia.ecovolution.environment.chem.compound.Phase;
import ch.schaermedia.ecovolution.environment.chem.phasediagram.PhaseDiagram_Energy_Pressure;
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

    private long specificHeatCapacity_kj_mol_K;

    private long meltingPoint_K;
    private long boilingPoint_K;
    private long fusionHeat_kj;
    private long vaporizationHeat_kj;

    private long triplePointHeat_K;
    private long triplePointPressure_kPa;
    private long criticalPointHeat_K;
    private long criticalPointPressure_kPa;

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
        return triplePointHeat_K != 0
                && triplePointPressure_kPa != 0
                && criticalPointHeat_K != 0
                && criticalPointPressure_kPa != 0
                && meltingPoint_K != 0
                && boilingPoint_K != 0
                && fusionHeat_kj != 0
                && vaporizationHeat_kj != 0;
    }

    private void readProperties(JSONObject object)
    {
        name = object.getString("name");
        code = object.getString("symbol");
        orderNumber = object.optInt("number");
        defaultPhase = Phase.valueOf(object.getString("phase").toUpperCase());
        specificHeatCapacity_kj_mol_K = (long) (object.getDouble("specificHeatCapacity") * Consts.PRESCISION);
        meltingPoint_K = (long) (object.optDouble("meltingPoint") * Consts.PRESCISION);
        boilingPoint_K = (long) (object.optDouble("boilingPoint") * Consts.PRESCISION);
        fusionHeat_kj = (long) (object.optDouble("fusionHeat") * Consts.PRESCISION);
        vaporizationHeat_kj = (long) (object.optDouble("vaporizationHeat") * Consts.PRESCISION);
        triplePointHeat_K = (long) (object.optDouble("triplePointHeat") * Consts.PRESCISION);
        triplePointPressure_kPa = (long) (object.optDouble("triplePointPressure") * Consts.PRESCISION);
        criticalPointHeat_K = (long) (object.optDouble("criticalPointHeat") * Consts.PRESCISION);
        criticalPointPressure_kPa = (long) (object.optDouble("criticalPointPressure") * Consts.PRESCISION);
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
        return boilingPoint_K < triplePointHeat_K && Consts.STANDARD_PRESSURE_kPa < triplePointPressure_kPa;
    }

    public boolean isMeltingPointUnderTriplePoint()
    {
        return meltingPoint_K < triplePointHeat_K && Consts.STANDARD_PRESSURE_kPa < triplePointPressure_kPa;
    }

    public long getSpecificHeatCapacity_kj_mol_K()
    {
        return specificHeatCapacity_kj_mol_K;
    }

    public long getMeltingPoint_K()
    {
        return meltingPoint_K;
    }

    public long getBoilingPoint_K()
    {
        return boilingPoint_K;
    }

    public long getFusionHeat_kj()
    {
        return fusionHeat_kj;
    }

    public long getVaporizationHeat_kj()
    {
        return vaporizationHeat_kj;
    }

    public long getTriplePointHeat_K()
    {
        return triplePointHeat_K;
    }

    public long getTriplePointPressure_kPa()
    {
        return triplePointPressure_kPa;
    }

    public long getCriticalPointHeat_K()
    {
        return criticalPointHeat_K;
    }

    public long getCriticalPointPressure_kPa()
    {
        return criticalPointPressure_kPa;
    }

    public PhaseDiagram_Energy_Pressure getEnergy_Pressure_Diagram()
    {
        return energy_Pressure_Diagram;
    }

    public long minCriticalEnergy()
    {
        return criticalPointHeat_K * specificHeatCapacity_kj_mol_K + fusionHeat_kj;
    }

    public long maxCriticalEnergy()
    {
        return minCriticalEnergy() + vaporizationHeat_kj;
    }

    public long minMeltingPointEnergy()
    {
        return meltingPoint_K * specificHeatCapacity_kj_mol_K;
    }

    public long maxMeltingPointEnergy()
    {
        return minMeltingPointEnergy() + fusionHeat_kj;
    }

    public long minBoilingEnergy()
    {
        return boilingPoint_K * specificHeatCapacity_kj_mol_K + fusionHeat_kj;
    }

    public long maxBoilingEnergy()
    {
        return minBoilingEnergy() + vaporizationHeat_kj;
    }

    public long minTriplePointEnergy()
    {
        return triplePointHeat_K * specificHeatCapacity_kj_mol_K;
    }

    public long maxTriplePointVaporizationEnergy()
    {
        return minTriplePointEnergy() + fusionHeat_kj + vaporizationHeat_kj;
    }

    public long maxTriplePointMeltingEnergy()
    {
        return minTriplePointEnergy() + fusionHeat_kj;
    }

    public long maxTriplePointSublimationEnergy()
    {
        return minTriplePointEnergy() + fusionHeat_kj + vaporizationHeat_kj;
    }
}
