package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.scene.Scene;
import edu.umass.cs390cg.atmosphere.scene.SceneReader;

import javax.vecmath.Color3f;

public class RayTracer {

  public static final RayTracer r = new RayTracer();

  public Scene scene;

  public static void main(String[] args) {
    if (args.length == 1) {
      r.scene = new SceneReader(args[0]).readScene();
      r.startTracing();
    } else {
      System.out.println("Usage: java RayTracer input.scene");
    }
  }

  public void startTracing() {
    int i,j;

    scene.image = new Color3f[scene.width][scene.height];
    for(i=0;i<scene.width;i++)
      for(j=0;j<scene.height;j++)
        scene.image[i][j] = new Color3f();

    float x, y;
    for (j = 0; j < scene.height; j++) {
      y = (float) j / (float) scene.height;
      System.out.print("\rray tracing... " + j * 100 / scene.height + "%");
      for (i = 0; i < scene.width; i++) {
        x = (float) i / (float) scene.width;
        scene.image[i][j] = trace(scene.camera.getCameraRay(x, y));
      }
    }
    System.out.println("\rray tracing completed.");
    scene.writeImage();
  }

  private Color3f trace(Ray ray) {
    /*float d2 = -ray.d.dot(scene.sun.d);
    d2=(float)Math.pow(d2,14);
    return new Color3f(d2,d2,d2);*/
    HitRecord hit = scene.intersectScene(ray);
    if(hit == null) return new Color3f();
    else if(hit.type == HitRecord.HitType.TYPE_SKY) {
      return scene.sky.calculateShading(ray, hit);
    }
    else {
      return scene.terrain.color;
    }
  }
}