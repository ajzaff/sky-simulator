package edu.umass.cs390cg.atmosphere;
import javax.vecmath.Vector3f;

import static java.lang.Math.*;

public class ScatteringEquations {

	public static final float Mie_G = -.8f;
	public static final float Step  = .1f;

	/**
	 * This calculates how much light is scattered in the 
	 * direction of the camera
	 * @param theta is the angle between two rays
	 * @param g affects the symmetry of scattering
	 * where g=0 results in symmetrical Rayleigh scattering
	 * and -.999 < g < -.75 results in Mie aerosol scattering
	 * @return
	 */
	public static float MiePhaseFunction(float theta, float g) {
		float cos_theta, gg;

		cos_theta = (float) cos(theta);
		gg = g*g;

		return (3*(1-gg))/(2*(2+gg)) *
				(1+cos_theta*cos_theta)/
				(float)Math.pow(1+gg-2*g*cos_theta,3f/2);
	}

	/**
	 * This calculates how much light is scattered in the
	 * direction of the camera
	 * @param theta is the angle between two rays
	 * @param g affects the symmetry of scattering
	 * where g=0 results in symmetrical Rayleigh scattering
	 * and -.999 < g < -.75 results in Mie aerosol scattering
	 * @return
	 */
	public static float RayleighPhaseFunction(float cosTheta) {
		return (float)(3f/4 * (1 + cos(theta)));
	}
	
	/**
	 * Given two points in the air, this calculates how much light along that segment
	 * gets scattered away
	 * @param P_A Point A in the atmosphere
	 * @param P_B Point B in the atmosphere
	 * @param wavelength The wavelength of light you are calculating for
	 * @return
	 */
	public static float RayleighOutScatterAmount(Vector3f P_A, Vector3f P_B, float wavelength){
		
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
	public static float MieOutScatterAmount(Vector3f P_A, Vector3f P_B, float wavelength){
		
		return 0f;
	}
	
	/**
	 * This calculates how much light is added to a ray through scattering
	 * @param P_A
	 * @param P_B
	 * @param wavelength
	 * @return
	 */
	public static float InScatterAmount(Vector3f P_A, Vector3f P_B, float wavelength){
		return 0f;
	}

	//
	
}
