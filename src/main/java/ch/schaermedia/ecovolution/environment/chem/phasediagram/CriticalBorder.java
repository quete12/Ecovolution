/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.properties.ElementProperties;

/**
 *
 * @author Quentin
 */
public class CriticalBorder extends PhaseBorder{
    private final long criticalEnergy_kj_mol;
    private final long criticalPressure_kPa;

    public CriticalBorder(ElementProperties properties)
    {
        super(false);
        criticalPressure_kPa = properties.getCriticalPointPressure_kPa();
        criticalEnergy_kj_mol = properties.maxCriticalEnergy();
    }

    public boolean isCritical(long energy_kj_mol, long pressure_kPa)
    {
        return pressure_kPa > criticalPressure_kPa
                && energy_kj_mol > criticalEnergy_kj_mol;
    }

    @Override
    public long getMinEnergy_kj_mol(long pressure_kPa)
    {
        return criticalEnergy_kj_mol;
    }


}
