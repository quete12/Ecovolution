/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Quentin
 */
public class ChemUtilities {
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
    //</editor-fold>

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

    public static void readElements(String file) throws FileNotFoundException {
        JSONObject root;
        try {
            root = new JSONObject(new JSONTokener(new FileReader(file)));
            readElements(root);
            readCompounds(root);
            readReactions(root);
        } catch (FileNotFoundException ex) {
            throw ex;
        }
    }

    private static void readElements(JSONObject root){
        JSONArray array = root.getJSONArray("elements");
        for (int i = 0; i < array.length(); i++) {
            ElementProperties element = null;
            try {
                element = new ElementProperties(array.getJSONObject(i));
                element.map();
                Logger.getLogger(Element.class.getName()).log(Level.INFO,"Loaded: {0}", element);
            } catch (JSONException ex) {
                Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void readCompounds(JSONObject root){
        JSONArray array = root.getJSONArray("compounds");
        for (int i = 0; i < array.length(); i++) {
            CompoundProperties compound = null;
            try {
                compound = new CompoundProperties(array.getJSONObject(i));
                compound.map();
                Logger.getLogger(Element.class.getName()).log(Level.INFO,"Loaded: {0}", compound);
            } catch (JSONException ex) {
                Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private static void readReactions(JSONObject root){
        JSONArray array = root.getJSONArray("reactions");
        for (int i = 0; i < array.length(); i++) {
//            ElementProperties element = null;
//            try {
//                Logger.getLogger(Element.class.getName()).log(Level.INFO,"Loaded: {0}", element);
//            } catch (JSONException ex) {
//                Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

    }
}
