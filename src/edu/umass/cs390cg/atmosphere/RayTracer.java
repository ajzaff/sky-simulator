package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.scene.Scene;
import edu.umass.cs390cg.atmosphere.scene.SceneReader;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

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

    scene.image = new Vector3d[scene.width][scene.height];
    for(i=0;i<scene.width;i++)
      for(j=0;j<scene.height;j++)
        scene.image[i][j] = new Vector3d();

    PixelThread.dest = scene.image;
    PixelThread.width = scene.width;
    PixelThread.height = scene.height;
    PixelThread.camera = scene.camera;
    PixelThread.tracer = this;
    PixelThread.samples = 1;
    List<Thread> threads = new ArrayList<Thread>();
    float timeAtStart = System.currentTimeMillis();

    //region Threaded
    /*for(int y = 0; y < scene.height; y++){
      Thread newThread = new Thread(new PixelThread(y));
      newThread.start();
      threads.add(newThread);
    }
    for(Thread t : threads){
      try {
        t.join();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }//*/
    //endregion

    //region Non threaded
    float x, y;
    for (j = 0; j < scene.height; j++) {
      y = (float) j / (float) scene.height;
      System.out.print("\rray tracing... " + j * 100 / scene.height + "%");
      for (i = 0; i < scene.width; i++) {
        x = (float) i / (float) scene.width;
        scene.image[i][j] = trace(scene.camera.getCameraRay(x, y));
      }
    }//*/
    //endregion


    float timeAtEnd = System.currentTimeMillis();
    System.out.println("Took " + (timeAtEnd-timeAtStart) + " milliseconds");

    System.out.println("\rray tracing completed.");
    scene.writeImage();
  }

  public Vector3d trace(Ray ray) {
    /*float d2 = -ray.d.dot(scene.sun.d);
    d2=(float)Math.pow(d2,14);
    return new Color3f(d2,d2,d2);*/
    HitRecord hit = scene.intersectScene(ray);
    if(hit == null) return new Vector3d();
    else if(hit.type == HitRecord.HitType.TYPE_SKY) {
      return new Vector3d(0,0,1);//scene.sky.calculateShading(ray, hit);
    }
    else {
      System.out.println(scene.terrain.color);
      //return new Color3f();
      return scene.terrain.color;
    }
  }
}