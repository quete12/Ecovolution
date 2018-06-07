/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;

/**
 *
 * @author Quentin
 */
public class Compound {

    private final CompoundProperties properties;
    private double amount_mol;
    private double amountBuffer_mol;

    private double energy_kj;
    private double energyBuffer_kj;

    private double temperature_K;
    private Phase phase;

    public Compound(CompoundProperties properties) {
        this.properties = properties;
    }

    public double volume_L(double pressure_kPa) {
        return ChemUtilities.volume_L(pressure_kPa, amount_mol, temperature_K);
    }

    public double pressure_kPa(double volume_L) {
        return ChemUtilities.pressure_kPa(volume_L, amount_mol, temperature_K);
    }

    public void addEnergy(double add_kj) {
        if (add_kj < 0) {
            //TODO: add proper error handling
            //Maybe even allow removeing energy this way ??
            System.out.println("Negative add!!");
            return;
        }
        energyBuffer_kj += add_kj;
    }

    public void addAmount(double add_mol) {
        if (add_mol < 0) {
            //TODO: add proper error handling
            //Maybe even allow removeing moles this way ??
            System.out.println("Negative add!!");
            return;
        }
        amountBuffer_mol += add_mol;
    }

    public void importCompound(Compound com) {
        amountBuffer_mol += (com.getAmount_mol() + com.getAmountBuffer_mol());
        energyBuffer_kj += (com.getEnergy_kj() + com.getEnergyBuffer_kj());
    }

    public double splitDirectMoles(double percentage) {
        //maybe we should consider the buffer in this calculation even though it sould be 0 by the time this method gets called, just to avoid negative values
        double remove = amount_mol * percentage;
        amount_mol -= remove;
        return remove;
    }

    public double splitDirectEnergy(double percentage) {
        //maybe we should consider the buffer in this calculation even though it sould be 0 by the time this method gets called, just to avoid negative values
        double remove = energy_kj * percentage;
        energy_kj -= remove;
        return remove;
    }

    public double splitMoles(double percentage) {
        double remove = amount_mol * percentage;
        amountBuffer_mol -= remove;
        return remove;
    }

    public double splitEnergy(double percentage) {
        double remove = energy_kj * percentage;
        energyBuffer_kj -= remove;
        return remove;
    }

    public double removeAmount(double moles){
        double tmp_amount = amount_mol+amountBuffer_mol-moles;
        if(tmp_amount < 0){
            double diff = moles - (amount_mol+amountBuffer_mol);
            if(diff < 0){
                return 0;
            }
            amountBuffer_mol -= diff;
            return diff;
        }
        return moles;
    }

    /**
     * imports the energy and moles from internal buffer and recalculates the
     * temperature and phase for this compound.
     */
    public void update() {
        //Import buffers
        this.amount_mol += amountBuffer_mol;
        this.energy_kj += energyBuffer_kj;
        //reset buffers
        amountBuffer_mol = 0;
        energyBuffer_kj = 0;
        //other procedures
        updateTemperature();
        updatePhase();
    }

    private void updateTemperature() {
        if (energy_kj <= energyToMeltingPoint_kj()) {
            temperature_K = temperature(energy_kj);
        } else if (energy_kj <= energyToMeltingPoint_kj() + energyRequiredToMelt_kj()) {
            temperature_K = properties.getMeltingPoint();
        } else if (energy_kj <= energyToBoilingPoint_kj()) {
            temperature_K = temperature(energy_kj - energyRequiredToMelt_kj());
        } else if (energy_kj <= energyToBoilingPoint_kj() + energyRequiredToVaporize_kj()) {
            temperature_K = properties.getBoilingPoint();
        } else {
            temperature_K = temperature(energy_kj - energyRequiredToMelt_kj() - energyRequiredToVaporize_kj());
        }
    }

    private double temperature(double energyForTemperature_kj) {
        return (energyForTemperature_kj / amount_mol) / properties.getSpecificHeatCapacity();
    }

    private void updatePhase() {
        if (energy_kj < energyToMeltingPoint_kj() + energyRequiredToMelt_kj()) {
            phase = Phase.SOLID;
        } else if (energy_kj < energyToBoilingPoint_kj() + energyRequiredToVaporize_kj()) {
            phase = Phase.LIQUID;
        } else {
            phase = Phase.GAS;
        }
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

    public CompoundProperties getProperties() {
        return properties;
    }

    public double getAmountBuffer_mol() {
        return amountBuffer_mol;
    }

    public double getEnergyBuffer_kj() {
        return energyBuffer_kj;
    }

    @Override
    public String toString() {
        return "Compound{" + "properties=" + properties + ", amount_mol=" + amount_mol + ", energy_j=" + energy_kj + '}';
    }

    public double getEnergy_kj() {
        return energy_kj;
    }

    public void setEnergy_kj(double energy_kj) {
        this.energy_kj = energy_kj;
    }

    public double getTemperature_K() {
        return temperature_K;
    }

    public void setTemperature_K(double temperature_K) {
        this.temperature_K = temperature_K;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public String getName() {
        return properties.getName();
    }

    public String getCode() {
        return properties.getCode();
    }

    public double getSpecificHeatCapacity() {
        return properties.getSpecificHeatCapacity();
    }

    public double getTotalHeatCapacity() {
        return properties.getSpecificHeatCapacity() * amount_mol;
    }

}
