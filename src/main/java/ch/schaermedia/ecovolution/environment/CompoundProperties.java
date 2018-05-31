/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Quentin
 */
public class CompoundProperties extends ElementProperties{
    private static final Map<String, CompoundProperties> BY_CODE = new HashMap<>();

    public static CompoundProperties getPropertiesFromCode(String code) {
        return BY_CODE.get(code);
    }

    private Map<ElementProperties, Integer> composition;

    public CompoundProperties() {
    }

    public CompoundProperties(JSONObject object) {
        super(object);
        decodeComposition(code);
    }

    private void decodeComposition(String code) {
        char[] chars = code.toCharArray();
        StringBuilder current = new StringBuilder();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            boolean upperCase = Character.isUpperCase(aChar);
            boolean lowerCase = Character.isLowerCase(aChar);
            boolean digit = Character.isDigit(aChar);

            if (upperCase) {
                if (i != 0) {
                    //push current to result
                    String currentS = current.toString();
                    String num = digits.toString();
                    addToComposition(currentS, num);
                }
                //begin new
                current = new StringBuilder().append(aChar);
                digits = new StringBuilder();
                continue;
            }
            if (lowerCase) {
                current.append(aChar);
            }
            if (digit) {
                digits.append(aChar);
            }
        }
        String currentS = current.toString();
        String num = digits.toString();
        addToComposition(currentS, num);
    }

    private void addToComposition(String code, String amount) {
        ElementProperties el = ElementProperties.getPropertiesFromCode(code);
        amount = (amount.isEmpty()) ? "1" : amount;
        int amnt = Integer.parseInt(amount);
        composition.put(el, amnt);
    }

    @Override
    public void map() {
        BY_CODE.put(code, this);
    }



}
