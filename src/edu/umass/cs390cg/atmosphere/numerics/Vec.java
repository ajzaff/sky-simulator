package edu.umass.cs390cg.atmosphere.numerics;

import javax.vecmath.Vector3d;

import static java.lang.Math.exp;

public class Vec {

    //region Vector Functions
    public static Vector3d Scale(Vector3d a, double scale) {
        return new Vector3d(
                a.x * scale,
                a.y * scale,
                a.z * scale);
    }
    public static Vector3d Scale(Vector3d a, Vector3d b) {
        return new Vector3d(
                a.x * b.x,
                a.y * b.y,
                a.z * b.z);
    }

    public static Vector3d Add(Vector3d a, Vector3d b) {
        return new Vector3d(
                a.x + b.x,
                a.y + b.y,
                a.z + b.z);
    }

    public static Vector3d Add(Vector3d a, double b){
        return new Vector3d(
                a.x + b,
                a.y + b,
                a.z + b);
    }

    public static Vector3d Subtract(Vector3d a, Vector3d b) {
        return new Vector3d(
                a.x - b.x,
                a.y - b.y,
                a.z - b.z);
    }

    // Good
    public static Vector3d Negate(Vector3d r) {
        return new Vector3d(-r.x, -r.y, -r.z);
    }

    public static Vector3d ColorNormalize(Vector3d V, double min, double max){
        double range = max - min;
        return new Vector3d(
                (V.x - min)/range,
                (V.y - min)/range,
                (V.z - min)/range);
    }

    public static double Normalize(double num, double min, double max){
        double range = max - min;
        return (num - min)/range;
    }

    public static double Map(double num, double min, double max, double newMin, double newMax){
        return Normalize(num, min, max) * (newMax - newMin) + newMin;
    }

    public static double Distance(Vector3d A, Vector3d B){
        return Subtract(A, B).length();
    }
    public static boolean isCloseEnough(double actual, double target, double tolerance) {
        if (actual <= target + tolerance && (actual >= target - tolerance))
            return true;
        else
            return false;
    }

    // Take from Rui Wang Starter code
    // reflect a direction (in) around a given normal
	/* NOTE: dir is assuming to point INTO from the hit point
	 * if your ray direction is point INTO the hit point, you should flip
	 * the sign of the direction before calling reflect
	 */
    public static Vector3d Reflect(Vector3d dir, Vector3d normal)
    {
        Vector3d newDir = Negate(dir);
        Vector3d out = new Vector3d(normal);
        out.scale(2.f * newDir.dot(normal));
        out.sub(newDir);
        return out;
    }

    public static boolean VectorIsNonZero(Vector3d c) {
        if (c.x == 0f && c.y == 0f && c.z == 0f)
            return false;
        else
            return true;
    }

    public static double GetVectorCos(Vector3d A, Vector3d B) {
        return A.dot(B);
    }

    public static Vector3d VecExponent(Vector3d V) {
        return new Vector3d(
                exp(V.x),
                exp(V.y),
                exp(V.z));
    }



    /*
    public static double getDeterminent(Vector3d a, Vector3d b, Vector3d c) {
        return a.x * b.y * c.z +
                b.x * c.y * a.z +
                c.x * a.y * b.z -

                a.z * b.y * c.x -
                b.z * c.y * a.x -
                c.z * a.y * b.x;
    }*/
    // endregion

    //region Color functions
    /*
    public static Color3f Multiply(Color3f a, Color3f b) {
        return new Color3f(
                a.x * b.x,
                a.y * b.y,
                a.z * b.z);
    }

    public static Color3f Scale(Color3f a, double scale){
        return new Color3f(
                (float)(a.x * scale),
                (float)(a.y * scale),
                (float)(a.z * scale));
    }

    public static Color3f AddColors(Color3f a, Color3f b) {
        return new Color3f(
                a.x + b.x,
                a.y + b.y,
                a.z + b.z);
    }

    public static boolean ColorIsNonZero(Color3f c) {
        if (c.x == 0f && c.y == 0f && c.z == 0f)
            return false;
        else
            return true;
    }*/

    /*public static Color3f VecToColor(Vector3d v) {
        return new Color3f((double)v.x, v.y, v.z);
    }*/
    //endregion

    /*public static Ray NegateRay(Ray r) {
        return new Ray(r.d, new Vector3d(-r.d.x, -r.d.y, -r.d.z));
    }*/

    /*

    */

}
