package edu.umass.cs390cg.atmosphere.numerics;

import javax.vecmath.*;

import edu.umass.cs390cg.atmosphere.geom.Ray;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

import javax.imageio.*;

import java.io.*;

public class Vec {

    //region Vector Functions
    public static Vector3f Scale(Vector3f a, float scale) {
        return new Vector3f(
                a.x * scale,
                a.y * scale,
                a.z * scale);
    }

    public static Vector3f Pointwise(Vector3f a, Vector3f b) {
        return new Vector3f(
                a.x * b.x,
                a.y * b.y,
                a.z * b.z);
    }

    public static Vector3f Add(Vector3f a, Vector3f b) {
        return new Vector3f(
                a.x + b.x,
                a.y + b.y,
                a.z + b.z);
    }

    public static Vector3f Subtract(Vector3f a, Vector3f b) {
        return new Vector3f(
                a.x - b.x,
                a.y - b.y,
                a.z - b.z);
    }

    public static Vector3f Negate(Vector3f r) {
        return new Vector3f(-r.x, -r.y, -r.z);
    }

    public static float getDeterminent(Vector3f a, Vector3f b, Vector3f c) {
        return a.x * b.y * c.z +
                b.x * c.y * a.z +
                c.x * a.y * b.z -

                a.z * b.y * c.x -
                b.z * c.y * a.x -
                c.z * a.y * b.x;
    }
    // endregion

    //region Color functions
    public Color3f Multiply(Color3f a, Color3f b) {
        return new Color3f(
                a.x * b.x,
                a.y * b.y,
                a.z * b.z);
    }

    public Color3f AddColors(Color3f a, Color3f b) {
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
    }

    public static Color3f VecToColor(Vector3f v) {
        return new Color3f(v.x, v.y, v.z);
    }
    //endregion

    public static Ray NegateRay(Ray r) {
        return new Ray(r.d, new Vector3f(-r.d.x, -r.d.y, -r.d.z));
    }


    public static boolean isCloseEnough(float actual, float target, float tolerance) {
        if (actual <= target + tolerance && (actual >= target - tolerance))
            return true;
        else
            return false;
    }

}
