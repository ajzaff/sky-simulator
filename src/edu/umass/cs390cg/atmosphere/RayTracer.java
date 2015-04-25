package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.scene.Scene;
import edu.umass.cs390cg.atmosphere.scene.SceneReader;

import javax.vecmath.Color3f;

public class RayTracer {

  private Scene scene;

  public static void main(String[] args) {
    if (args.length == 1) {
      new RayTracer(args[0]).startTracing();
    } else {
      System.out.println("Usage: java RayTracer input.scene");
    }
  }

  RayTracer(String sceneFile) {
    scene = new SceneReader(sceneFile).readScene();
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
    HitRecord hit = scene.intersectScene(ray);
    if(hit == null) return new Color3f();
    else if(hit.type == HitRecord.HitType.TYPE_SKY) {
      return scene.sky.calculateShading(ray, hit);
    }
    else if(hit.type == HitRecord.HitType.TYPE_TERRAIN) {
      return scene.terrain.color;
    }
    else /* SUN */ {
      return new Color3f(1,1,1);
    }
  }
}