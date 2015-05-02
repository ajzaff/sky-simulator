package edu.umass.cs390cg.atmosphere.numerics;

import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3f;

public final class Integrals {

  /**
   * Estimates the integral of a function from A to B.
   * @param function a function.
   * @param A a starting point.
   * @param B a finishing point.
   * @param n a number of points [1,)
   * @return the value of the integral.
   */
  public static float estimateIntegral(
      Function function,
      Vector3f A,
      Vector3f B,
      int n)
  {
    Vector3f d;
    Object[] args;
    float t, u, result;

    d = new Vector3f(B); d.sub(A);
    Ray ray = new Ray(A,d);
    u = d.length() / n;

    result = 0;
    for(t=0; t <= 1; t += u) {
      args = new Object[] { ray.pointAt(t+u/2) };
      result += u * function.evaluate(args);
    }

    return result;
  }
}
