package edu.umass.cs390cg.atmosphere.geom;

import edu.umass.cs390cg.atmosphere.geom.shapes.Material;

import javax.vecmath.Vector3d;

public class HitRecord {

  public enum HitType {
    TYPE_SKY, TYPE_TERRAIN, TYPE_SUN
  }

  public Material material;
  public HitType type;
  public double t;
  public Vector3d pos = new Vector3d(0,0,0),
    normal = new Vector3d(0,0,0);

  public void set(HitRecord r) {
    this.t = r.t;
    this.pos.set(r.pos);
    this.normal.set(r.normal);
  }
}