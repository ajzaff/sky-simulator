package edu.umass.cs390cg.atmosphere.scene;

import edu.umass.cs390cg.atmosphere.Camera;
import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.geom.shapes.Sky;
import edu.umass.cs390cg.atmosphere.geom.Sun;
import edu.umass.cs390cg.atmosphere.geom.shapes.Terrain;

import javax.imageio.ImageIO;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Scene {
  public String output = "output.png";
  public Vector3d[][] image;
  public int width, height;
  public Terrain terrain = new Terrain();
  public Sky sky = new Sky();
  public Sun sun = new Sun();
  public Camera camera;

  public HitRecord intersectScene(Ray ray) {
    HitRecord r, best = null;
    double tMin = Double.MAX_VALUE;

    r=intersectTerrain(ray);
    if (r != null && r.t < tMin) {
      best = r;
      best.type = HitRecord.HitType.TYPE_TERRAIN;
    }

    // If there is a terrain intersection there is no need to check for sky collision
    r=intersectSky(ray);
    if(r != null && r.t < tMin) {
      best = r;
      best.type = HitRecord.HitType.TYPE_SKY;
      tMin = r.t;
    }

    return best;
  }



  public HitRecord intersectSky(Ray ray) {
    return sky.hit(ray, .001f, 0xffffff);
  }

  public HitRecord intersectTerrain(Ray ray) {
    return terrain.hit(ray, .001f, 0xffffff);
  }

  public void writeImage() {
    int x, y, index;
    int pixels[] = new int[width * height];

    index = 0;
    // apply a standard 2.2 gamma correction
    double gamma = 1.f / 2.2f;
    for (y=height-1; y >= 0; y --) {
      for (x=0; x<width; x ++) {
        Color3f c = new Color3f(image[x][y]);
        c.x = (float)Math.pow(c.x, gamma);
        c.y = (float)Math.pow(c.y, gamma);
        c.z = (float)Math.pow(c.z, gamma);
        c.clampMax(1.f);
        pixels[index++] = c.get().getRGB();

      }
    }

    BufferedImage oimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    oimage.setRGB(0, 0, width, height, pixels, 0, width);
    File outfile = new File(output);
    try {
      ImageIO.write(oimage, "png", outfile);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}