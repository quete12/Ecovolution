/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.CompoundProperties;
import ch.schaermedia.ecovolution.environment.chem.Phase;

/**
 *
 * @author Quentin
 */
public class PhaseDiagram_Energy_Pressure2 {

    private final CompoundProperties properties;

    private final SublimationBorder sublimationBorder;
    private final MeltingBorder meltingBorder;
    private final VaporizationBorder vaporizationBorder;
    private final CriticalBorder criticalBorder;

    public PhaseDiagram_Energy_Pressure2(CompoundProperties properties)
    {
        this.properties = properties;
        sublimationBorder = new SublimationBorder(properties);
        meltingBorder = new MeltingBorder(properties);
        vaporizationBorder = new VaporizationBorder(properties);
        criticalBorder = new CriticalBorder(properties);
    }

    public Phase getPhaseAt(double energy_kj_mol, double pressure_kPa)
    {
        if (isSupercriticalFluid(energy_kj_mol, pressure_kPa))
        {
            return Phase.SUPERCRITICAL_FLUID;
        }
        if (isGas(energy_kj_mol, pressure_kPa))
        {
            return Phase.GAS;
        }
        if (isLiquid(energy_kj_mol, pressure_kPa))
        {
            return Phase.LIQUID;
        }
        if (isSolid(energy_kj_mol, pressure_kPa))
        {
            return Phase.SOLID;
        }
        throw new AssertionError("Invalid Conditions for e= " + energy_kj_mol + "kj/mol and p= " + pressure_kPa + "kPa");
    }

    private boolean isSolid(double energy_kj_mol, double pressure_kPa)
    {
        if (criticalBorder.isCritical(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (meltingBorder.isMelted(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        return !sublimationBorder.isSublimated(energy_kj_mol, pressure_kPa);
    }

    private boolean isLiquid(double energy_kj_mol, double pressure_kPa)
    {
        if (criticalBorder.isCritical(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (vaporizationBorder.isVaporized(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        return meltingBorder.isMelted(energy_kj_mol, pressure_kPa);
    }

    private boolean isGas(double energy_kj_mol, double pressure_kPa)
    {
        if (criticalBorder.isCritical(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (energy_kj_mol < properties.minTriplePointEnergy()) //below triplePoint is considered sublimating
        {
            return sublimationBorder.isSublimated(energy_kj_mol, pressure_kPa);
        } else
        {
            return vaporizationBorder.isVaporized(energy_kj_mol, pressure_kPa);
        }
    }

    private boolean isSupercriticalFluid(double energy_kj_mol, double pressure_kPa)
    {
        return criticalBorder.isCritical(energy_kj_mol, pressure_kPa);
    }

    private double getTemperature_K_ofSolid(double energy_kj_mol, double pressure_kPa)
    {
        boolean isMelting = meltingBorder.isMelting(energy_kj_mol, pressure_kPa);
        boolean isSublimating = sublimationBorder.isSublimating(energy_kj_mol, pressure_kPa);
        double energyForCalculation;
        if (isMelting && isSublimating)
        {
            // this is an edge case at the triple point
            // I assume that the melting slope is allways steeper than sublimation
            energyForCalculation = Math.min(
                    meltingBorder.getMinEnergy_kj_mol(pressure_kPa),
                    sublimationBorder.getMinEnergy_kj_mol(pressure_kPa));
        } else if (isMelting)
        {
            energyForCalculation = meltingBorder.getMinEnergy_kj_mol(pressure_kPa);
        } else if (isSublimating)
        {
            energyForCalculation = sublimationBorder.getMinEnergy_kj_mol(pressure_kPa);
        } else
        {
            energyForCalculation = energy_kj_mol;
        }
        return energyForCalculation / properties.getSpecificHeatCapacity_kj_mol_K();
    }
}
