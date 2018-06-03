/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

/**
 *
 * @author Quentin
 */
public class Compound {

    private final CompoundProperties properties;
    private double amount_mol;
    private double energy_j;

    public Compound(CompoundProperties properties) {
        this.properties = properties;
    }

    public double getTemperature_K() {
        if (energy_j < energyToMeltingPoint_j() + energyRequiredToMelt_j()) {
            return (energy_j / amount_mol) / properties.getSpecificHeatCapacity();
        }
        if (energy_j < energyToBoilingPoint_j() + energyRequiredToVaporize_j()) {
            return ((energy_j - energyRequiredToMelt_j()) / amount_mol) / properties.getSpecificHeatCapacity();
        }
        return ((energy_j - energyRequiredToMelt_j() - energyRequiredToVaporize_j()) / amount_mol) / properties.getSpecificHeatCapacity();
    }

    public Phase getPhase(){
        if (energy_j < energyToMeltingPoint_j() + energyRequiredToMelt_j()) {
            return Phase.SOLID;
        }
        if (energy_j < energyToBoilingPoint_j() + energyRequiredToVaporize_j()) {
            return Phase.LIQUID;
        }
        return Phase.GAS;
    }

    private double energyToMeltingPoint_j() {
        return amount_mol * properties.getMeltingPoint() * properties.getSpecificHeatCapacity();
    }

    private double energyRequiredToMelt_j() {
        return amount_mol * properties.getFusionHeat();
    }

    private double energyToBoilingPoint_j() {
        double energyToBoilingPoint = amount_mol * properties.getBoilingPoint() * properties.getSpecificHeatCapacity();
        double result = energyToBoilingPoint + energyRequiredToMelt_j();
        return result;
    }

    private double energyRequiredToVaporize_j() {
        return amount_mol * properties.getVaporizationHeat();
    }

}
