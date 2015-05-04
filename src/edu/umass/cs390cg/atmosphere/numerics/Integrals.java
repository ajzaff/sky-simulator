package edu.umass.cs390cg.atmosphere.numerics;

import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.numerics.Vec.*;

import javax.vecmath.Vector3d;

public final class Integrals {

    /**
     * Estimates the integral of a function from A to B.
     *
     * @param f a function.
     * @param A        a starting point.
     * @param B        a finishing point.
     * @param n        a number of points [1,)
     * @return the value of the integral.
     */
    public static Vector3d estimateIntegral(
            Function f,
            Vector3d A,
            Vector3d B,
            double scale,
            int n) {
        Vector3d dir;
        Object[] args;
        double t, segmentLength;
        Vector3d result = new Vector3d();

        dir = new Vector3d(B);
        dir.sub(A);
        Ray ray = new Ray(A, dir);
        segmentLength = dir.length() / n;

        for (t = 0; t <= 1; t += segmentLength) {
            args = new Object[]{ray.pointAt(t + segmentLength / 2)};
            result = Vec.Add(
                        Vec.Scale(f.evaluate(args), segmentLength * scale),
                        result);
        }

        return result;
    }
}
