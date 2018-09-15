/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.math.LinearFunction;

/**
 * Conditions for Case3:
 * <ul>
 * <li> triplePointPressure > Standardpressure
 * <li> sublimationPointTemperature < triplePointTemperature </ul> The Idea of
 * Case3 is to implement following connections:
 * <ul>
 * <li> sublimation line from 0 to sublimationPoint
 * <li> sublimation line from sublimationPoint to triplePoint
 * <li> melting line from triplePoint through sublimationPoint
 * <li> vaporization line from triplePoint to criticalPoint
 * </ul>
 * The Connections must implement following endpoint limits
 * <ul>
 * <li> sublimation 0 and aublimationPoint / sublimationPoint and triplePoint
 * <li> melting triplePoint to infinity (temperature limited by
 * criticalPointTemperature)
 * <li> vaporization triplePoint and criticalPoint
 * </ul>
 *
 */
public class PhaseDiagramCase3 extends PhaseDiagram {

    private LinearFunction sublimation1Min;
    private LinearFunction sublimation1Max;

    private LinearFunction sublimation2Min;
    private LinearFunction sublimation2Max;

    private LinearFunction meltingMin;
    private LinearFunction meltingMax;

    private LinearFunction vaporizationMin;
    private LinearFunction vaporizationMax;

    public PhaseDiagramCase3(CompoundProperties properties)
    {
        super(properties, "Case 3");
    }

    protected BigDouble getEnergyForSolidTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        BigDouble energyForTemp;
        if (sublimation1Min.isPointUnder(energy_kj_mol, pressure_kPa))
        {
            energyForTemp = sublimation1Min.x(pressure_kPa);
        } else if (sublimation2Min.isPointUnder(energy_kj_mol, pressure_kPa))
        {
            energyForTemp = sublimation2Min.x(pressure_kPa);
        } else if (meltingMin.isPointRight(energy_kj_mol, pressure_kPa))
        {
            energyForTemp = meltingMin.x(pressure_kPa);
        } else
        {
            energyForTemp = new BigDouble(energy_kj_mol);
        }
        return energyForTemp;
    }

    protected BigDouble getEnergyForLiquidTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        BigDouble energyForTemp;
        if (vaporizationMin.isPointUnder(energy_kj_mol, pressure_kPa))
        {
            energyForTemp = vaporizationMin.x(pressure_kPa);
        } else
        {
            energyForTemp = new BigDouble(energy_kj_mol);
        }
        return energyForTemp.sub(properties.getFusionHeat_kj());
    }

    protected BigDouble getEnergyForGasTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        BigDouble energyForTemp = energy_kj_mol.sub(properties.getFusionHeat_kj(), new BigDouble()).sub(properties.getVaporizationHeat_kj());
        return energyForTemp;
    }

    protected BigDouble getEnergyForCriticalTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        BigDouble energyForTemp = energy_kj_mol.sub(properties.getSublimationHeat_kj(), new BigDouble());
        return energyForTemp;
    }

    protected boolean isSolid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean sublimation1 = sublimation1Max.isPointOnOrOver(energy_kj_mol, pressure_kPa);
        boolean sublimation2 = sublimation2Max.isPointOnOrOver(energy_kj_mol, pressure_kPa);
        boolean melting = meltingMax.isPointOnOrLeft(energy_kj_mol, pressure_kPa);
        return sublimation1 || sublimation2 || melting;
    }

    protected boolean isLiquid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean melting = meltingMax.isPointRight(energy_kj_mol, pressure_kPa);
        boolean vaporization = vaporizationMax.isPointOnOrOver(energy_kj_mol, pressure_kPa);
        return melting && vaporization;
    }

    protected boolean isGas(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean sublimation1 = sublimation1Max.isPointUnder(energy_kj_mol, pressure_kPa);
        boolean sublimation2 = sublimation2Max.isPointUnder(energy_kj_mol, pressure_kPa);
        boolean vaporization = vaporizationMax.isPointUnder(energy_kj_mol, pressure_kPa);
        boolean critical = pressure_kPa.compareTo(properties.getCriticalPointPressure_kPa()) < 0;

        return (sublimation1 || sublimation2 || vaporization) && critical;
    }

    protected boolean isCritical(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean pressure = pressure_kPa.compareTo(properties.getCriticalPointPressure_kPa()) > 0;
        boolean energy = energy_kj_mol.compareTo(properties.maxCriticalPointEnergy_kj()) > 0;
        return pressure && energy;
    }

    @Override
    protected void initBorders()
    {
        initSublimation();
        initMelting();
        initVaporization();
    }

    private void initSublimation()
    {
        sublimation1Min = new LinearFunction(
                BigDouble.ZERO,
                BigDouble.ZERO,
                properties.minSublimationPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                true);
        sublimation1Max = new LinearFunction(
                properties.getSublimationHeat_kj(),
                BigDouble.ZERO,
                properties.maxSublimationPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                true);
        sublimation2Min = new LinearFunction(
                properties.minSublimationPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.minTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                true);
        sublimation2Max = new LinearFunction(
                properties.maxSublimationPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.vaporizationTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                true);
    }

    private void initMelting()
    {
        meltingMin = new LinearFunction(
                properties.minTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.minSublimationPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa);
        meltingMin.limitMinX(properties.minTriplePointEnergy_kj());
        meltingMin.limitMinY(properties.getTriplePointPressure_kPa());
        meltingMin.limitMaxX(properties.maxCriticalPointEnergy_kj());

        meltingMax = new LinearFunction(
                properties.meltingTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.meltingSublimationPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa);
        meltingMax.limitMinX(properties.meltingTriplePointEnergy_kj());
        meltingMax.limitMinY(properties.getTriplePointPressure_kPa());
        meltingMax.limitMaxX(properties.maxCriticalPointEnergy_kj());
    }

    private void initVaporization()
    {
        vaporizationMin = new LinearFunction(
                properties.meltingTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.minCriticalPointEnergy_kj(),
                properties.getCriticalPointPressure_kPa(),
                true);
        vaporizationMax = new LinearFunction(
                properties.vaporizationTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.maxCriticalPointEnergy_kj(),
                properties.getCriticalPointPressure_kPa(),
                true);
    }
}
