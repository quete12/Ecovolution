/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.Map;

/**
 *
 * @author Quentin
 */
public class CompoundElement extends Element{

    private Map<Element, Integer> CompoundComposition;

    public CompoundElement(String code) {
        initFromCode(code);
    }

    private void initFromCode(String code) {
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
                    addAsComponent(currentS, num);
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
        addAsComponent(currentS, num);
    }

    private void addAsComponent(String code, String amount) {
        Element el = Element.getByCode(code);
        amount = (amount.isEmpty()) ? "1" : amount;
        int amnt = Integer.parseInt(amount);
        CompoundComposition.put(el, amnt);
    }


}
