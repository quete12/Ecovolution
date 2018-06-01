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

    public Phase getPhase(){
        if(energy_j < totalEnergyToMelt_j()){
            return Phase.SOLID;
        }
        if(energy_j < totalEnergyToVaporize_j()){
            return Phase.LIQUID;
        }
        return Phase.GAS;
    }

    private double energyCapacityToMelting_j() {
        return properties.getMeltingPoint() * amount_mol;
    }

    private double energyRequiredToMelt_j() {
        return properties.getFusionHeat() * amount_mol;
    }

    private double totalEnergyToMelt_j() {
        return energyCapacityToMelting_j() + energyRequiredToMelt_j();
    }

    private double energyCapacityToBoiling_j() {
        return properties.getBoilingPoint() * amount_mol;
    }

    private double energyRequiredToVaporize_j() {
        return properties.getVaporizationHeat() * amount_mol;
    }

    private double totalEnergyToVaporize_j() {
        return totalEnergyToMelt_j() + energyCapacityToBoiling_j() + energyRequiredToVaporize_j();
    }

}
