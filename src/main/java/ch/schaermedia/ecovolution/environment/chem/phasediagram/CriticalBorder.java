/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.CompoundProperties;

/**
 *
 * @author Quentin
 */
public class CriticalBorder extends PhaseBorder{
    private final double criticalEnergy_kj_mol;
    private final double criticalPressure_kPa;

    public CriticalBorder(CompoundProperties properties)
    {
        super(false);
        criticalPressure_kPa = properties.getCriticalPointPressure_kPa();
        criticalEnergy_kj_mol = properties.maxCriticalEnergy();
    }

    public boolean isCritical(double energy_kj_mol, double pressure_kPa)
    {
        return pressure_kPa > criticalPressure_kPa
                && energy_kj_mol > criticalEnergy_kj_mol;
    }

    @Override
    public double getMinEnergy_kj_mol(double pressure_kPa)
    {
        return criticalEnergy_kj_mol;
    }


}
