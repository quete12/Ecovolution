/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.math.BigDouble;

/**
 *
 * @author Quentin
 */
public abstract class PhaseDiagram {

    public static PhaseDiagram createPhaseDiagram(CompoundProperties properties)
    {
        PhaseDiagram diag = null;
        if (isCase1(properties))
        {
            diag = new PhaseDiagramCase1(properties);
        } else if (isCase2(properties))
        {
            diag = new PhaseDiagramCase2(properties);
        } else if (isCase3(properties))
        {
            diag = new PhaseDiagramCase3(properties);
        } else
        {
            throw new RuntimeException("No mating diagram type for properties: \n" + properties);
        }
        diag.init();
        return diag;
    }

    /**
     * This function is used to descide what structure the phasediagram should
     * have.
     *
     * @return
     */
    private static boolean isCase1(CompoundProperties properties)
    {
        boolean hasSublimationPoint = properties.hasSublimationPoint();
        boolean pressureResult = properties.getTriplePointPressure_kPa().compareTo(ChemUtilities.STANDARD_PRESSURE_kPa) < 0;
        boolean temperatureResult = properties.getTriplePointHeat_K().compareTo(properties.getMeltingPoint_K()) < 0;
        return !hasSublimationPoint && pressureResult && temperatureResult;
    }

    private static boolean isCase2(CompoundProperties properties)
    {
        boolean hasSublimationPoint = properties.hasSublimationPoint();
        boolean pressureResult = properties.getTriplePointPressure_kPa().compareTo(ChemUtilities.STANDARD_PRESSURE_kPa) < 0;
        boolean temperatureResult = properties.getTriplePointHeat_K().compareTo(properties.getMeltingPoint_K()) > 0;
        return !hasSublimationPoint && pressureResult && temperatureResult;
    }

    private static boolean isCase3(CompoundProperties properties)
    {
        boolean hasSublimationPoint = properties.hasSublimationPoint();
        boolean pressureResult = properties.getTriplePointPressure_kPa().compareTo(ChemUtilities.STANDARD_PRESSURE_kPa) > 0;
        boolean temperatureResult = properties.getSublimationPoint_K().compareTo(properties.getTriplePointHeat_K()) < 0;
        return hasSublimationPoint && pressureResult && temperatureResult;
    }

    protected final CompoundProperties properties;
    private final String diagramCase;

    protected PhaseDiagram(CompoundProperties properties, String diagramCase)
    {
        this.properties = properties;
        this.diagramCase = diagramCase;
    }

    protected void init()
    {
        initBorders();
    }

    protected abstract void initBorders();

    public String getDiagramCase()
    {
        return diagramCase;
    }

    public Phase getPhase(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        if (isSolid(energy_kj_mol, pressure_kPa))
        {
            return Phase.SOLID;
        }
        if (isLiquid(energy_kj_mol, pressure_kPa))
        {
            return Phase.LIQUID;
        }
        if (isGas(energy_kj_mol, pressure_kPa))
        {
            return Phase.GAS;
        }
        if (isCritical(energy_kj_mol, pressure_kPa))
        {
            return Phase.SUPERCRITICAL_FLUID;
        }
        throw new RuntimeException("Could not find phase for: " + pressure_kPa + " kPa and " + energy_kj_mol + " kj"
                + "\nDiagram case: " + diagramCase + " compound: " + properties.getCode());
    }

    protected abstract boolean isSolid(BigDouble energy_kj_mol, BigDouble pressure_kPa);

    protected abstract boolean isLiquid(BigDouble energy_kj_mol, BigDouble pressure_kPa);

    protected abstract boolean isGas(BigDouble energy_kj_mol, BigDouble pressure_kPa);

    protected abstract boolean isCritical(BigDouble energy_kj_mol, BigDouble pressure_kPa);

    public BigDouble getTemperature(BigDouble energy_kj_mol, BigDouble pressure_kPa)
    {
        return getTemperature(energy_kj_mol, pressure_kPa, getPhase(energy_kj_mol, pressure_kPa));
    }

    public BigDouble getTemperature(BigDouble energy_kj_mol, BigDouble pressure_kPa, Phase phase)
    {
        BigDouble energyForCalculation;
        switch (phase)
        {
            case SOLID:
                energyForCalculation = getEnergyForSolidTemp(energy_kj_mol, pressure_kPa);
                break;
            case LIQUID:
                energyForCalculation = getEnergyForLiquidTemp(energy_kj_mol, pressure_kPa);
                break;
            case GAS:
                energyForCalculation = getEnergyForGasTemp(energy_kj_mol, pressure_kPa);
                break;
            case SUPERCRITICAL_FLUID:
                energyForCalculation = getEnergyForCriticalTemp(energy_kj_mol, pressure_kPa);
                break;
            default:
                throw new AssertionError(phase.name());
        }
        BigDouble temperature = energyForCalculation.div(properties.getSpecificHeatCapacity_kj_mol_K());
//        System.out.println("CalculatedTemperature: " + temperature);
        return temperature;
    }

    protected abstract BigDouble getEnergyForSolidTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa);

    protected abstract BigDouble getEnergyForLiquidTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa);

    protected abstract BigDouble getEnergyForGasTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa);

    protected abstract BigDouble getEnergyForCriticalTemp(BigDouble energy_kj_mol, BigDouble pressure_kPa);
}
