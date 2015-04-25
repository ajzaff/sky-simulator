package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3f;

public class Sphere implements Shape {
  public Vector3f center;
  public float radius;

  public Sphere() {
  }

  public Sphere(Vector3f pos, float r) {
    center = new Vector3f(pos);
    radius = r;
  }

  @Override
  public HitRecord hit(Ray ray, float tmin, float tmax) {

		/* compute ray-plane intersection */
    Vector3f temp = new Vector3f(ray.o);
    temp.sub(center);
    float A = ray.d.dot(ray.d);
    float B = 2f * temp.dot(ray.d);
    float C = temp.dot(temp) - (radius*radius);
    float innerTerm = B*B - 4f*A*C;

    if(innerTerm < 0)
      return null;

		/* if t out of range, return null */
    float t0 = (-B + (float)Math.sqrt(innerTerm)) / (2f*A);
    float t1 = (-B - (float)Math.sqrt(innerTerm)) / (2f*A);
    if ((t0 < tmin || t0 > tmax) && (t1 < tmin || t1 > tmax))
      return null;
    float t;
    if(t0 < tmin) t=t1;
    else if(t1 < tmin) t=t0;
    else t=Math.min(t0,t1);

    temp = new Vector3f(ray.pointAt(t));
    temp.sub(center);

		/* construct hit record */
    HitRecord rec = new HitRecord();
    rec.pos = ray.pointAt(t);		// position of hit point
    rec.t = t;						// parameter t (distance along the ray)
    rec.normal = temp;					// normal at the hit point
    rec.normal.normalize();			// normal should be normalized

    return rec;
  }
}
