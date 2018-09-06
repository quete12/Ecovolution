
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.old.ecovolution.environment.chem.properties.ElementProperties;
import ch.schaermedia.old.ecovolution.general.math.BigDouble;
import ch.schaermedia.old.ecovolution.general.math.Consts;
import ch.schaermedia.old.ecovolution.general.math.Function;
import ch.schaermedia.old.ecovolution.general.math.LinearFunction;

/**
 *
 * @author Quentin
 */
public class MeltingBorder extends PhaseBorder {

    private Function meltingMin;
    private Function meltingMax;

    public MeltingBorder(ElementProperties properties)
    {
        super(false);
        initMeltingBorders(properties);
    }

    private void initMeltingBorders(ElementProperties properties)
    {
        meltingMin = new LinearFunction(
                properties.minTriplePointEnergy(),
                properties.getTriplePointPressure_kPa(),
                properties.minMeltingPointEnergy(),
                Consts.STANDARD_PRESSURE_kPa);
        BigDouble highPressure = properties.getCriticalPointPressure_kPa();
        BigDouble meltingEnergyAtHighPressure = meltingMin.x(highPressure);
        meltingMax = new LinearFunction(
                properties.maxTriplePointMeltingEnergy(),
                properties.getTriplePointPressure_kPa(),
                meltingEnergyAtHighPressure.add(properties.getFusionHeat_kj()),
                highPressure);

        if (meltingMin.isPositive() && meltingMax.isPositive())
        {
            if (properties.isMeltingPointUnderTriplePoint())
            {
                meltingMin.limitMinX(properties.minMeltingPointEnergy());
                meltingMin.limitMinY(Consts.STANDARD_PRESSURE_kPa);
                meltingMax.limitMinX(properties.maxMeltingPointEnergy());
                meltingMax.limitMinY(Consts.STANDARD_PRESSURE_kPa);
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
                meltingMin.limitMinY(Consts.STANDARD_PRESSURE_kPa);
                meltingMax.limitMaxX(properties.maxMeltingPointEnergy());
                meltingMax.limitMinY(Consts.STANDARD_PRESSURE_kPa);
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
