/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.math.BigDouble;

/**
 *
 * @author Quentin
 */
public class CriticalBorder extends PhaseBorder{
    private final BigDouble criticalEnergy_kj_mol;
    private final BigDouble criticalPressure_kPa;

    public CriticalBorder(CompoundProperties properties)
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

    public BigDouble getCriticalEnergy_kj_mol()
    {
        return criticalEnergy_kj_mol;
    }

    public BigDouble getCriticalPressure_kPa()
    {
        return criticalPressure_kPa;
    }

}
