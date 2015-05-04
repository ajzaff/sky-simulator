package edu.umass.cs390cg.atmosphere.geom.shapes;// Material class
// defines material which encapsulates Diffuse, Specular,
// Reflective and Refractive parameters

import javax.vecmath.*;

import static edu.umass.cs390cg.atmosphere.numerics.Vec.*;

public class Material {

	public Vector3d Ka;	// ambient reflectance
	public Vector3d Kd;	// diffuse reflectance
	public Vector3d Ks;	// specular reflectance
	public Vector3d Kr;	// reflective color
	public Vector3d Kt;	// transmitive (refractive) color
	public double phong_exp;		// phong specular exponent
	public double ior;			// index of refraction
	public String Name;
	public String Type;
	public double lowNoise, highNoise;

	// Returns the noise coefficient, or 1 if none
	public double GetNoise(){
		double noiseDiff = highNoise - lowNoise;
		if(!isCloseEnough(noiseDiff, 0, 0.01)){
			return Map(rand.nextDouble(), 0d, 1d, lowNoise, highNoise);
		}
		else
			return 1d;
	}
	
	public Material()
	{
		Ka = new Vector3d(0,0,0);
		Kd = new Vector3d(0,0,0);
		Ks = new Vector3d(0,0,0);
		Kr = new Vector3d(0,0,0);
		Kt = new Vector3d(0,0,0);
		phong_exp = 1.f;
		ior = 1.f;
	}
	static public Material makeDiffuse(Vector3d a, Vector3d d) {
		Material m = new Material();
		m.Ka = new Vector3d(a);
		m.Kd = new Vector3d(d);
		return m;
	}
	static public Material makeSpecular(Vector3d a, Vector3d d, Vector3d s, double _exp) {
		Material m = new Material();
		m.Ka = new Vector3d(a);
		m.Kd = new Vector3d(d);
		m.Ks = new Vector3d(s);
		m.phong_exp = _exp;
		return m;
	}
	static public Material makeMirror(Vector3d r) {
		Material m = new Material();
		m.Kr = new Vector3d(r);
		return m;
	}
	static public Material makeGlass(Vector3d r, Vector3d t, double _ior) {
		Material m = new Material();
		m.Kr = new Vector3d(r);
		m.Kt = new Vector3d(t);
		m.ior = _ior;
		return m;
	}
	static public Material makeSuper(Vector3d a, Vector3d d, Vector3d s, double _exp, Vector3d r, Vector3d t, double _ior) {
		Material m = new Material();
		m.Ka = new Vector3d(a);
		m.Kd = new Vector3d(d);
		m.Ks = new Vector3d(s);
		m.phong_exp = _exp;
		m.Kr = new Vector3d(r);
		m.Kt = new Vector3d(t);
		m.ior = _ior;
		return m;
	}
}

