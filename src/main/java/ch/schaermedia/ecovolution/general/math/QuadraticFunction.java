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
public class QuadraticFunction implements Function{

    private double a;
    private double b;
    private double c;

    public QuadraticFunction(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        fitVars(x1, y1, x2, y2, x3, y3);
    }

    public double y(double x)
    {
        return a * x * x + b * x + c;
    }

    public double x(double y)
    {
        double newC = c -y;
        double discriminant = Math.sqrt(b * b - 4 * a * newC);
        return (-b + discriminant) / 2 * a;
    }

    //Cramer's rule
    private void fitVars(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        double[] xs =
        {
            x1, x2, x3
        };
        double[] ys =
        {
            y1, y2, y3
        };
        double[][] matD = matD(xs);
        double D = calcD(matD);

        double[][] matDa = matDa(xs, ys);
        double Da = calcD(matDa);

        double[][] matDb = matDb(xs, ys);
        double Db = calcD(matDb);

        double[][] matDc = matDc(xs, ys);
        double Dc = calcD(matDc);

        a = Da / D;
        b = Db / D;
        c = Dc / D;
    }

    private double[][] matD(double[] xs)
    {
        double mat[][] = new double[3][3];
        for (int i = 0; i < xs.length; i++)
        {
            mat[0][i] = xs[i] * xs[i];
            mat[1][i] = xs[i];
            mat[2][i] = 1;
        }
        return mat;
    }

    private double[][] matDa(double[] xs, double[] ys)
    {
        double mat[][] = new double[3][3];
        for (int i = 0; i < xs.length; i++)
        {
            mat[0][i] = ys[i];
            mat[1][i] = xs[i];
            mat[2][i] = 1;
        }
        return mat;
    }

    private double[][] matDb(double[] xs, double[] ys)
    {
        double mat[][] = new double[3][3];
        for (int i = 0; i < xs.length; i++)
        {
            mat[0][i] = xs[i] * xs[i];
            mat[1][i] = ys[i];
            mat[2][i] = 1;
        }
        return mat;
    }

    private double[][] matDc(double[] xs, double[] ys)
    {
        double mat[][] = new double[3][3];
        for (int i = 0; i < xs.length; i++)
        {
            mat[0][i] = xs[i] * xs[i];
            mat[1][i] = xs[i];
            mat[2][i] = ys[i];
        }
        return mat;
    }

    private double calcD(double[][] mat)
    {
        double v1 = mat[0][0] * (mat[1][1] * mat[2][2] - (mat[1][2] * mat[2][1]));
        double v2 = mat[1][0] * (mat[0][1] * mat[2][2] - (mat[0][2] * mat[2][1]));
        double v3 = mat[2][0] * (mat[0][1] * mat[1][2] - (mat[0][2] * mat[1][1]));

        double d = v1 - v2 + v3;
        return d;
    }

    public double getA()
    {
        return a;
    }

    public double getB()
    {
        return b;
    }

    public double getC()
    {
        return c;
    }

}
