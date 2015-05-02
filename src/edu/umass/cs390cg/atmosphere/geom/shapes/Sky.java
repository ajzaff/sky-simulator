package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.RayTracer;
import edu.umass.cs390cg.atmosphere.ScatteringEquations;
import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import static edu.umass.cs390cg.atmosphere.RayTracer.*;
import static edu.umass.cs390cg.atmosphere.ScatteringEquations.*;


public class Sky extends Sphere {
  public Color3f color;

  public Color3f calculateShading(Ray ray, HitRecord hr) {

    float v;

    float fCos = r.scene.sun.d.dot(ray.d) / ray.d.length();
    v = RayleighPhaseFunction(fCos);

    return new Color3f(v,v,v);
  }
}