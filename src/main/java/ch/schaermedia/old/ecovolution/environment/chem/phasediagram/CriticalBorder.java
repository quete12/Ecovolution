/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.old.ecovolution.environment.chem.properties.ElementProperties;
import ch.schaermedia.old.ecovolution.general.math.BigDouble;

/**
 *
 * @author Quentin
 */
public class CriticalBorder extends PhaseBorder{
    private final BigDouble criticalEnergy_kj_mol;
    private final BigDouble criticalPressure_kPa;

    public CriticalBorder(ElementProperties properties)
    {
        super(false);
        criticalPressure_kPa = properties.getCriticalPointPressure_kPa();
        criticalEnergy_kj_mol = properties.maxCriticalEnergy();
    }

    public boolean isCritical(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        return pressure_kPa.compareTo(criticalPressure_kPa)>0
                && energy_kj_mol.compareTo(criticalEnergy_kj_mol)>0;
    }

    @Override
    public BigDouble getMinEnergy_kj_mol(BigDouble pressure_kPa)
    {
        return criticalEnergy_kj_mol;
    }


}
