package edu.umass.cs390cg.atmosphere.numerics;

import javax.vecmath.Vector3d;

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

    public static Vector3d Pointwise(Vector3d a, Vector3d b) {
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

    public static Vector3d Negate(Vector3d r) {
        return new Vector3d(-r.x, -r.y, -r.z);
    }

    public static Vector3d ColorNormalize(Vector3d V, double min, double max){
        
    }

    public static boolean isCloseEnough(double actual, double target, double tolerance) {
        if (actual <= target + tolerance && (actual >= target - tolerance))
            return true;
        else
            return false;
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
