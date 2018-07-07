/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.properties;

import ch.schaermedia.ecovolution.environment.chem.properties.ElementProperties;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Quentin
 */
public class CompoundDecoder {

    private StringBuilder current = new StringBuilder();
    private StringBuilder digits = new StringBuilder();
    private final Map<ElementProperties, Integer> composition;
    private final String code;

    public CompoundDecoder(String code)
    {
        this.code = code;
        this.composition = new HashMap<>();
        readComposition();
    }

    public Map<ElementProperties, Integer> getComposition()
    {
        return composition;
    }

    private void readComposition()
    {
        char[] chars = code.toCharArray();
        current.append(chars[0]);
        for (int i = 1; i < chars.length; i++)
        {
            char aChar = chars[i];
            if (Character.isDigit(aChar))
            {
                digits.append(aChar);
                continue;
            }
            if (Character.isUpperCase(aChar))
            {
                finishElement();
                resetBuilders();
            }
            current.append(aChar);
        }
        finishElement();
    }

    private void resetBuilders()
    {
        current = new StringBuilder();
        digits = new StringBuilder();
    }

    private void finishElement()
    {
        String amount = digits.toString();
        ElementProperties el = ElementProperties.getPropertiesFromCode(current.toString());
        amount = (amount.isEmpty()) ? "1" : amount;
        int amnt = Integer.parseInt(amount);
        composition.put(el, amnt);
    }
}
