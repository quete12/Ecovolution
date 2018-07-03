/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.compound.Phase;
import ch.schaermedia.ecovolution.environment.chem.properties.ElementProperties;

/**
 *
 * @author Quentin
 */
public class PhaseDiagram_Energy_Pressure {

    private final ElementProperties properties;

    private final SublimationBorder sublimationBorder;
    private final MeltingBorder meltingBorder;
    private final VaporizationBorder vaporizationBorder;
    private final CriticalBorder criticalBorder;

    public PhaseDiagram_Energy_Pressure(ElementProperties properties)
    {
        this.properties = properties;
        sublimationBorder = new SublimationBorder(properties);
        meltingBorder = new MeltingBorder(properties);
        vaporizationBorder = new VaporizationBorder(properties);
        criticalBorder = new CriticalBorder(properties);
    }

    public Phase getPhaseAt(long energy_kj_mol, long pressure_kPa)
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
        throw new AssertionError("Invalid Conditions for element: " + properties.getCode() + " e= " + energy_kj_mol + "kj/mol and p= " + pressure_kPa + "kPa");
    }

    private boolean isSolid(long energy_kj_mol, long pressure_kPa)
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

    private boolean isLiquid(long energy_kj_mol, long pressure_kPa)
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

    private boolean isGas(long energy_kj_mol, long pressure_kPa)
    {
        if (criticalBorder.isCritical(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (energy_kj_mol < properties.maxTriplePointSublimationEnergy()) //below triplePoint is considered sublimating
        {
            return sublimationBorder.isSublimated(energy_kj_mol, pressure_kPa);
        } else
        {
            return vaporizationBorder.isVaporized(energy_kj_mol, pressure_kPa);
        }
    }

    private boolean isSupercriticalFluid(long energy_kj_mol, long pressure_kPa)
    {
        return criticalBorder.isCritical(energy_kj_mol, pressure_kPa);
    }

    public long getTemperature_K_at(long energy_kj_mol, long pressure_kPa)
    {
        return getTemperature_K_at(energy_kj_mol, pressure_kPa, getPhaseAt(energy_kj_mol, pressure_kPa));
    }

    public long getTemperature_K_at(long energy_kj_mol, long pressure_kPa, Phase phase)
    {
        switch (phase)
        {
            case SOLID:
                return getTemperature_K_ofSolid(energy_kj_mol, pressure_kPa);
            case LIQUID:
                return getTemperature_K_ofLiquid(energy_kj_mol, pressure_kPa);
            case GAS:
                return getTemperature_K_ofGas(energy_kj_mol, pressure_kPa);
            case SUPERCRITICAL_FLUID:
                return getTemperature_K_ofSupercriticalFluid(energy_kj_mol, pressure_kPa);
            default:
                throw new AssertionError(phase.name());
        }
    }

    private long getTemperature_K_ofSupercriticalFluid(long energy_kj_mol, long pressure_kPa)
    {
        long energyForCalculation = energy_kj_mol;
        energyForCalculation -= properties.getFusionHeat_kj();
        energyForCalculation -= properties.getVaporizationHeat_kj();
        return energyForCalculation / properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private long getTemperature_K_ofGas(long energy_kj_mol, long pressure_kPa)
    {
        long energyForCalculation = energy_kj_mol;
        energyForCalculation -= properties.getFusionHeat_kj();
        energyForCalculation -= properties.getVaporizationHeat_kj();
        return energyForCalculation / properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private long getTemperature_K_ofLiquid(long energy_kj_mol, long pressure_kPa)
    {
        boolean isVaporizing = vaporizationBorder.isVaporizing(energy_kj_mol, pressure_kPa);
        long energyForCalculation;
        if (isVaporizing)
        {
            energyForCalculation = vaporizationBorder.getMinEnergy_kj_mol(pressure_kPa);
        } else
        {
            energyForCalculation = energy_kj_mol;
        }
        energyForCalculation -= properties.getFusionHeat_kj();
        return energyForCalculation / properties.getSpecificHeatCapacity_kj_mol_K();
    }

    private long getTemperature_K_ofSolid(long energy_kj_mol, long pressure_kPa)
    {
        boolean isMelting = meltingBorder.isMelting(energy_kj_mol, pressure_kPa);
        boolean isSublimating = sublimationBorder.isSublimating(energy_kj_mol, pressure_kPa);
        long energyForCalculation;
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
