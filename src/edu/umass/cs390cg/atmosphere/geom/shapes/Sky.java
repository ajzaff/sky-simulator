package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3d;


public class Sky extends Sphere {
  public Vector3d color;
  public static double lowestHeight = 10000d;

  public Vector3d calculateShading(Ray ray, HitRecord hit) {


    //return new Color3f(depth, depth, depth);
    //return ScatteringEquations.InScatterAmount(ray, hit);

  }
}