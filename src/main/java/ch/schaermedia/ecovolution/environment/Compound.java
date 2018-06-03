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

    public void addEnergy(double add_j){
        if(add_j<0){
            //TODO: add proper error handling
            //Maybe even allow removeing energy this way ??
            System.out.println("Negative add!!");
            return;
        }
        energy_j += add_j;
    }

    public double getTemperature_K() {
        if (energy_j < energyToMeltingPoint_j()) {
            return temperature(energy_j);
        }else if( energy_j <energyToMeltingPoint_j() + energyRequiredToMelt_j()){
            return properties.getMeltingPoint();
        }
        if (energy_j < energyToBoilingPoint_j()) {
            return temperature(energy_j - energyRequiredToMelt_j());
        }else if(energy_j < energyToBoilingPoint_j() + energyRequiredToVaporize_j()){
            return properties.getBoilingPoint();
        }
        return temperature(energy_j - energyRequiredToMelt_j() - energyRequiredToVaporize_j());
    }

    private double temperature(double energyForTemperature_j) {
        return (energyForTemperature_j / amount_mol) / properties.getSpecificHeatCapacity();
    }

    public Phase getPhase() {
        if (energy_j < energyToMeltingPoint_j() + energyRequiredToMelt_j()) {
            return Phase.SOLID;
        }
        if (energy_j < energyToBoilingPoint_j() + energyRequiredToVaporize_j()) {
            return Phase.LIQUID;
        }
        return Phase.GAS;
    }

    public boolean isMelted() {
        return energy_j > energyToMeltingPoint_j() + energyRequiredToMelt_j();
    }

    public boolean isVaporized() {
        return energy_j > energyToBoilingPoint_j() + energyRequiredToVaporize_j();
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

    public double getAmount_mol() {
        return amount_mol;
    }

    public void setAmount_mol(double amount_mol) {
        this.amount_mol = amount_mol;
    }

}
