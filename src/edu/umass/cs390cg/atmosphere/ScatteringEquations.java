//region
package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.geom.shapes.Sky;
import edu.umass.cs390cg.atmosphere.geom.shapes.Terrain;
import edu.umass.cs390cg.atmosphere.numerics.Function;
import edu.umass.cs390cg.atmosphere.numerics.Integrals;

import javax.vecmath.Vector3d;

import static edu.umass.cs390cg.atmosphere.RayTracer.*;

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

    public static double Kr = 0.0025d;
    public static double Km = 0.001d;
    public static double Kr4Pi = Kr * 4 * PI;
    public static double Km4pi = Km * 4 * PI;

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
        scale = 1d / (sky.radius - terrain.radius);
        scaleOverScaleDepth = scale / scaleDepth;
    }

    //endregion

    //region NewCode
    private static double scale(double Cos) {
        double x = 1f - Cos;
        return scaleDepth * exp(-0.00287 + x * (0.459 + x * (3.83 + x * (-6.80 + x * 5.25))));
    }

    public Vector3d InScatter(Ray ray, HitRecord hit){
        Vector3d totalLight = GetRawScatter(ray, hit);
        Vector3d RayleighLight Scale(totalLight, RayleighPhaseFunction())
    }

    public Vector3d GetRawScatter(Ray ray, HitRecord hit) {
        double rayLength = Subtract(hit.pos, ray.o).length();

        Vector3d startPoint = ray.o;
        Vector3d endPoint = hit.pos;
        double cameraHeight = height(startPoint);
        double cameraDepth = exp(scaleOverScaleDepth * (terrain.radius - cameraHeight));

        double startAngle = ray.d.dot(startPoint) / cameraHeight;
        double startOffset = cameraDepth * scale(startAngle);

        double sampleLength = rayLength / samplesPerInScatterRay;
        double scaledLength = sampleLength * scale;

        Vector3d RaySegment = Scale(ray.d, sampleLength);
        Vector3d SamplePoint = Add(startPoint, Scale(RaySegment, 0.5d));


        Vector3d myColor = Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public Vector3d evaluate(Object[] args) {
                        Vector3d v = (Vector3d) args[0];
                        double sampleHeight = height(v);
                        double depth = exp(scaleOverScaleDepth * (terrain.radius - sampleHeight));
                        double lightAngle = v.dot(r.scene.sun.d) / sampleHeight;
                        double cameraAngle = v.dot(ray.d) / sampleHeight;

                        double forwardScatter = (startOffset +
                                cameraDepth * (scale(lightAngle) - scale(cameraAngle)));

                        Vector3d lightToAttenuate = Scale(Add(Scale(InvWavelength, Kr4Pi), Km4pi), -forwardScatter);

                        Vector3d addedLight = new Vector3d(
                                exp(lightToAttenuate.x),
                                exp(lightToAttenuate.y),
                                exp(lightToAttenuate.z));
                        return Scale(addedLight, depth * scaledLength);
                    }
                },
                startPoint, endPoint, scaledLength, samplesPerOutScatterRay
        );
        return Scale(myColor, r.scene.sun.color);
    }

    //region Phase (theta, g)

    /**
     * This calculates how much light is scattered in the
     * direction of the camera
     *
     * @param theta is the angle between two rays
     *              where g=0 results in symmetrical Rayleigh scattering
     *              and -.999 < g < -.75 results in Mie aerosol scattering
     * @return
     */
    public static double RayleighPhaseFunction(double theta) {
        return (3d / 4 * (1 + cos(theta)));
    }

    /**
     * This calculates how much light is scattered in the
     * direction of the camera
     *
     * @param theta is the angle between two rays
     * @param g     affects the symmetry of scattering
     *              where g=0 results in symmetrical Rayleigh scattering
     *              and -.999 < g < -.75 results in Mie aerosol scattering
     * @return
     */
    public static double MiePhaseFunction(float theta, float g) {
        double cos_theta, gg;

        cos_theta = cos(theta);
        gg = g * g;

        return (3 * (1 - gg)) / (2 * (2 + gg)) *
                (1 + cos_theta * cos_theta) /
                pow(1 + gg - 2 * g * cos_theta, 3d / 2);
    }
    //endregion

    /*
    public static double OpticalDepth(Vector3d A, Vector3d B) {
        return Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public double evaluate(Object[] args) {
                        Vector3d v = (Vector3d) args[0];
                        double sampleHeight = height(v);
                        return exp(-sampleHeight * scaleOverScaleDepth);
                    }
                },
                A, B, samplesPerOutScatterRay
        );
    }*/


    /**
     * Gets the vertical distance from the terrain surface
     *
     * @param pos a point in the atmosphere.
     * @return the altutide of this point [0,1) iif
     * the point is contained within the atmosphere.
     */
    public static double height(Vector3d pos) {
        double height = Subtract(pos, terrain.center).length() - terrain.radius;
        if (height < 0 || height > sky.radius - terrain.radius) {

            System.out.println("Height function broken, given value out of range");
            System.out.println(height + " height, pos = " + pos);
        }
        return height;
    }

    //endregion

}
