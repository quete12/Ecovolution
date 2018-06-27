/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.general.math;

/**
 *
 * @author Quentin
 */
public class LinearFunction implements Function{

    private final double varA;
    private final double varB;

    public LinearFunction(double p1x, double p1y, double p2x, double p2y)
    {
        this.varA = findVarA(p1x, p1y, p2x, p2y);
        this.varB = findVarB(varA, p2x, p2y);
    }

    public LinearFunction(double varA, double varB)
    {
        this.varA = varA;
        this.varB = varB;
    }

    public LinearFunction shiftLeft(double n){
        double newB = varB + varA*n;
        return new LinearFunction(varA, newB);
    }

    public LinearFunction shiftRight(double n){
        double newB = varB - varA*n;
        return new LinearFunction(varA, newB);
    }

    public double y(double x)
    {
        return varA * x + varB;
    }

    public double x(double y)
    {
        return (y - varB) / varA;
    }

    public boolean isPointLeft(double px, double py){
        if(varA>0){
            return y(px)<py;
        }else{
            return y(px)>py;
        }
    }

    public boolean isPointOnOrOver(double px, double py){
        if(varA>0){
            return y(px)<=py;
        }else{
            return y(px)>=py;
        }
    }

    private double findVarB(double varA, double px, double py)
    {
        return py - (varA * px);
    }

    private double findVarA(double p1X, double p1Y, double p2X, double p2Y)
    {
        return (p2Y - p1Y) / (p2X - p1X);
    }

    @Override
    public String toString()
    {
        return "f(x) = " + varA + "x + " + varB;
    }

    @Override
    public boolean isPositive()
    {
        return varA > 0;
    }

    public double getVarA()
    {
        return varA;
    }

    public double getVarB()
    {
        return varB;
    }
}
