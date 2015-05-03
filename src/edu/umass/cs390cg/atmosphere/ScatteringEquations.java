package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.geom.shapes.Sky;
import edu.umass.cs390cg.atmosphere.geom.shapes.Terrain;
import edu.umass.cs390cg.atmosphere.numerics.Function;
import edu.umass.cs390cg.atmosphere.numerics.Integrals;
import edu.umass.cs390cg.atmosphere.scene.Scene;

import static edu.umass.cs390cg.atmosphere.RayTracer.*;

import edu.umass.cs390cg.atmosphere.numerics.Vec;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import static edu.umass.cs390cg.atmosphere.RayTracer.r;
import static java.lang.Math.*;

public class ScatteringEquations {

    public static final float Mie_G = -.8f;
    public static int samplesPerInScatterRay = 10;
    public static int samplesPerOutScatterRay = 50;

    public static float RayleighScaleHeight;

    public static float OuterRadius;
    public static Sky sky;
    public static Terrain terrain;
    public static float scale; // 1 / (Outer radius - inner radius)
    public static float scaleDepth = 0.25f; // Depth of average atmospheric density, 0.25
    public static float scaleOverScaleDepth = scale/scaleDepth;

    public static float KMie = 0.0015f;
    public static Color3f Wavelength = new Color3f(0.650f, 0.570f, 0.475f);
    public static Color3f InvWavelength = new Color3f(
            Wavelength.x * Wavelength.x * Wavelength.x * Wavelength.x,
            Wavelength.y * Wavelength.y * Wavelength.y * Wavelength.y,
            Wavelength.z * Wavelength.z * Wavelength.z * Wavelength.z);

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
    public static float MiePhaseFunction(float theta, float g) {
        float cos_theta, gg;

        cos_theta = (float) cos(theta);
        gg = g * g;

        return (3 * (1 - gg)) / (2 * (2 + gg)) *
                (1 + cos_theta * cos_theta) /
                (float) Math.pow(1 + gg - 2 * g * cos_theta, 3f / 2);
    }

    /**
     * This calculates how much light is scattered in the
     * direction of the camera
     *
     * @param theta is the angle between two rays
     *              where g=0 results in symmetrical Rayleigh scattering
     *              and -.999 < g < -.75 results in Mie aerosol scattering
     * @return
     */
    public static float RayleighPhaseFunction(float theta) {
        return (float) (3f / 4 * (1 + Math.cos(theta)));
    }


    /**
     * The scattering constant function for a given wavelength.
     *
     * @param w a wavelength of light.
     * @return the scattering amount.
     */
    public static float Krayleigh(float w) {
        if(w == Wavelength.x)
            return InvWavelength.x;
        if(w == Wavelength.y)
            return InvWavelength.y;
        if(w == Wavelength.z)
            return InvWavelength.z;

        System.out.println("Wavelength error");
        return 1f;
    }

    /**
     * Gets the altitude of a point in the atmosphere.
     *
     * @param pos a point in the atmosphere.
     * @return the altutide of this point [0,1) iif
     * the point is contained within the atmosphere.
     */
    public static float height(Vector3f pos) {
        return Vec.SubVec(pos, terrain.center).length() - terrain.radius;
    }

    /**
     * Returns how much air is between two points in the atmosphere
     *
     * @param P_A
     * @param P_B
     * @return
     */
    public static float GetOpticalDepth(Vector3f P_A, Vector3f P_B) {

        Vector3f ray = Vec.SubVec(P_B, P_A);
        float lengthOfSample = ray.length() / samplesPerOutScatterRay;
        ray.normalize();
        Vector3f RayDir = Vec.ScaleVec(ray, lengthOfSample);
        Vector3f samplePoint = Vec.AddVec(P_A, Vec.ScaleVec(RayDir, 0.5f));

        float opticalDepth = 0f;
        for (int i = 0; i < samplesPerOutScatterRay; i++) {
            float sampleHeight = height(samplePoint);
            opticalDepth += Math.exp(sampleHeight * scaleOverScaleDepth) * lengthOfSample;
            samplePoint = Vec.AddVec(samplePoint, Vec.ScaleVec(RayDir, 0.5f));
        }
        return opticalDepth;
    }

    /**
     * Given two points in the air, this calculates how much light along that segment
     * gets scattered away
     *
     * @param P_A        Point A in the atmosphere
     * @param P_B        Point B in the atmosphere
     * @param wavelength The wavelength of light you are calculating for
     * @return
     */
    public static float MieOutScatterAmount(Vector3f P_A, Vector3f P_B, float wavelength) {

        float pi = (float) PI;
        float K = KMie;

        return 4 * pi * K * Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public float evaluate(Object[] args) {
                        Vector3f v = (Vector3f) args[0];
                        float sampleHeight = height(v);
                        return (float)Math.exp(-sampleHeight * scaleOverScaleDepth);
                    }
                },
                P_A, P_B, samplesPerOutScatterRay
        );
    }
    /**
     * Given two points in the air, this calculates how much light along that segment
     * gets scattered away
     *
     * @param P_A        Point A in the atmosphere
     * @param P_B        Point B in the atmosphere
     * @param wavelength The wavelength of light you are calculating for
     * @return
     */
    public static float RayleighOutScatterAmount(Vector3f P_A, Vector3f P_B, float wavelength) {

        float pi = (float) PI;
        float K = Krayleigh(wavelength);

        return 4 * pi * K * Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public float evaluate(Object[] args) {
                        Vector3f v = (Vector3f) args[0];
                        float sampleHeight = height(v);
                        return (float)Math.exp(-sampleHeight * scaleOverScaleDepth);
                    }
                },
                P_A, P_B, samplesPerOutScatterRay
        );
    }

    /**
     * This calculates how much light is added to a ray through scattering
     *
     * @param ray Is the ray
     * @param hit Is the hit location
     * @return
     */
    public static Color3f InScatterAmount(Ray ray, HitRecord hit) {
        Vector3f A = ray.o;
        Vector3f B = hit.pos;
        float angle = 10f;// change this;

        Vector3f MieColor = new Vector3f(
                MieInScatter(A, B, Wavelength.x),
                MieInScatter(A, B, Wavelength.y),
                MieInScatter(A, B, Wavelength.z));
        MieColor = Vec.ScaleVec(MieColor, MiePhaseFunction(angle, Mie_G));

        Vector3f RayleighColor = new Vector3f(
                RayleighInScatter(A, B, Wavelength.x),
                RayleighInScatter(A, B, Wavelength.y),
                RayleighInScatter(A, B, Wavelength.z));
        RayleighColor = Vec.ScaleVec(RayleighColor, RayleighPhaseFunction(angle));

        )


    }

    private static float MieInScatter(Vector3f A, Vector3f B, float wavelength){
        return KMie *Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public float evaluate(Object[] args) {
                        Vector3f v = (Vector3f) args[0];
                        float sampleHeight = height(v);
                        return (float)Math.exp(-sampleHeight * scaleOverScaleDepth);
                    }
                },
                A, B, samplesPerOutScatterRay
        );
    }

    private static float RayleighInScatter(Vector3f A, Vector3f B, float wavelength){
        Krayleigh(wavelength)
    }

    //

}
