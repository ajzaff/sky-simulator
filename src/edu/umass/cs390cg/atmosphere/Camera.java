package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3f;

public class Camera {
  public Vector3f eye, up, at;
  public float fovy, aspect_ratio;
  public Vector3f center, corner,across;

  public Camera(Vector3f eye,
                Vector3f at,
                Vector3f up,
                float fovy,
                float ar)	{

    // we will use a default camera
    this.eye = new Vector3f(eye);
    this.up = new Vector3f(up);
    this.at = new Vector3f(at);

    this.fovy = fovy;
    aspect_ratio = ar;

		/* Code for initializing camera
		 * Compute the four corner points of the camera's image plane */
    float dist = 1.f;
    float top = dist * (float)Math.tan(this.fovy * Math.PI / 360.f);
    float bottom = -top;
    float right = aspect_ratio * top;
    float left = -right;
    Vector3f gaze = new Vector3f();
    gaze.sub(this.at, this.eye);

    center = this.eye;
    Vector3f W = gaze;
    W.negate();
    W.normalize();
    Vector3f V = this.up;
    Vector3f U = new Vector3f();
    U.cross(V, W);
    U.normalize();
    V.cross(W, U);

    corner = new Vector3f();
    corner.scaleAdd(left, U, center);
    corner.scaleAdd(bottom, V, corner);
    corner.scaleAdd(-dist, W, corner);

    across = new Vector3f(U);
    across.scale(right-left);

    this.up = new Vector3f(V);
    this.up.scale(top - bottom);
  }

  Ray getCameraRay(float x, float y)
  {
		/* getCameraRay function
		 * (x,y) is a normalized image coordinate, where
		 * both of them vary between [0,1] */

    Vector3f direction = new Vector3f();
    direction.scaleAdd(x, across, corner);
    direction.scaleAdd(y, up, direction);
    direction.sub(center);
    direction.normalize();
    return new Ray(center, direction);
  }
}
