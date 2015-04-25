package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class Sky extends Sphere {
  public Color3f color;

  public Color3f calculateShading(Ray ray, HitRecord hr) {
    float i,x;
    Vector3f v,rgbLoss = new Vector3f();

    for(i=0; i < hr.t; i += .01) {
      v = ray.pointAt(i);
      v.sub(center);
      x = 2f/v.lengthSquared();
      rgbLoss.add(new Vector3f(x, -x, -2*x));
    }

    Color3f color = new Color3f(this.color);
    color.add(rgbLoss);
    return color;
  }
}