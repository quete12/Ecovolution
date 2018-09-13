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
 *
 * @author Quentin
 */
public class PhaseDiagramCase2 extends PhaseDiagram {

    private LinearFunction sublimationMin;
    private LinearFunction sublimationMax;

    private LinearFunction meltingMin;
    private LinearFunction meltingMax;

    private LinearFunction vaporization1Min;
    private LinearFunction vaporization1Max;

    private LinearFunction vaporization2Min;
    private LinearFunction vaporization2Max;

    private BigDouble criticalPointMaxEnergy;
    private BigDouble criticalPointPressure;

    protected boolean isSolid(BigDouble pressure_kPa, BigDouble energy_kj_mol)
    {
        boolean sublimation = sublimationMax.isPointOnOrOver(pressure_kPa, energy_kj_mol);
        boolean melting = meltingMax.isPointOnOrLeft(pressure_kPa, energy_kj_mol);
        return sublimation && melting;
    }

    protected boolean isLiquid(BigDouble pressure_kPa, BigDouble energy_kj_mol)
    {
        boolean melting = meltingMax.isPointRight(pressure_kPa, energy_kj_mol);
        boolean vaporization1 = vaporization1Max.isPointOnOrOver(pressure_kPa, energy_kj_mol);
        boolean vaporization2 = vaporization2Max.isPointOnOrOver(pressure_kPa, energy_kj_mol);
        return melting && (vaporization1 || vaporization2);
    }

    protected boolean isGas(BigDouble pressure_kPa, BigDouble energy_kj_mol)
    {
        boolean sublimation = sublimationMax.isPointUnder(pressure_kPa, energy_kj_mol);
        boolean vaporization1 = vaporization1Max.isPointUnder(pressure_kPa, energy_kj_mol);
        boolean vaporization2 = vaporization2Max.isPointUnder(pressure_kPa, energy_kj_mol);
        boolean critical = pressure_kPa.compareTo(criticalPointPressure) < 0;

        return sublimation && (vaporization1 || vaporization2) && critical;
    }

    protected boolean isCritical(BigDouble pressure_kPa, BigDouble energy_kj_mol)
    {
        boolean pressure = pressure_kPa.compareTo(criticalPointPressure) > 0;
        boolean energy = energy_kj_mol.compareTo(criticalPointMaxEnergy) > 0;
        return pressure && energy;
    }

    @Override
    protected void initBorders(CompoundProperties properties)
    {
        initSublimation(properties);
        initMelting(properties);
        initVaporization(properties);
        this.criticalPointMaxEnergy = properties.maxCriticalPointEnergy_kj();
        this.criticalPointPressure = properties.getCriticalPointPressure_kPa();
    }

    private void initSublimation(CompoundProperties properties)
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

    private void initMelting(CompoundProperties properties)
    {
        meltingMin = new LinearFunction(
                properties.minTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.minMeltingPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa);
        meltingMin.limitMinX(BigDouble.ZERO);
        meltingMin.limitMaxX(properties.minTriplePointEnergy_kj());
        meltingMin.limitMinY(properties.getTriplePointPressure_kPa());

        meltingMax = new LinearFunction(
                properties.meltingTriplePointEnergy_kj(),
                properties.getTriplePointPressure_kPa(),
                properties.maxMeltingPointEnergy_kj(),
                ChemUtilities.STANDARD_PRESSURE_kPa);
        meltingMax.limitMinX(BigDouble.ZERO);
        meltingMax.limitMaxX(properties.meltingTriplePointEnergy_kj());
        meltingMax.limitMinY(properties.getTriplePointPressure_kPa());
    }

    private void initVaporization(CompoundProperties properties)
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
