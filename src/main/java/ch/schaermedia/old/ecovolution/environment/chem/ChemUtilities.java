/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.environment.chem;

import ch.schaermedia.old.ecovolution.environment.chem.properties.CompoundProperties;
import ch.schaermedia.old.ecovolution.environment.chem.properties.ElementProperties;
import ch.schaermedia.old.ecovolution.general.math.BigDouble;
import ch.schaermedia.old.ecovolution.general.math.Consts;
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
    public static BigDouble density_mol_L(BigDouble amount_mol, BigDouble volume_L)
    {
        return amount_mol.div(volume_L, new BigDouble());
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the long
     */
    public static BigDouble density_mol_L(BigDouble pressure_kPa, BigDouble amount_mol, BigDouble temperature_K)
    {
        return amount_mol.div(volume_L(pressure_kPa, amount_mol, temperature_K), new BigDouble());
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param volume_L the value of volume_L
     * @param temperature_K the value of temperature_K
     * @return the long
     */
    public static BigDouble moles(BigDouble pressure_kPa, BigDouble volume_L, BigDouble temperature_K)
    {
        BigDouble res = new BigDouble();
        moles(pressure_kPa, volume_L, temperature_K, res);
        return res;
    }

    public static void moles(BigDouble pressure_kPa, BigDouble volume_L, BigDouble temperature_K, BigDouble amount_mol)
    {
        BigDouble a = pressure_kPa.mul(volume_L, new BigDouble());
        BigDouble b = Consts.GAS_CONSTANT_L_kPa_K.mul(temperature_K, new BigDouble());
        a.div(b, amount_mol);
        if (amount_mol.isNegative())
        {
            System.out.println("a=" + pressure_kPa + " * " + volume_L + ", b=" + Consts.GAS_CONSTANT_L_kPa_K + " * " + temperature_K);
            System.out.println("a=" + a + ", b=" + b + ", result=" + amount_mol);
        }
    }

    /**
     *
     * @param volume_L the value of volume_L
     * @param density_mol_L the value of density_mol_L
     * @return the long
     */
    public static BigDouble molesOfDensity_mol(BigDouble volume_L, BigDouble density_mol_L)
    {
        return density_mol_L.mul(volume_L, new BigDouble());
    }

    /**
     *
     * @param volume_L the value of volume_L
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the long
     */
    public static BigDouble pressure_kPa(BigDouble volume_L, BigDouble amount_mol, BigDouble temperature_K)
    {
        BigDouble res = new BigDouble();
        pressure_kPa(volume_L, amount_mol, temperature_K, res);
        return res;
    }

    public static void pressure_kPa(BigDouble volume_L, BigDouble amount_mol, BigDouble temperature_K, BigDouble pressure_kPa)
    {
        if (volume_L.isZero())
        {
            pressure_kPa.clear();
            return;
        }
        amount_mol.mul(Consts.GAS_CONSTANT_L_kPa_K, pressure_kPa).mul(temperature_K).div(volume_L);
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param volume_L the value of volume_L
     * @param amount_mol the value of amount_mol
     * @return the long
     */
    public static BigDouble temperature_K(BigDouble pressure_kPa, BigDouble volume_L, BigDouble amount_mol)
    {
        if (amount_mol.isZero())
        {
            return new BigDouble();
        }
        return pressure_kPa.mul(volume_L, new BigDouble()).div(amount_mol.mul(Consts.GAS_CONSTANT_L_kPa_K, new BigDouble()));
    }

    /**
     *
     * @param tempInK the value of tempInK
     * @return the long
     */
    public static BigDouble toCelsius(BigDouble tempInK)
    {
        return tempInK.sub(Consts.CELSIUS_TO_KELVIN_CONVERSION, new BigDouble());
    }
    //</editor-fold>

    /**
     *
     * @param tempInC the value of tempInC
     * @return the long
     */
    public static BigDouble toKelvin(BigDouble tempInC)
    {
        return tempInC.add(Consts.CELSIUS_TO_KELVIN_CONVERSION, new BigDouble());
    }

    /**
     *
     * @param amount_mol the value of amount_mol
     * @param density_mol_L the value of density_mol_L
     * @return the long
     */
    public static BigDouble volumeOfDensity_L(BigDouble amount_mol, BigDouble density_mol_L)
    {
        return amount_mol.div(density_mol_L, new BigDouble());
    }

    /**
     *
     * @param pressure_kPa the value of pressure_kPa
     * @param amount_mol the value of amount_mol
     * @param temperature_K the value of temperature_K
     * @return the long
     */
    public static BigDouble volume_L(BigDouble pressure_kPa, BigDouble amount_mol, BigDouble temperature_K)
    {
        BigDouble res = new BigDouble();
        volume_L(pressure_kPa, amount_mol, temperature_K, res);
        return res;
    }

    public static void volume_L(BigDouble pressure_kPa, BigDouble amount_mol, BigDouble temperature_K, BigDouble volume_L)
    {
        if (pressure_kPa.isZero())
        {
            volume_L.clear();
            return;
        }
        amount_mol.mul(Consts.GAS_CONSTANT_L_kPa_K, volume_L).mul(temperature_K).div(pressure_kPa);
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
        } catch (FileNotFoundException ex)
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
            } catch (JSONException ex)
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
            } catch (JSONException ex)
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
