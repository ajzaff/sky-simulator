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
  public int samplesPerInScatterRay;
  public int samplesPerOutScatterRay;

  public float RayleighScaleHeight;
  public float MieScaleHeight;

  public float OuterRadius;
  public float InnerRadius;
  public Vector3f Center;
  public float scale; // 1 / (Outer radius - inner radius)
  public float scaleDepth = 0.25f; // Depth of average atmospheric density, 0.25
  public float scaleOverScaleDepth;

  public Color3f calculateShading(Ray ray, HitRecord hit) {
    /*
    float v;


    //float fCos = r.scene.sun.d.dot(ray.d) / ray.d.length();
    Vector3f negD = new Vector3f(ray.d); negD.negate();
    float c       = r.scene.sun.d.angle(negD);
    //v = RayleighPhaseFunction()PhaseFunction(r.scene.sun.d.dot(negD), Mie_G);

    return new Color3f(v,v,v);*/
    float depth = GetOpticalDepth(ray.o, hit.pos);
    return new Color3f()

  }
}