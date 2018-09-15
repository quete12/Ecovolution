/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.atmospherics;

import ch.schaermedia.ecovolution.chemics.phasediagram.PhaseDiagram;
import ch.schaermedia.ecovolution.math.BigDouble;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Quentin
 */
public class CompoundProperties {

    private static final Map<String, CompoundProperties> BY_CODE = new HashMap<>();

    public static CompoundProperties getPropertiesFromCode(String code)
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
    private BigDouble sublimationPoint_K;
    private BigDouble fusionHeat_kj;
    private BigDouble vaporizationHeat_kj;

    private BigDouble triplePointHeat_K;
    private BigDouble triplePointPressure_kPa;
    private BigDouble criticalPointHeat_K;
    private BigDouble criticalPointPressure_kPa;

    private PhaseDiagram phaseDiagram;

    public CompoundProperties()
    {
    }

    public CompoundProperties(JSONObject object)
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
        phaseDiagram = PhaseDiagram.createPhaseDiagram(this);
    }

    private boolean meetsDiagramConditions()
    {
        return triplePointHeat_K.isNotZero()
                && triplePointPressure_kPa.isNotZero()
                && criticalPointHeat_K.isNotZero()
                && criticalPointPressure_kPa.isNotZero()
                && ((meltingPoint_K.isNotZero() && boilingPoint_K.isNotZero()) || sublimationPoint_K.isNotZero())
                && fusionHeat_kj.isNotZero()
                && vaporizationHeat_kj.isNotZero();
    }

    private void readProperties(JSONObject object)
    {
        name = object.getString("name");
        code = object.getString("symbol");
        orderNumber = object.optInt("number");
        defaultPhase = Phase.valueOf(object.getString("phase").toUpperCase());
        specificHeatCapacity_kj_mol_K = new BigDouble(object.getDouble("specificHeatCapacity")).setImmutable();
        meltingPoint_K = new BigDouble(object.optDouble("meltingPoint", 0.0)).setImmutable();
        boilingPoint_K = new BigDouble(object.optDouble("boilingPoint", 0.0)).setImmutable();
        sublimationPoint_K = new BigDouble(object.optDouble("sublimationPoint", 0.0)).setImmutable();
        fusionHeat_kj = new BigDouble(object.optDouble("fusionHeat")).setImmutable();
        vaporizationHeat_kj = new BigDouble(object.optDouble("vaporizationHeat")).setImmutable();
        triplePointHeat_K = new BigDouble(object.optDouble("triplePointHeat")).setImmutable();
        triplePointPressure_kPa = new BigDouble(object.optDouble("triplePointPressure")).setImmutable();
        criticalPointHeat_K = new BigDouble(object.optDouble("criticalPointHeat")).setImmutable();
        criticalPointPressure_kPa = new BigDouble(object.optDouble("criticalPointPressure")).setImmutable();
    }

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

    public boolean hasMeltingPoint()
    {
        return meltingPoint_K.isNotZero();
    }

    public boolean hasBoilingPoint()
    {
        return boilingPoint_K.isNotZero();
    }

    public boolean hasSublimationPoint()
    {
        return sublimationPoint_K.isNotZero();
    }

    public BigDouble getFusionHeat_kj()
    {
        return fusionHeat_kj;
    }

    public BigDouble getVaporizationHeat_kj()
    {
        return vaporizationHeat_kj;
    }

    public BigDouble getSublimationHeat_kj()
    {
        return fusionHeat_kj.add(vaporizationHeat_kj, new BigDouble());
    }

    public BigDouble getTriplePointPressure_kPa()
    {
        return triplePointPressure_kPa;
    }

    public BigDouble getCriticalPointPressure_kPa()
    {
        return criticalPointPressure_kPa;
    }

    public BigDouble minTriplePointEnergy_kj()
    {
        return triplePointHeat_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble());
    }

    public BigDouble meltingTriplePointEnergy_kj()
    {
        return minTriplePointEnergy_kj().add(fusionHeat_kj);
    }

    public BigDouble vaporizationTriplePointEnergy_kj()
    {
        return meltingTriplePointEnergy_kj().add(vaporizationHeat_kj);
    }

    public BigDouble minMeltingPointEnergy_kj()
    {
        return meltingPoint_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble());
    }

    public BigDouble maxMeltingPointEnergy_kj()
    {
        return minMeltingPointEnergy_kj().add(fusionHeat_kj);
    }

    public BigDouble minBoilingPointEnergy_kj()
    {
        return boilingPoint_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble()).add(fusionHeat_kj);
    }

    public BigDouble maxBoilingPointEnergy_kj()
    {
        return minBoilingPointEnergy_kj().add(vaporizationHeat_kj);
    }

    public BigDouble minCriticalPointEnergy_kj()
    {
        return criticalPointHeat_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble()).add(fusionHeat_kj);
    }

    public BigDouble maxCriticalPointEnergy_kj()
    {
        return minCriticalPointEnergy_kj().add(vaporizationHeat_kj);
    }

    public BigDouble minSublimationPointEnergy_kj()
    {
        return sublimationPoint_K.mul(specificHeatCapacity_kj_mol_K, new BigDouble());
    }

    public BigDouble meltingSublimationPointEnergy_kj()
    {
        return minSublimationPointEnergy_kj().add(fusionHeat_kj);
    }

    public BigDouble maxSublimationPointEnergy_kj()
    {
        return meltingSublimationPointEnergy_kj().add(vaporizationHeat_kj);
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

    public BigDouble getSublimationPoint_K()
    {
        return sublimationPoint_K;
    }

    public BigDouble getTriplePointHeat_K()
    {
        return triplePointHeat_K;
    }

    public BigDouble getCriticalPointHeat_K()
    {
        return criticalPointHeat_K;
    }

    public PhaseDiagram getPhaseDiagram()
    {
        return phaseDiagram;
    }

    @Override
    public String toString()
    {
        return "CompoundProperties{" + "name=" + name + ", code=" + code + ", specificHeatCapacity_kj_mol_K=" + specificHeatCapacity_kj_mol_K + ",\nmeltingPoint_K=" + meltingPoint_K + ", boilingPoint_K=" + boilingPoint_K + ", sublimationPoint_K=" + sublimationPoint_K + ",\nfusionHeat_kj=" + fusionHeat_kj + ", vaporizationHeat_kj=" + vaporizationHeat_kj + ",\ntriplePointHeat_K=" + triplePointHeat_K + ", triplePointPressure_kPa=" + triplePointPressure_kPa + ",\ncriticalPointHeat_K=" + criticalPointHeat_K + ", criticalPointPressure_kPa=" + criticalPointPressure_kPa + ",\nphaseDiagram=" + phaseDiagram.getDiagramCase() + '}';
    }
}
