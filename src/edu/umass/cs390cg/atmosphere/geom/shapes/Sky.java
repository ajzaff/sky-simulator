package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.RayTracer;
import edu.umass.cs390cg.atmosphere.ScatteringEquations;
import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.numerics.Vec;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import java.util.Vector;


public class Sky extends Sphere {
  public Vector3d color;
  public static double lowestHeight = 10000d;

  public Vector3d calculateShading(Ray ray, HitRecord hit) {


    //return new Color3f(depth, depth, depth);
    //return ScatteringEquations.InScatterAmount(ray, hit);
    return new Vector3d();

  }
}