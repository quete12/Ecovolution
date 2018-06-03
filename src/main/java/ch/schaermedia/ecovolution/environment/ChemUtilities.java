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
