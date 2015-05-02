package edu.umass.cs390cg.atmosphere.scene;

import edu.umass.cs390cg.atmosphere.scene.parser.SceneParser;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SceneReader {

  private SceneParser sceneParser;
  private Scanner sc = null;

  public SceneReader(String sceneFile) {
    File file;

    try {
      file = new File(sceneFile);
      sc = new Scanner(file);
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public Scene readScene() {
    int i=1;
    sceneParser = new SceneParser();
    while(sc.hasNextLine()) {
      sceneParser.parseLine(sc.nextLine(), i++);
    }
    return sceneParser.yieldScene();
  }
}