
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.math.Function;
import ch.schaermedia.ecovolution.math.LinearFunction;

/**
 *
 * @author Quentin
 */
public class MeltingBorder extends PhaseBorder {

    private Function meltingMin;
    private Function meltingMax;

    public MeltingBorder(CompoundProperties properties)
    {
        super(false);
        initMeltingBorders(properties);
        initLimits(properties);
    }

    private void initMeltingBorders(CompoundProperties properties)
    {
        meltingMin = new LinearFunction(
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa(),
                properties.minMeltingPointEnergy(),
                ChemUtilities.STANDARD_PRESSURE_kPa);
        BigDouble highPressure = properties.getCriticalPointPressure_kPa();
        BigDouble meltingEnergyAtHighPressure = meltingMin.x(highPressure);
        meltingMax = new LinearFunction(
                properties.maxTriplePointMeltingEnergy(),
                properties.getTriplePointPressure_kPa(),
                meltingEnergyAtHighPressure.add(properties.getFusionHeat_kj()),
                highPressure);

    }

    private void initLimits(CompoundProperties properties)
    {
        if (meltingMin.isPositive() && meltingMax.isPositive())
        {
            if (properties.isMeltingPointUnderTriplePoint())
            {
                meltingMin.limitMinX(properties.minMeltingPointEnergy());
                meltingMin.limitMinY(ChemUtilities.STANDARD_PRESSURE_kPa);
                meltingMax.limitMinX(properties.maxMeltingPointEnergy());
                meltingMax.limitMinY(ChemUtilities.STANDARD_PRESSURE_kPa);
            } else
            {
                meltingMin.limitMinX(properties.minTriplePointEnergy());
                meltingMin.limitMinY(properties.getTriplePointPressure_kPa());
                meltingMax.limitMinX(properties.maxTriplePointMeltingEnergy());
                meltingMax.limitMinY(properties.getTriplePointPressure_kPa());
            }
        } else
        {
            if (properties.isMeltingPointUnderTriplePoint())
            {
                meltingMin.limitMinX(BigDouble.ZERO);
                meltingMax.limitMinX(BigDouble.ZERO);
                meltingMin.limitMaxX(properties.minMeltingPointEnergy());
                meltingMin.limitMinY(ChemUtilities.STANDARD_PRESSURE_kPa);
                meltingMax.limitMaxX(properties.maxMeltingPointEnergy());
                meltingMax.limitMinY(ChemUtilities.STANDARD_PRESSURE_kPa);
            } else
            {
                meltingMin.limitMinX(BigDouble.ZERO);
                meltingMax.limitMinX(BigDouble.ZERO);
                meltingMin.limitMaxX(properties.minTriplePointEnergy());
                meltingMin.limitMinY(properties.getTriplePointPressure_kPa());
                meltingMax.limitMaxX(properties.maxTriplePointMeltingEnergy());
                meltingMax.limitMinY(properties.getTriplePointPressure_kPa());
            }
        }
    }

    public boolean isMelted(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        return meltingMax.isPointRight(energy_kj_mol, pressure_kPa);
    }

    public boolean isMelting(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        if (isMelted(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        return meltingMin.isPointRight(energy_kj_mol, pressure_kPa);
    }

    @Override
    public BigDouble getMinEnergy_kj_mol(BigDouble pressure_kPa)
    {
        return meltingMin.x(pressure_kPa);
    }
}
