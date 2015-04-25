package edu.umass.cs390cg.atmosphere.geom;

import javax.vecmath.Vector3f;

public class Ray {
  public Vector3f o,d;

  public Ray() {
    this.o = new Vector3f(0, 0, 0);
    this.d = new Vector3f(0, -1, 0);
  }

  public Ray(Ray ray) {
    this.o = new Vector3f(ray.o);
    this.d = new Vector3f(ray.d);
  }

  public Ray(Vector3f o, Vector3f d) {
    this.o = new Vector3f(o);
    this.d = new Vector3f(d);
    this.d.normalize();
  }

  public Vector3f pointAt(float t) {
    Vector3f point = new Vector3f();
    point.scaleAdd(t, d, o);
    return point;
  }
}