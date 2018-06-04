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
    private double energy_kj;

    public Compound(CompoundProperties properties) {
        this.properties = properties;
    }

    public void addEnergy(double add_kj){
        if(add_kj<0){
            //TODO: add proper error handling
            //Maybe even allow removeing energy this way ??
            System.out.println("Negative add!!");
            return;
        }
        energy_kj += add_kj;
    }

    public double getTemperature_K() {
        if (energy_kj < energyToMeltingPoint_kj()) {
            return temperature(energy_kj);
        }else if( energy_kj <energyToMeltingPoint_kj() + energyRequiredToMelt_kj()){
            return properties.getMeltingPoint();
        }
        if (energy_kj < energyToBoilingPoint_kj()) {
            return temperature(energy_kj - energyRequiredToMelt_kj());
        }else if(energy_kj < energyToBoilingPoint_kj() + energyRequiredToVaporize_kj()){
            return properties.getBoilingPoint();
        }
        return temperature(energy_kj - energyRequiredToMelt_kj() - energyRequiredToVaporize_kj());
    }

    private double temperature(double energyForTemperature_j) {
        return (energyForTemperature_j / amount_mol) / properties.getSpecificHeatCapacity();
    }

    public Phase getPhase() {
        if (energy_kj < energyToMeltingPoint_kj() + energyRequiredToMelt_kj()) {
            return Phase.SOLID;
        }
        if (energy_kj < energyToBoilingPoint_kj() + energyRequiredToVaporize_kj()) {
            return Phase.LIQUID;
        }
        return Phase.GAS;
    }

    public boolean isMelted() {
        return energy_kj > energyToMeltingPoint_kj() + energyRequiredToMelt_kj();
    }

    public boolean isVaporized() {
        return energy_kj > energyToBoilingPoint_kj() + energyRequiredToVaporize_kj();
    }

    private double energyToMeltingPoint_kj() {
        return amount_mol * properties.getMeltingPoint() * properties.getSpecificHeatCapacity();
    }

    private double energyRequiredToMelt_kj() {
        return amount_mol * properties.getFusionHeat();
    }

    private double energyToBoilingPoint_kj() {
        double energyToBoilingPoint = amount_mol * properties.getBoilingPoint() * properties.getSpecificHeatCapacity();
        double result = energyToBoilingPoint + energyRequiredToMelt_kj();
        return result;
    }

    private double energyRequiredToVaporize_kj() {
        return amount_mol * properties.getVaporizationHeat();
    }

    public double getAmount_mol() {
        return amount_mol;
    }

    public void setAmount_mol(double amount_mol) {
        this.amount_mol = amount_mol;
    }

    @Override
    public String toString() {
        return "Compound{" + "properties=" + properties + ", amount_mol=" + amount_mol + ", energy_j=" + energy_kj + '}';
    }

}
