/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

/**
 *
 * @author Quentin
 */
public class Compound {

    private final CompoundProperties properties;
    private Phase phase;
    private double amount_mol;

    public Compound(CompoundProperties properties) {
        this.properties = properties;
    }

}
