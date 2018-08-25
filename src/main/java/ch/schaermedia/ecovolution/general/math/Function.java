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
public interface Function{

    public long x(long y);

    public long y(long x);

    public boolean isPositive();
    public boolean isNegative();
//
//    default public boolean isPointOnOrLeft(long x, long y)
//    {
//        long fx = x(y);
//        return x <= fx;
//    }
//
    default public boolean isPointLeft(long x, long y)
    {
        long fx = x(y);
        return x < fx;
    }
//
//    default public boolean isPointOnOrRight(long x, long y)
//    {
//        long fx = x(y);
//        return x >= fx;
//    }

    default public boolean isPointRight(long x, long y)
    {
        long fx = x(y);
        return x > fx;
    }
//
    default public boolean isPointOnOrOver(long x, long y)
    {
        long fy = y(x);
        return y >= fy;
    }
//
//    default public boolean isPointOver(long x, long y)
//    {
//        long fy = y(x);
//        return y > fy;
//    }
//
//    default public boolean isPointOnOrUnder(long x, long y)
//    {
//        long fy = y(x);
//        return y <= fy;
//    }
//
    default public boolean isPointUnder(long x, long y)
    {
        long fy = y(x);
        return y < fy;
    }
}
