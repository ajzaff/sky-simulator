package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.numerics.Vec;

import javax.vecmath.Color3f;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

import javax.imageio.*;
import javax.vecmath.Vector3d;


import java.io.*;

public class PixelThread implements Runnable{
	static Vector3d[][] dest;
	static int width, height;
	static Camera camera;
	static RayTracer tracer;
	static int samples = 1;
	static Random rand = new Random();
	private int y;
	
	public PixelThread(int y){
		this.y = y;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// The percent down the screen this pixel is at
		double yAmt = (double)y / (double)height;
		// This jitters the ray within a pixel's width
		double xJitRange = 1d/width;
		// The jitters the ray within a pixel height
		double yJitRange = 1d/height;
		//System.out.println(xJitRange + " : " + width + " " + yJitRange);
		
		for(int x = 0; x < width; x++){
			// The x percent across the screen
			double xAmt = (double)x / width;
			
			// Make samples and average the color
			Vector3d pixelColor = new Vector3d();
			for(int s = 0; s < samples;s++){
				double xJitter = rand.nextDouble()*xJitRange;
				double yJitter = rand.nextDouble()*yJitRange;
				Vector3d color = tracer.trace(
						camera.getCameraRay(xAmt + xJitter, yAmt + yJitter));
								
				pixelColor.add(color);
			}

			dest[x][y] = Vec.Scale(pixelColor, (1d/samples));
		}
	}

}
