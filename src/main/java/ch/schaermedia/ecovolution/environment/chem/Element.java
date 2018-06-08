/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

/**
 *
 * @author Quentin
 */
public class Element {

    private final ElementProperties properties;
    private Phase phase;
    private double amount_mol;

    public Element(ElementProperties properties)
    {
        this.properties = properties;
    }

    @Override
    public String toString()
    {
        return "Element{" + "properties=" + properties + ", phase=" + phase + ", amount_mol=" + amount_mol + '}';
    }
}
