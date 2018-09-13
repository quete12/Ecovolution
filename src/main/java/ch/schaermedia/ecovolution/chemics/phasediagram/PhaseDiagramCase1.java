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
 * Conditions for Case1:
 * <ul>
 * <li> triplePointPressure < Standardpressure <li> meltingPointTemperature >
 * triplePointTemperature
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
public class PhaseDiagramCase1 extends PhaseDiagram {

    private LinearFunction sublimationMin;
    private LinearFunction sublimationMax;

    private LinearFunction meltingMin;
    private LinearFunction meltingMax;

    private LinearFunction vaporization1Min;
    private LinearFunction vaporization1Max;

    private LinearFunction vaporization2Min;
    private LinearFunction vaporization2Max;

    public PhaseDiagramCase1(CompoundProperties properties)
    {
        super(properties, "Case 1");
    }

    protected boolean isSolid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean sublimation = sublimationMax.isPointOnOrOver(energy_kj_mol, pressure_kPa);
        boolean melting = meltingMax.isPointOnOrLeft(energy_kj_mol, pressure_kPa);
        return sublimation && melting;
    }

    protected boolean isLiquid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean melting = meltingMax.isPointRight(energy_kj_mol, pressure_kPa);
        boolean vaporization1 = vaporization1Max.isPointOnOrOver(energy_kj_mol, pressure_kPa);
        boolean vaporization2 = vaporization2Max.isPointOnOrOver(energy_kj_mol, pressure_kPa);
        return melting && (vaporization1 || vaporization2);
    }

    protected boolean isGas(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean sublimation = sublimationMax.isPointUnder(energy_kj_mol, pressure_kPa);
        boolean vaporization1 = vaporization1Max.isPointUnder(energy_kj_mol, pressure_kPa);
        boolean vaporization2 = vaporization2Max.isPointUnder(energy_kj_mol, pressure_kPa);
        boolean critical = pressure_kPa.compareTo(properties.getCriticalPointPressure_kPa()) < 0;

        return (sublimation || vaporization1 || vaporization2) && critical;
    }

    protected boolean isCritical(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean pressure = pressure_kPa.compareTo(properties.getCriticalPointPressure_kPa()) > 0;
        boolean energy = energy_kj_mol.compareTo(properties.maxCriticalPointEnergy_kj()) > 0;
        return pressure && energy;
    }

    protected BigDouble getEnergyForSolidTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        BigDouble energyForTemp;
        if (sublimationMin.isPointUnder(energy_kj_mol, pressure_kPa))
        {
            energyForTemp = sublimationMin.x(pressure_kPa);
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
        if (vaporization1Min.isPointUnder(energy_kj_mol, pressure_kPa))
        {
            energyForTemp = vaporization1Min.x(pressure_kPa);
        } else if (vaporization2Min.isPointUnder(energy_kj_mol, pressure_kPa))
        {
            energyForTemp = vaporization2Min.x(pressure_kPa);
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

    @Override
    protected void initBorders()
    {
        initSublimation();
        initMelting();
        initVaporization();
    }

    private void initSublimation()
    {
        sublimationMin = new LinearFunction(
                BigDouble.ZERO,
                BigDouble.ZERO,
                properties.minTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                true);
        sublimationMax = new LinearFunction(
                properties.getSublimationHeat_kj(),
                BigDouble.ZERO,
                properties.vaporizationTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                true);
    }

    private void initMelting()
    {
        meltingMin = new LinearFunction(
                properties.minTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.minMeltingPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa);
        meltingMin.limitMinX(properties.minTriplePointEnergy_kj());
        meltingMin.limitMinY(properties.getTriplePointPressure_kPa());

        meltingMax = new LinearFunction(
                properties.meltingTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.maxMeltingPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa);
        meltingMax.limitMinX(properties.meltingTriplePointEnergy_kj());
        meltingMax.limitMinY(properties.getTriplePointPressure_kPa());
    }

    private void initVaporization()
    {
        vaporization1Min = new LinearFunction(
                properties.meltingTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.minBoilingPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                true);
        vaporization1Max = new LinearFunction(
                properties.vaporizationTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.maxBoilingPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                true);

        vaporization2Min = new LinearFunction(
                properties.minBoilingPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.minCriticalPointEnergy_kj(),
                properties.getCriticalPointPressure_kPa(),
                true);
        vaporization2Max = new LinearFunction(
                properties.maxBoilingPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa,
                properties.maxCriticalPointEnergy_kj(),
                properties.getCriticalPointPressure_kPa(),
                true);
    }

}
