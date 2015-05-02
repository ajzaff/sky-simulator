package edu.umass.cs390cg.atmosphere;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.numerics.Function;
import edu.umass.cs390cg.atmosphere.numerics.Integrals;

import javax.vecmath.Vector3f;

import static edu.umass.cs390cg.atmosphere.RayTracer.r;
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
	public static float PhaseFunction(float theta, float g) {
		float cos_theta, gg;

		cos_theta = (float) cos(theta);
		gg = g*g;

		return (3*(1-gg))/(2*(2+gg)) *
				(1+cos_theta*cos_theta)/
				(float)Math.pow(1+gg-2*g*cos_theta,3f/2);
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
	 * The scattering constant function for a given wavelength.
	 * @param w a wavelength of light.
	 * @return the scattering amount.
	 */
	public static float K(float w) {
		return 1/(w*w*w*w);
	}

	/**
	 * Gets the altitude of a point in the atmosphere.
	 * @param pos a point in the atmosphere.
	 * @return the altutide of this point [0,1) iif
	 * 				 the point is contained within the atmosphere.
	 */
	public static float h(Vector3f pos) {
		Ray ray;
		Vector3f v;

		v = new Vector3f(r.scene.terrain.center);
		v.sub(pos);

		ray = new Ray();
		ray.o = pos;
		ray.d = v;
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

		float pi = (float)PI;
		float K = K(wavelength);

		return 4*pi*K* Integrals.estimateIntegral(
				new Function() {
					@Override
					public float evaluate(Object[] args) {
						Vector3f v = (Vector3f) args[0];

						float altitude
						return pow(2, )
					}
				},
				P_A, P_B, 100
		);
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
