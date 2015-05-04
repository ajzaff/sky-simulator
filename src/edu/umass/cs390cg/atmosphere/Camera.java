package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3d;

public class Camera {
  public Vector3d eye, up, at;
  public double fovy, aspect_ratio;
  public Vector3d center, corner,across;

  public Camera(Vector3d eye,
                Vector3d gaze,
                Vector3d up,
                double fovy,
                double ar)	{

    // we will use a default camera
    this.eye = new Vector3d(eye);
    this.up = new Vector3d(up);
    this.at = new Vector3d(gaze);

    this.fovy = fovy;
    aspect_ratio = ar;

		/* Code for initializing camera
		 * Compute the four corner points of the camera's image plane */
    double dist = 1.f;
    double top = dist * Math.tan(this.fovy * Math.PI / 360.d);
    double bottom = -top;
    double right = aspect_ratio * top;
    double left = -right;

    center = this.eye;
    Vector3d W = gaze;
    W.negate();
    W.normalize();
    Vector3d V = this.up;
    Vector3d U = new Vector3d();
    U.cross(V, W);
    U.normalize();
    V.cross(W, U);

    corner = new Vector3d();
    corner.scaleAdd(left, U, center);
    corner.scaleAdd(bottom, V, corner);
    corner.scaleAdd(-dist, W, corner);

    across = new Vector3d(U);
    across.scale(right-left);

    this.up = new Vector3d(V);
    this.up.scale(top - bottom);
  }

  Ray getCameraRay(double x, double y)
  {
		/* getCameraRay function
		 * (x,y) is a normalized image coordinate, where
		 * both of them vary between [0,1] */

    Vector3d direction = new Vector3d();
    direction.scaleAdd(x, across, corner);
    direction.scaleAdd(y, up, direction);
    direction.sub(center);
    direction.normalize();
    return new Ray(center, direction);
  }
}
