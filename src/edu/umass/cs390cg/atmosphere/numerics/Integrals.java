package edu.umass.cs390cg.atmosphere.numerics;

import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3d;

public final class Integrals {

  /**
   * Estimates the integral of a function from A to B.
   * @param function a function.
   * @param A a starting point.
   * @param B a finishing point.
   * @param n a number of points [1,)
   * @return the value of the integral.
   */
  public static double estimateIntegral(
      Function function,
      Vector3d A,
      Vector3d B,
      int n)
  {
    Vector3d dir;
    Object[] args;
    double t, u, result;

    dir = new Vector3d(B); dir.sub(A);
    Ray ray = new Ray(A,dir);
    u = dir.length() / n;

    result = 0;
    for(t=0; t <= 1; t += u) {
      args = new Object[] { ray.pointAt(t+u/2) };
      result += u * function.evaluate(args);
    }

    return result;
  }
}
