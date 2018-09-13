/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.atmospherics;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.phasediagram.PhaseDiagram_Energy_Pressure;
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

    private PhaseDiagram_Energy_Pressure energy_Pressure_Diagram;

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
        energy_Pressure_Diagram = new PhaseDiagram_Energy_Pressure(this);
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

    /**
     * This function is used to descide what structure the phasediagram should
     * have.
     *
     * @return
     */
    private boolean isCase1()
    {
        boolean hasSublimationPoint = hasSublimationPoint();
        boolean pressureResult = triplePointPressure_kPa.compareTo(ChemUtilities.STANDARD_PRESSURE_kPa) < 0;
        boolean temperatureResult = triplePointHeat_K.compareTo(meltingPoint_K) < 0;
        return !hasSublimationPoint && pressureResult && temperatureResult;
    }

    private boolean isCase2()
    {
        boolean hasSublimationPoint = hasSublimationPoint();
        boolean pressureResult = triplePointPressure_kPa.compareTo(ChemUtilities.STANDARD_PRESSURE_kPa) < 0;
        boolean temperatureResult = triplePointHeat_K.compareTo(meltingPoint_K) > 0;
        return !hasSublimationPoint && pressureResult && temperatureResult;
    }

    private boolean isCase3()
    {
        boolean hasSublimationPoint = hasSublimationPoint();
        boolean pressureResult = triplePointPressure_kPa.compareTo(ChemUtilities.STANDARD_PRESSURE_kPa) > 0;
        boolean temperatureResult = sublimationPoint_K.compareTo(triplePointHeat_K) < 0;
        return hasSublimationPoint && pressureResult && temperatureResult;
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
        return meltingTriplePointEnergy_kj().add(fusionHeat_kj);
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

    public BigDouble meltingSublimationPointEnergy_kj(){
        return minSublimationPointEnergy_kj().add(fusionHeat_kj);
    }
    public BigDouble maxSublimationPointEnergy_kj()
    {
        return meltingSublimationPointEnergy_kj().add(vaporizationHeat_kj);
    }

    public static enum PhaseDiagramCase {
        /**
         * Conditions for Case1:
         * <ul>
         * <li> triplePointPressure < Standardpressure <li>
         * meltingPointTemperature > triplePointTemperature
         * </ul>
         * The Idea of Case1 is to implement following connections:
         * <ul>
         * <li> sublimation line from 0 to triplePoint
         * <li> melting line from triplePoint through meltingPoint
         * <li> vaporization line from triplePoint to boilingPoint
         * <li> vaporization line from boilingPoint to criticalPoint
         * </ul>
         * The Connections must implement following endpoint limits
         * <ul>
         * <li> sublimation 0 and triplePoint
         * <li> melting triplePoint to infinity (temperature limited by
         * criticalPointTemperature)
         * <li> vaporization triplePoint and boilingPoint / boilingPoint and
         * criticalPoint
         * </ul>
         *
         */
        CASE1,
        /**
         * Conditions for Case2:
         * <ul>
         * <li> triplePointPressure < Standardpressure <li>
         * meltingPointTemperature < triplePointTemperature </ul> The Idea of
         * Case2 is to implement following connections:
         * <ul>
         * <li> sublimation line from 0 to triplePoint
         * <li> melting line from triplePoint through meltingPoint
         * <li> vaporization line from triplePoint to boilingPoint
         * <li> vaporization line from boilingPoint to criticalPoint
         * </ul>
         * The Connections must implement following endpoint limits
         * <ul>
         * <li> sublimation 0 and triplePoint
         * <li> melting triplePoint to infinity (pressure and temperature
         * limited by 0)
         * <li> vaporization triplePoint and boilingPoint / boilingPoint and
         * criticalPoint
         * </ul>
         *
         */
        CASE2,
        /**
         * Conditions for Case3:
         * <ul>
         * <li> triplePointPressure > Standardpressure
         * <li> sublimationPointTemperature < triplePointTemperature </ul> The
         * Idea of Case3 is to implement following connections:
         * <ul>
         * <li> sublimation line from 0 to sublimationPoint
         * <li> sublimation line from sublimationPoint to triplePoint
         * <li> melting line from triplePoint through sublimationPoint
         * <li> vaporization line from triplePoint to criticalPoint
         * </ul>
         * The Connections must implement following endpoint limits
         * <ul>
         * <li> sublimation 0 and aublimationPoint / sublimationPoint and
         * triplePoint
         * <li> melting triplePoint to infinity (temperature limited by
         * criticalPointTemperature)
         * <li> vaporization triplePoint and criticalPoint
         * </ul>
         *
         */
        CASE3;
    }
}
