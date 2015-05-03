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

  public Color3f calculateShading(Ray ray, HitRecord hit) {
    /*

    return new Color3f(v,v,v);*/
    ScatteringEquations.InScatterAmount(ray, hit);
    //System.out.println(depth);
    return new Color3f(depth, 0f,0f);
    //return new Color3f(0.7f, 0.5f, 0f);

  }
}