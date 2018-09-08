/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.math.BigDouble;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class PhaseDiagram_Energy_Pressure {

    private final CompoundProperties properties;

    private final SublimationBorder sublimationBorder;
    private final MeltingBorder meltingBorder;
    private final VaporizationBorder vaporizationBorder;
    private final CriticalBorder criticalBorder;

    public PhaseDiagram_Energy_Pressure(CompoundProperties properties)
    {
        this.properties = properties;
        sublimationBorder = new SublimationBorder(properties);
        meltingBorder = new MeltingBorder(properties);
        vaporizationBorder = new VaporizationBorder(properties);
        criticalBorder = new CriticalBorder(properties);
    }

    public Phase getPhaseAt(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        if (pressure_kPa.isZero())
        {
            //if there's no pressure all compounds are assumed to be solid
            return Phase.SOLID;
        }
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

    private boolean isSolid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
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

    private boolean isLiquid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
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

    private boolean isGas(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        if (criticalBorder.isCritical(energy_kj_mol, pressure_kPa))
        {
            return false;
        }
        if (energy_kj_mol.compareTo(properties.maxTriplePointSublimationEnergy()) < 0) //below triplePoint is considered sublimating
        {
            return sublimationBorder.isSublimated(energy_kj_mol, pressure_kPa);
        } else
        {
            return vaporizationBorder.isVaporized(energy_kj_mol, pressure_kPa);
        }
    }

    private boolean isSupercriticalFluid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        return criticalBorder.isCritical(energy_kj_mol, pressure_kPa);
    }

    public BigDouble getTemperature_K_at(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        return getTemperature_K_at(energy_kj_mol, pressure_kPa, getPhaseAt(energy_kj_mol, pressure_kPa));
    }

    public BigDouble getTemperature_K_at(BigDouble energy_kj_mol, BigDouble pressure_kPa, Phase phase)
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

    private BigDouble getTemperature_K_ofSupercriticalFluid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        BigDouble energyForCalculation = new BigDouble(energy_kj_mol);
        energyForCalculation.sub(properties.getFusionHeat_kj());
        energyForCalculation.sub(properties.getVaporizationHeat_kj());
        return energyForCalculation.div(properties.getSpecificHeatCapacity_kj_mol_K());
    }

    private BigDouble getTemperature_K_ofGas(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        BigDouble energyForCalculation = new BigDouble(energy_kj_mol);
        energyForCalculation.sub(properties.getFusionHeat_kj());
        energyForCalculation.sub(properties.getVaporizationHeat_kj());
        return energyForCalculation.div(properties.getSpecificHeatCapacity_kj_mol_K());
    }

    private BigDouble getTemperature_K_ofLiquid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean isVaporizing = vaporizationBorder.isVaporizing(energy_kj_mol, pressure_kPa);
        BigDouble energyForCalculation;
        if (isVaporizing)
        {
            energyForCalculation = vaporizationBorder.getMinEnergy_kj_mol(pressure_kPa);
        } else
        {
            energyForCalculation = new BigDouble(energy_kj_mol);
        }
        energyForCalculation.sub(properties.getFusionHeat_kj());
        return energyForCalculation.div(properties.getSpecificHeatCapacity_kj_mol_K());
    }

    private BigDouble getTemperature_K_ofSolid(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        boolean isMelting;
        boolean isSublimating;
        if (pressure_kPa.isZero())
        {
            isMelting = false;
            isSublimating = false;
        } else
        {
            isMelting = meltingBorder.isMelting(energy_kj_mol, pressure_kPa);
            isSublimating = sublimationBorder.isSublimating(energy_kj_mol, pressure_kPa);
        }
        BigDouble energyForCalculation;
        if (isMelting && isSublimating)
        {
            // this is an edge case at the triple point
            // I assume that the melting slope is allways steeper than sublimation
            energyForCalculation = BigDouble.min(
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
            energyForCalculation = new BigDouble(energy_kj_mol);
        }
        return energyForCalculation.div(properties.getSpecificHeatCapacity_kj_mol_K());
    }

    public void render(PGraphics g)
    {
        BigDouble maxX = criticalBorder.getCriticalEnergy_kj_mol();
        BigDouble maxY = criticalBorder.getCriticalPressure_kPa();
        sublimationBorder.render(g, maxY, maxX);
        //meltingBorder.render(g, maxY, maxX);
        //vaporizationBorder.render(g, maxY, maxX);
//        g.strokeWeight(20);
//        g.stroke(0);
//        g.fill(0);
//        g.point(0, g.height - 0);
//        float x = (float) properties.minMeltingPointEnergy().div(maxX, new BigDouble()).mul(g.width, 0).toDouble();
//        float y = g.height - (float) ChemUtilities.STANDARD_PRESSURE_kPa.div(maxY, new BigDouble()).mul(g.height, 0).toDouble();
//        g.point(x, y);
//        //g.text("Melting|Std Pressure", x+50, y);
//        x = (float) properties.getFusionHeat_kj().div(maxX, new BigDouble()).mul(g.width, 0).toDouble();
//        y = g.height - 0;
//        g.point(x, y);
//        //g.text("Fusion|0", x+50, y);
//        x = (float) properties.maxBoilingEnergy().div(maxX, new BigDouble()).mul(g.width, 0).toDouble();
//        y = g.height - (float) ChemUtilities.STANDARD_PRESSURE_kPa.div(maxY, new BigDouble()).mul(g.height, 0).toDouble();
//        g.point(x, y);
//        //g.text("Boiling|Std Pressure", x+50, y);
//        x = (float) properties.minTriplePointEnergy().div(maxX, new BigDouble()).mul(g.width, 0).toDouble();
//        y = g.height - (float) properties.getTriplePointPressure_kPa().div(maxY, new BigDouble()).mul(g.height, 0).toDouble();
//        g.point(x, y);
//        //g.text("Min Triple Point", x+50, y);
//        x = (float) properties.maxTriplePointSublimationEnergy().div(maxX, new BigDouble()).mul(g.width, 0).toDouble();
//        y = g.height - (float) properties.getTriplePointPressure_kPa().div(maxY, new BigDouble()).mul(g.height, 0).toDouble();
//        g.point(x, y);
//        //g.text("Max Triple Point", x+50, y);
    }

    public SublimationBorder getSublimationBorder()
    {
        return sublimationBorder;
    }

    public MeltingBorder getMeltingBorder()
    {
        return meltingBorder;
    }

    public VaporizationBorder getVaporizationBorder()
    {
        return vaporizationBorder;
    }

    public CriticalBorder getCriticalBorder()
    {
        return criticalBorder;
    }
}
