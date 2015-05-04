package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.Sun;
import edu.umass.cs390cg.atmosphere.scene.Scene;
import edu.umass.cs390cg.atmosphere.scene.SceneReader;

import javax.vecmath.Vector3d;

import java.io.File;

import static edu.umass.cs390cg.atmosphere.numerics.Vec.*;

public class TweenDriver {

  private static final String startPath = "scene/start.scene";
  private static final String endPath = "scene/end.scene";
  private static final int N = 20;

  public static void main(String[] args) {
    Scene scene, end;
    int i; double _n;
    String outputDirectory;
    RayTracer r;
    Vector3d gaze, up;

    /* find the first unused output directory. */
    for(i=1; ; i++) {
      if(!new File("sunset" + i + "/").exists()) {
        outputDirectory = "sunset" + i + "/";
        new File(outputDirectory).mkdir();
        break;
      }
    }

    r = new RayTracer(startPath);
    end = new SceneReader(endPath).readScene();
    scene = r.scene;

    _n = 1d/N;
    /* camera tweening. */
    Vector3d _d_camera_gaze = Scale(Subtract(end.camera.at,scene.camera.at),_n);
    Vector3d _d_camera_eye = Scale(Subtract(end.camera.eye,scene.camera.eye),_n);
    Vector3d _d_camera_up = Scale(Subtract(end.camera.up,scene.camera.up),_n);
    double   _d_camera_fov = (end.camera.fovy-scene.camera.fovy)*_n;


    System.out.println(_d_camera_gaze);
    System.out.println(_d_camera_eye);
    System.out.println(_d_camera_up);
    System.out.println(_d_camera_fov);

    /* sun tweening. */
    Vector3d _d_sun_direction = Scale(Subtract(end.sun.d,scene.sun.d),_n);
    Vector3d _d_sun_color = Scale(Subtract(end.sun.color,scene.sun.color),_n);

    System.out.println(_d_sun_direction);
    System.out.println(_d_sun_color);

    /* terrain tweening. */
    Vector3d _d_terrain_color = Scale(Subtract(end.terrain.color,scene.terrain.color),_n);

    System.out.println(_d_terrain_color);

    for(i=1; i <= N; i++) {
      scene.output = outputDirectory + i + ".png";
      r.startTracing();

      /* tween camera values. */
      gaze = Add(scene.camera.at, _d_camera_gaze);
      up = Add(scene.camera.up, _d_camera_up);
      gaze.normalize();
      up.normalize();
      scene.camera = new Camera(
          Add(scene.camera.eye, _d_camera_eye), gaze, up,
          scene.camera.fovy + _d_camera_fov,
          scene.camera.aspect_ratio
      );

      /* tween sun values. */
      scene.sun.d = Add(r.scene.sun.d, _d_sun_direction);
      scene.sun.color = Add(r.scene.sun.color, _d_sun_color);
      scene.sun.d.normalize();

      /* tween terrain values. */
      scene.terrain.color = Add(r.scene.terrain.color, _d_terrain_color);
    }
  }
}
