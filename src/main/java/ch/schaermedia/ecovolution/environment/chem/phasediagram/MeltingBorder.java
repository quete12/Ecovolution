/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import ch.schaermedia.ecovolution.environment.chem.ElementProperties;
import ch.schaermedia.ecovolution.general.math.Function;
import ch.schaermedia.ecovolution.general.math.LinearFunction;

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
                CompoundMix.STATIC_PRESSURE_kPa);

        double highPressure = properties.getCriticalPointPressure_kPa();
        double meltingEnergyAtHighPressure = meltingMin.x(highPressure);
        meltingMax = new LinearFunction(
                properties.maxTriplePointMeltingEnergy(),
                properties.getTriplePointPressure_kPa(),
                meltingEnergyAtHighPressure+properties.getFusionHeat_kj(),
                highPressure);
    }

    public boolean isMelted(double energy_kj_mol, double pressure_kPa)
    {
        return meltingMax.isPointOnOrRight(energy_kj_mol, pressure_kPa);
    }

    public boolean isMelting(double energy_kj_mol, double pressure_kPa)
    {
        if (isMelted(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        return meltingMin.isPointOnOrRight(energy_kj_mol, pressure_kPa);
    }

    @Override
    public double getMinEnergy_kj_mol(double pressure_kPa)
    {
        return meltingMin.x(pressure_kPa);
    }

}
