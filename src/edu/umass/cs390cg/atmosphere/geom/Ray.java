package edu.umass.cs390cg.atmosphere.geom;

import javax.vecmath.Vector3d;

public class Ray {
  public Vector3d o,d;

  public Ray() {
    this.o = new Vector3d(0, 0, 0);
    this.d = new Vector3d(0, -1, 0);
  }

  public Ray(Ray ray) {
    this.o = new Vector3d(ray.o);
    this.d = new Vector3d(ray.d);
    this.d.normalize();
  }

  public Ray(Vector3d o, Vector3d d) {
    this.o = new Vector3d(o);
    this.d = new Vector3d(d);
    this.d.normalize();
  }

  public Vector3d pointAt(double t) {
    Vector3d point = new Vector3d();
    point.scaleAdd(t, d, o);
    return point;
  }
}