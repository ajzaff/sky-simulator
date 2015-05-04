package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.ScatteringEquations;
import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3d;

public class Sky extends Sphere {
  public Vector3d color;
  public static double lowestHeight = 10000d;

  public Vector3d calculateShading(Ray ray, HitRecord hit) {
    //System.out.println("Hit at " + hit.pos);
    double height = ScatteringEquations.height(hit.pos);
    if (height < 200d || height > 210d) {
      System.out.println("Height is really " + height);
    }
    Vector3d v3 = ScatteringEquations.GetLightRays(ray, hit);
    return v3;

  }
}