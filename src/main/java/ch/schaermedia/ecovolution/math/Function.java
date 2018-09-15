/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.math;

/**
 *
 * @author Quentin
 */
public interface Function {

    public BigDouble x(BigDouble y);

    public BigDouble y(BigDouble x);

    public boolean isPositive();

    public boolean isNegative();

    public void limitMinX(BigDouble minX);

    public void limitMaxX(BigDouble maxX);

    public void limitMinY(BigDouble minY);

    public void limitMaxY(BigDouble maxY);

    public boolean isWithinLimits(BigDouble x, BigDouble y);
}
