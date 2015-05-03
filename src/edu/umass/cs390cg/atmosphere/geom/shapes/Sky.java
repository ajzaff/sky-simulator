package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.RayTracer;
import edu.umass.cs390cg.atmosphere.ScatteringEquations;
import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.numerics.Vec;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import java.util.Vector;

import static edu.umass.cs390cg.atmosphere.RayTracer.*;
import static edu.umass.cs390cg.atmosphere.ScatteringEquations.*;


public class Sky extends Sphere {
  public Color3f color;
  public static float lowestHeight = 10000f;

  public Color3f calculateShading(Ray ray, HitRecord hit) {

    float height = (hit.pos.y - r.scene.camera.center.y)/ (sky.radius - r.scene.camera.center.y);

    return new Color3f(height, height, height);
    //return ScatteringEquations.InScatterAmount(ray, hit);

  }
}