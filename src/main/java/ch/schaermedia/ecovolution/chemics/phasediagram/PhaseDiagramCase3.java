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
public class PhaseDiagramCase3 extends PhaseDiagram {

    private LinearFunction sublimation1Min;
    private LinearFunction sublimation1Max;

    private LinearFunction sublimation2Min;
    private LinearFunction sublimation2Max;

    private LinearFunction meltingMin;
    private LinearFunction meltingMax;

    private LinearFunction vaporizationMin;
    private LinearFunction vaporizationMax;

    private BigDouble criticalPointMaxEnergy;
    private BigDouble criticalPointPressure;

    protected boolean isSolid(BigDouble pressure_kPa, BigDouble energy_kj_mol)
    {
        boolean sublimation1 = sublimation1Max.isPointOnOrOver(pressure_kPa, energy_kj_mol);
        boolean sublimation2 = sublimation2Max.isPointOnOrOver(pressure_kPa, energy_kj_mol);
        boolean melting = meltingMax.isPointOnOrLeft(pressure_kPa, energy_kj_mol);
        return (sublimation1 || sublimation2) && melting;
    }

    protected boolean isLiquid(BigDouble pressure_kPa, BigDouble energy_kj_mol)
    {
        boolean melting = meltingMax.isPointRight(pressure_kPa, energy_kj_mol);
        boolean vaporization = vaporizationMax.isPointOnOrOver(pressure_kPa, energy_kj_mol);
        return melting && vaporization;
    }

    protected boolean isGas(BigDouble pressure_kPa, BigDouble energy_kj_mol)
    {
        boolean sublimation1 = sublimation1Max.isPointUnder(pressure_kPa, energy_kj_mol);
        boolean sublimation2 = sublimation2Max.isPointUnder(pressure_kPa, energy_kj_mol);
        boolean vaporization = vaporizationMax.isPointUnder(pressure_kPa, energy_kj_mol);
        boolean critical = pressure_kPa.compareTo(criticalPointPressure) < 0;

        return (sublimation1 || sublimation2) && vaporization && critical;
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

    private void initMelting(CompoundProperties properties)
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

    private void initVaporization(CompoundProperties properties)
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
