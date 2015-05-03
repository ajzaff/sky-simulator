package edu.umass.cs390cg.atmosphere.scene.parser;

import edu.umass.cs390cg.atmosphere.Camera;
import edu.umass.cs390cg.atmosphere.ScatteringEquations;
import edu.umass.cs390cg.atmosphere.scene.Scene;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import java.util.Scanner;

import static edu.umass.cs390cg.atmosphere.scene.SceneProperties.*;
import static edu.umass.cs390cg.atmosphere.scene.SceneProperties.PROPERTY_TERRAIN_RADIUS;

public class SceneParser {

  private final Scene scene;
  private int lineOffset;
  private int offset;

  // camera variables.
  private Vector3f at, eye, up;
  private float fov, ar;

  public SceneParser() {
    scene = new Scene();
  }

  public Scene yieldScene() {
    ScatteringEquations.sky = scene.sky;
    ScatteringEquations.terrain = scene.terrain;
    ar = 1.f*scene.width/scene.height;
    scene.camera = new Camera(eye,at,up,fov,ar);
    return scene;
  }

  public void parseLine(String line, int lineOffset) {
    this.lineOffset = lineOffset; offset = 0;
    line = line.trim().toLowerCase();
    if(line.startsWith("#")) return;
    if(line.startsWith(PROPERTY_SCENE_WIDTH)) {
      offset += PROPERTY_SCENE_WIDTH.length();
      scene.width = consumeInt(line);
    }
    if(line.startsWith(PROPERTY_SCENE_HEIGHT)) {
      offset += PROPERTY_SCENE_HEIGHT.length();
      scene.height = consumeInt(line);
    }
    else if(line.startsWith(PROPERTY_SCENE_OUTPUT)) {
      offset += PROPERTY_SCENE_OUTPUT.length();
      scene.output = line.substring(offset).trim();
    }
    else if(line.startsWith(PROPERTY_SKY_COLOR)) {
      offset += PROPERTY_SKY_COLOR.length();
      scene.sky.color = consumeColor(line);
    }
    else if(line.startsWith(PROPERTY_SKY_CENTER)) {
      offset += PROPERTY_SKY_CENTER.length();
      scene.sky.center = consumeVector(line);
    }
    else if(line.startsWith(PROPERTY_SKY_RADIUS)) {
      offset += PROPERTY_SKY_RADIUS.length();
      scene.sky.radius = consumeFloat(line);
    }
    else if(line.startsWith(PROPERTY_SUN_COLOR)) {
      offset += PROPERTY_SUN_COLOR.length();
      scene.sun.color = consumeColor(line);
    }
    else if(line.startsWith(PROPERTY_SUN_DIRECTION)) {
      offset += PROPERTY_SUN_DIRECTION.length();
      scene.sun.d = consumeVector(line);
      scene.sun.d.normalize();
    }
    else if(line.startsWith(PROPERTY_TERRAIN_COLOR)) {
      offset += PROPERTY_TERRAIN_COLOR.length();
      scene.terrain.color = consumeColor(line);
    }
    else if(line.startsWith(PROPERTY_TERRAIN_CENTER)) {
      offset += PROPERTY_TERRAIN_CENTER.length();
      scene.terrain.center = consumeVector(line);
    }
    else if(line.startsWith(PROPERTY_TERRAIN_RADIUS)) {
      offset += PROPERTY_TERRAIN_RADIUS.length();
      scene.terrain.radius = consumeFloat(line);
    }
    else if(line.startsWith(PROPERTY_CAMERA_AT)) {
      offset += PROPERTY_CAMERA_AT.length();
      at = consumeVector(line);
    }
    else if(line.startsWith(PROPERTY_CAMERA_EYE)) {
      offset += PROPERTY_CAMERA_EYE.length();
      eye = consumeVector(line);
    }
    else if(line.startsWith(PROPERTY_CAMERA_UP)) {
      offset += PROPERTY_CAMERA_UP.length();
      up = consumeVector(line);
    }
    else if(line.startsWith(PROPERTY_CAMERA_FOV)) {
      offset += PROPERTY_CAMERA_FOV.length();
      fov = consumeFloat(line);
    }
  }

  public int consumeInt(String line) {
    String floatString = line.substring(offset).trim();
    try {
      return new Integer(floatString);
    }
    catch (NumberFormatException e) {
      System.err.println("error (line " + lineOffset + "): " +
        "malformed int near: " + floatString + ".");
      return 0;
    }
  }

  public float consumeFloat(String line) {
    String floatString = line.substring(offset).trim();
    try {
      return new Float(floatString);
    }
    catch (NumberFormatException e) {
      System.err.println("error (line " + lineOffset + "): " +
        "malformed float near: " + floatString + ".");
      return 0.f;
    }
  }

  public Color3f consumeColor(String line) {
    String colorString = line.substring(offset).trim();
    String[] c;
    Color3f color = new Color3f();
    c = colorString.split(" ");

    try {
      color = new Color3f(new Float(c[0]),
        new Float(c[1]), new Float(c[2]));
      return color;
    }
    catch (NumberFormatException e) {
      System.err.println("error (line " + lineOffset + "): " +
        "color malformed near: " + colorString + ".");
      return color;
    }
    catch (IndexOutOfBoundsException e) {
      System.err.println("error (line "+lineOffset+"): " +
        "expected 3 color components " +
        "got " + c.length+".");
      return color;
    }
  }

  public Vector3f consumeVector(String line) {
    String vectorString = line.substring(offset).trim();
    String[] c;
    Vector3f vector = new Vector3f();
    c = vectorString.split(" ");

    try {
      vector = new Vector3f(new Float(c[0]),
        new Float(c[1]), new Float(c[2]));
      return vector;
    }
    catch (NumberFormatException e) {
      System.err.println("error (line " + lineOffset + "): " +
        "vector malformed near: " + vectorString + ".");
      return vector;
    }
    catch (IndexOutOfBoundsException e) {
      System.err.println("error (line "+lineOffset+"): " +
        "expected 3 vector components " +
        "got " + c.length+".");
      return vector;
    }
  }
}
