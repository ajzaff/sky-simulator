//region
package edu.umass.cs390cg.atmosphere;

import com.sun.scenario.effect.impl.prism.ps.PPSBlend_COLOR_BURNPeer;
import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.geom.shapes.Sky;
import edu.umass.cs390cg.atmosphere.geom.shapes.Terrain;
import edu.umass.cs390cg.atmosphere.numerics.Function;
import edu.umass.cs390cg.atmosphere.numerics.Integrals;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3d;

import java.util.Vector;

import static edu.umass.cs390cg.atmosphere.RayTracer.r;
import static edu.umass.cs390cg.atmosphere.numerics.Vec.*;
import static java.lang.Math.*;

public class ScatteringEquations {

    //region Declarations

    public static int samplesPerInScatterRay = 10;
    public static int samplesPerOutScatterRay = 50;
    public static Sky sky;
    public static Terrain terrain;
    public static double scale; // 1 / (Outer radius - inner radius)
    public static double scaleDepth = 0.25d; // Depth of average atmospheric density, 0.25
    public static double scaleOverScaleDepth;

    public static final double Mie_G = -.8d;
    public static double KMie = 0.0015d;
    public static Vector3d Wavelength = new Vector3d(0.650f, 0.570f, 0.475f);
    public static Vector3d InvWavelength = new Vector3d(
            1d / Math.pow(Wavelength.x, 4),
            1d / Math.pow(Wavelength.y, 4),
            1d / Math.pow(Wavelength.z, 4));

    public static void Initialize(Sky sky, Terrain terrain) {
        ScatteringEquations.sky = sky;
        ScatteringEquations.terrain = terrain;
        scale = 1f / (sky.radius - terrain.radius);
        scaleOverScaleDepth = scale / scaleDepth;

    }

    //endregion

    //region NewCode
    private static double scale(double Cos) {
        double x = 1f - Cos;
        return scaleDepth * exp(-0.00287 + x * (0.459 + x * (3.83 + x * (-6.80 + x * 5.25))));
    }

    public Vector3d Scatter(Ray ray, HitRecord hit) {
        Vector3d cameraRay = Subtract(hit.pos, ray.o);
        double cameraRayLength = cameraRay.length();
        cameraRay.scale(1f / cameraRayLength);

        Vector3d startPoint = ray.o;
        Vector3d endPoint = hit.pos;
        double cameraHeight = height(startPoint);
        double cameraDepth = (float)exp(scaleOverScaleDepth * (terrain.radius - cameraHeight));



    }

    //endregion

}
