package edu.umass.cs390cg.atmosphere.geom;

import javax.vecmath.Vector3f;

public class HitRecord {

  public enum HitType {
    TYPE_SKY, TYPE_TERRAIN, TYPE_SUN
  }

  public HitType type;
  public float t;
  public Vector3f pos = new Vector3f(0,0,0),
    normal = new Vector3f(0,0,0);

  public void set(HitRecord r) {
    this.t = r.t;
    this.pos.set(r.pos);
    this.normal.set(r.normal);
  }
}