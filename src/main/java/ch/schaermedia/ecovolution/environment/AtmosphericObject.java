/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes an Object with atmospheric properties. Main feature is managing the mixture of contained elements.
 * @author Quentin
 */
public class AtmosphericObject {

    public static final double CELSIUS_TO_KELVIN_CONVERSION = 273.15;
    public static final double GAS_CONSTANT_L_kPa_K = 8.3144598;

    //<editor-fold desc="static calculations">
    /**
     *
     * @param amount_mol the value of amount_mol
     * @param volume_L the value of volume_L
     * @return the double
     */
    public static double density_mol_L(double amount_mol, double volume_L) {
        return amount_mol / volume_L;
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the double
     */
    public static double density_mol_L(double pressure_kPa, double amount_mol, double temperature_K) {
        return amount_mol / volume_L(pressure_kPa, amount_mol, temperature_K);
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param volume_L the value of volume_L
     * @param temperature_K the value of temperature_K
     * @return the double
     */
    public static double moles(double pressure_kPa, double volume_L, double temperature_K) {
        return pressure_kPa * volume_L / (GAS_CONSTANT_L_kPa_K * temperature_K);
    }

    /**
     *
     * @param volume_L the value of volume_L
     * @param density_mol_L the value of density_mol_L
     * @return the double
     */
    public static double molesOfDensity_mol(double volume_L, double density_mol_L) {
        return density_mol_L * volume_L;
    }

    /**
     *
     * @param volume_L the value of volume_L
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the double
     */
    public static double pressure_kPa(double volume_L, double amount_mol, double temperature_K) {
        return (amount_mol * GAS_CONSTANT_L_kPa_K * temperature_K) / volume_L;
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param volume_L the value of volume_L
     * @param amount_mol the value of amount_mol
     * @return the double
     */
    public static double temperature_K(double pressure_kPa, double volume_L, double amount_mol) {
        return pressure_kPa * volume_L / (amount_mol * GAS_CONSTANT_L_kPa_K);
    }

    /**
     *
     * @param tempInK the value of tempInK
     * @return the double
     */
    public static double toCelsius(double tempInK) {
        return tempInK - CELSIUS_TO_KELVIN_CONVERSION;
    }

    /**
     *
     * @param tempInC the value of tempInC
     * @return the double
     */
    public static double toKelvin(double tempInC) {
        return tempInC + CELSIUS_TO_KELVIN_CONVERSION;
    }

    /**
     *
     * @param amount_mol the value of amount_mol
     * @param density_mol_L the value of density_mol_L
     * @return the double
     */
    public static double volumeOfDensity_L(double amount_mol, double density_mol_L) {
        return amount_mol / density_mol_L;
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the double
     */
    public static double volume_L(double pressure_kPa, double amount_mol, double temperature_K) {
        return (amount_mol * GAS_CONSTANT_L_kPa_K * temperature_K) / pressure_kPa;
    }
    //</editor-fold>

    protected final Map<Element, Double> composition;

    protected final Map<Element, Double> inBuffer;
    protected final Map<Element, Double> outBuffer;

    public AtmosphericObject() {
        this.composition = new HashMap<>();
        this.inBuffer = new HashMap<>();
        this.outBuffer = new HashMap<>();
    }

    public double totalOf(Element element) {
        return composition.get(element);
    }

    public double molarAmount() {
        double result = 0;
        result = composition.entrySet().stream().map((entry) -> entry.getValue()).map((value) -> value).reduce(result, (accumulator, _item) -> accumulator + _item);
        return result;
    }

    public double concentrationOf(Element element) {
        double total = molarAmount();
        double selected = composition.get(element);
        return selected / total;
    }

    public void update() {
        directAdd(inBuffer);
        directRemove(outBuffer);
    }

    public void add(Element element, double amount) {
        if (inBuffer.containsKey(element)) {
            Double value = inBuffer.get(element);
            value += amount;
            inBuffer.replace(element, value);
        } else {
            inBuffer.put(element, amount);
        }
    }

    public void remove(Element element, double amount) {
        if (outBuffer.containsKey(element)) {
            Double value = outBuffer.get(element);
            value += amount;
            outBuffer.replace(element, value);
        } else {
            outBuffer.put(element, amount);
        }
    }

    public void directAdd(Element element, double amount) {
        if (composition.containsKey(element)) {
            Double value = composition.get(element);
            value += amount;
            composition.replace(element, value);
        } else {
            composition.put(element, amount);
        }
    }

    public double directRemove(Element element, double amount) {
        if (!composition.containsKey(element)) {
            return 0;
        }
        double value = composition.get(element);
        if (value < amount) {
            amount = value;
        }
        value -= amount;
        composition.replace(element, value);
        return amount;
    }

    protected void directAdd(Map<Element, Double> mixture) {
        mixture.entrySet().forEach((entry) -> {
            Element key = entry.getKey();
            Double value = entry.getValue();
            directAdd(key, value);
            entry.setValue(0d);
        });
    }

    /**
     * Removes each element, amount pair of the given mixture from this one.
     * Values exceeding the current availability of an element in the mixture
     * remain in the given mixture.
     *
     * @param mixture
     */
    protected void directRemove(Map<Element, Double> mixture) {
        mixture.entrySet().forEach((entry) -> {
            Element key = entry.getKey();
            Double value = entry.getValue();
            double removed = directRemove(key, value);
            entry.setValue(value - removed);
        });
    }

}
