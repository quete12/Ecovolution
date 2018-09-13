/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties.PhaseDiagramCase;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.math.BigDouble;

/**
 *
 * @author Quentin
 */
public abstract class PhaseDiagram {

    public static PhaseDiagram createPhaseDiagram(PhaseDiagramCase diagramCase, CompoundProperties properties)
    {
        PhaseDiagram diag = null;
        switch (diagramCase)
        {
            case CASE1:
                diag = new PhaseDiagramCase1();
                break;
            case CASE2:
                diag = new PhaseDiagramCase2();
                break;
            case CASE3:
                diag = new PhaseDiagramCase3();
                break;
        }
        diag.init(properties);
        return diag;
    }

    protected PhaseDiagram()
    {

    }

    protected void init(CompoundProperties properties)
    {
        initBorders(properties);
    }

    protected abstract void initBorders(CompoundProperties properties);

    public Phase getPhase(BigDouble pressure_kPa, BigDouble energy_kj_mol)
    {
        if (isSolid(pressure_kPa, energy_kj_mol))
        {
            return Phase.SOLID;
        }
        if (isLiquid(pressure_kPa, energy_kj_mol))
        {
            return Phase.LIQUID;
        }
        if (isGas(pressure_kPa, energy_kj_mol))
        {
            return Phase.GAS;
        }
        if (isCritical(pressure_kPa, energy_kj_mol))
        {
            return Phase.SUPERCRITICAL_FLUID;
        }
        throw new RuntimeException("Could not find phase for: " + pressure_kPa + " kPa and " + energy_kj_mol + " kj");
    }

    protected abstract boolean isSolid(BigDouble pressure_kPa, BigDouble energy_kj_mol);

    protected abstract boolean isLiquid(BigDouble pressure_kPa, BigDouble energy_kj_mol);

    protected abstract boolean isGas(BigDouble pressure_kPa, BigDouble energy_kj_mol);

    protected abstract boolean isCritical(BigDouble pressure_kPa, BigDouble energy_kj_mol);
}
