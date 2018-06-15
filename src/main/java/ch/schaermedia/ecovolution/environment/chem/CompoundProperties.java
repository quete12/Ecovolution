/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Quentin
 */
public class CompoundProperties extends ElementProperties {

    private static final Map<String, CompoundProperties> BY_CODE = new HashMap<>();

    public static CompoundProperties getPropertiesFromCode(String code)
    {
        return BY_CODE.get(code);
    }

    private final Map<ElementProperties, Integer> composition;

    public CompoundProperties()
    {
        this.composition = new HashMap<>();
    }

    public CompoundProperties(JSONObject object)
    {
        super(object);
        this.composition = new CompoundDecoder(getCode()).getComposition();
    }

    @Override
    public void map()
    {
        BY_CODE.put(getCode(), this);
    }

    @Override
    public String toString()
    {
        return super.toString() + "\n\tCompoundProperties{" + "composition=" + composition + '}';
    }
}
