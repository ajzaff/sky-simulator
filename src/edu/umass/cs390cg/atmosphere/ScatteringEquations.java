package edu.umass.cs390cg.atmosphere;
import javax.vecmath.*;

import edu.umass.cs390cg.atmosphere.geom.Ray;

public class ScatteringEquations {
	
	/**
	 * This calculates how much light is scattered in the 
	 * direction of the camera
	 * @param theta is the angle between two rays
	 * @param g affects the symmetry of scattering
	 * where g=0 results in symmetrical Rayleigh scattering
	 * and -.999 < g < -.75 results in Mie aerosol scattering
	 * @return
	 */
	public float PhaseFunction(float theta, float g){
		return (float) ((3*(1-g*g)  * (1+Math.pow(Math.cos(theta), 2))) /
				    ((2*(2 + g*g)) * Math.pow((1 + g*g -2*g*Math.cos(theta)), 1.5)));
	}
	
	/**
	 * Given two points in the air, this calculates how much light along that segment
	 * gets scattered away
	 * @param P_A Point A in the atmosphere
	 * @param P_B Point B in the atmosphere
	 * @param wavelength The wavelength of light you are calculating for
	 * @return
	 */
	public float RayleighOutScatterAmount(Vector3f P_A, Vector3f P_B, float wavelength){
		
		return 0f;
	}
	
	/**
	 * Given two points in the air, this calculates how much light along that segment
	 * gets scattered away
	 * @param P_A Point A in the atmosphere
	 * @param P_B Point B in the atmosphere
	 * @param wavelength The wavelength of light you are calculating for
	 * @return
	 */
	public float MieOutScatterAmount(Vector3f P_A, Vector3f P_B, float wavelength){
		
		return 0f;
	}
	
	/**
	 * This calculates how much light is added to a ray through scattering
	 * @param P_A
	 * @param P_B
	 * @param wavelength
	 * @return
	 */
	public float InScatterAmount(Vector3f P_A, Vector3f P_B, float wavelength){
		return 0f;
	}
	
	public Color3f GetInScatterColor(Ray ray){
		
	}
	
	// 
	
}
