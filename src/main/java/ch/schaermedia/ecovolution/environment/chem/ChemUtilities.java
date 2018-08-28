/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import ch.schaermedia.ecovolution.environment.chem.properties.CompoundProperties;
import ch.schaermedia.ecovolution.environment.chem.properties.ElementProperties;
import ch.schaermedia.ecovolution.general.math.Consts;
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

    //<editor-fold desc="static calculations">
    /**
     *
     * @param amount_mol the value of amount_mol
     * @param volume_L the value of volume_L
     * @return the long
     */
    public static long density_mol_L(long amount_mol, long volume_L)
    {
        return amount_mol / volume_L;
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the long
     */
    public static long density_mol_L(long pressure_kPa, long amount_mol, long temperature_K)
    {
        return amount_mol / volume_L(pressure_kPa, amount_mol, temperature_K);
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param volume_L the value of volume_L
     * @param temperature_K the value of temperature_K
     * @return the long
     */
    public static long moles(long pressure_kPa, long volume_L, long temperature_K)
    {
        long a = pressure_kPa * volume_L;
        long b = Consts.GAS_CONSTANT_L_kPa_K * temperature_K;
        long result = a / b;
        if (result < 0)
        {
            System.out.println("a=" + pressure_kPa + " * " + volume_L + ", b=" + Consts.GAS_CONSTANT_L_kPa_K + " * " + temperature_K);
            System.out.println("a=" + a + ", b=" + b + ", result=" + result);
        }
        return result;
    }

    /**
     *
     * @param volume_L the value of volume_L
     * @param density_mol_L the value of density_mol_L
     * @return the long
     */
    public static long molesOfDensity_mol(long volume_L, long density_mol_L)
    {
        return density_mol_L * volume_L;
    }

    /**
     *
     * @param volume_L the value of volume_L
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the long
     */
    public static long pressure_kPa(long volume_L, long amount_mol, long temperature_K)
    {
        if (volume_L == 0)
        {
            return 0;
        }
        return (amount_mol * Consts.GAS_CONSTANT_L_kPa_K * temperature_K) / (volume_L * Consts.PRESCISION * Consts.PRESCISION);
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param volume_L the value of volume_L
     * @param amount_mol the value of amount_mol
     * @return the long
     */
    public static long temperature_K(long pressure_kPa, long volume_L, long amount_mol)
    {
        if (amount_mol == 0)
        {
            return 0;
        }
        return pressure_kPa * volume_L / (amount_mol * Consts.GAS_CONSTANT_L_kPa_K);
    }

    /**
     *
     * @param tempInK the value of tempInK
     * @return the long
     */
    public static long toCelsius(long tempInK)
    {
        return tempInK - Consts.CELSIUS_TO_KELVIN_CONVERSION;
    }
    //</editor-fold>

    /**
     *
     * @param tempInC the value of tempInC
     * @return the long
     */
    public static long toKelvin(long tempInC)
    {
        return tempInC + Consts.CELSIUS_TO_KELVIN_CONVERSION;
    }

    /**
     *
     * @param amount_mol the value of amount_mol
     * @param density_mol_L the value of density_mol_L
     * @return the long
     */
    public static long volumeOfDensity_L(long amount_mol, long density_mol_L)
    {
        return amount_mol / density_mol_L;
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the long
     */
    public static long volume_L(long pressure_kPa, long amount_mol, long temperature_K)
    {
        if (pressure_kPa == 0)
        {
            return 0;
        }
        return (amount_mol * Consts.GAS_CONSTANT_L_kPa_K * temperature_K) / (pressure_kPa * Consts.PRESCISION * Consts.PRESCISION);
    }

    public static void readElements(String file) throws FileNotFoundException
    {
        JSONObject root;
        try
        {
            root = new JSONObject(new JSONTokener(new FileReader(file)));
            readElements(root);
            readCompounds(root);
            readReactions(root);
        }
        catch (FileNotFoundException ex)
        {
            throw ex;
        }
    }

    private static void readElements(JSONObject root)
    {
        JSONArray array = root.getJSONArray("elements");
        for (int i = 0; i < array.length(); i++)
        {
            ElementProperties element = null;
            try
            {
                element = new ElementProperties(array.getJSONObject(i));
                element.map();
                Logger.getLogger(ChemUtilities.class.getName()).log(Level.INFO, "Loaded: {0}", element);
            }
            catch (JSONException ex)
            {
                Logger.getLogger(ChemUtilities.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void readCompounds(JSONObject root)
    {
        JSONArray array = root.getJSONArray("compounds");
        for (int i = 0; i < array.length(); i++)
        {
            CompoundProperties compound = null;
            try
            {
                compound = new CompoundProperties(array.getJSONObject(i));
                compound.map();
                Logger.getLogger(ChemUtilities.class.getName()).log(Level.INFO, "Loaded: {0}", compound);
            }
            catch (JSONException ex)
            {
                Logger.getLogger(ChemUtilities.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private static void readReactions(JSONObject root)
    {
        JSONArray array = root.getJSONArray("reactions");
        for (int i = 0; i < array.length(); i++)
        {
//            ElementProperties element = null;
//            try {
//                Logger.getLogger(Element.class.getName()).log(Level.INFO,"Loaded: {0}", element);
//            } catch (JSONException ex) {
//                Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

    }
}
