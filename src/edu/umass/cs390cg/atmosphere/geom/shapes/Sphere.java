package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3d;

public class Sphere implements Shape {
  public Vector3d center;
  public double radius;
  public Material material = new Material();

  public Sphere() {
  }

  public Sphere(Vector3d pos, double r) {
    center = new Vector3d(pos);
    radius = r;
  }

  @Override
  public HitRecord hit(Ray ray, double tmin, double tmax) {

		/* compute ray-plane intersection */
    Vector3d temp = new Vector3d(ray.o);
    temp.sub(center);
    double A = ray.d.dot(ray.d);
    double B = 2f * temp.dot(ray.d);
    double C = temp.dot(temp) - (radius*radius);
    double innerTerm = B*B - 4f*A*C;

    if(innerTerm < 0)
      return null;

		/* if t out of range, return null */
    double t0 = (-B + Math.sqrt(innerTerm)) / (2f*A);
    double t1 = (-B - Math.sqrt(innerTerm)) / (2f*A);
    if ((t0 < tmin || t0 > tmax) && (t1 < tmin || t1 > tmax))
      return null;
    double t;
    if(t0 < tmin) t=t1;
    else if(t1 < tmin) t=t0;
    else t=Math.min(t0,t1);

    temp = new Vector3d(ray.pointAt(t));
    temp.sub(center);

		/* construct hit record */
    HitRecord rec = new HitRecord();
    rec.pos = ray.pointAt(t);		// position of hit point
    rec.t = t;						// parameter t (distance along the ray)
    rec.normal = temp;					// normal at the hit point
    rec.normal.normalize();			// normal should be normalized
    rec.material = material;
    return rec;
  }
}
