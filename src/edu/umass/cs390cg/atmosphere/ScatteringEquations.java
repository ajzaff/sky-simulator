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
import javax.vecmath.Vector3f;

import static edu.umass.cs390cg.atmosphere.RayTracer.r;
import static edu.umass.cs390cg.atmosphere.numerics.Vec.*;
import static java.lang.Math.*;

public class ScatteringEquations {

    //region Declarations

    public static int samplesPerInScatterRay = 10;
    public static int samplesPerOutScatterRay = 50;
    public static Sky sky;
    public static Terrain terrain;
    public static float scale; // 1 / (Outer radius - inner radius)
    public static float scaleDepth = 0.25f; // Depth of average atmospheric density, 0.25
    public static float scaleOverScaleDepth = scale / scaleDepth;

    public static final float Mie_G = -.8f;
    public static float KMie = 0.0015f;
    public static Color3f Wavelength = new Color3f(0.650f, 0.570f, 0.475f);
    public static Color3f InvWavelength = new Color3f(
            Wavelength.x * Wavelength.x * Wavelength.x * Wavelength.x,
            Wavelength.y * Wavelength.y * Wavelength.y * Wavelength.y,
            Wavelength.z * Wavelength.z * Wavelength.z * Wavelength.z);

    //endregion

    //region K(wavelength)

    /**
     * The scattering constant function for a given wavelength.
     *
     * @param w a wavelength of light.
     * @return the scattering amount.
     */
    public static float KRayleigh(float w) {
        if (w == Wavelength.x)
            return InvWavelength.x;
        if (w == Wavelength.y)
            return InvWavelength.y;
        if (w == Wavelength.z)
            return InvWavelength.z;

        System.out.println("Wavelength error");
        return 1f;
    }

    public static float Kmie(float w) {
        return KMie;
    }

    //endregion

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
    public static float RayleighPhaseFunction(float theta) {
        return (float) (3f / 4 * (1 + cos(theta)));
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
    public static float MiePhaseFunction(float theta, float g) {
        float cos_theta, gg;

        cos_theta = (float) cos(theta);
        gg = g * g;

        return (3 * (1 - gg)) / (2 * (2 + gg)) *
                (1 + cos_theta * cos_theta) /
                (float) pow(1 + gg - 2 * g * cos_theta, 3f / 2);
    }
    //endregion

    //region Outscatter from P1 to P2 with wavelength

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

        return 4 * pi * KRayleigh(wavelength) * OpticalDepth(P_A, P_B);
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

        return 4 * pi * Kmie(wavelength) * OpticalDepth(P_A, P_B);
    }
    //endregion

    //region InScatter (Ray, hit)

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
        float angle = (float) acos(A.dot(B) / (A.length() * B.length()));

        Color3f MieColor = new Color3f(
                MieInScatter(A, B, Wavelength.x, angle),
                MieInScatter(A, B, Wavelength.y, angle),
                MieInScatter(A, B, Wavelength.z, angle));

        Color3f RayleighColor = new Color3f(
                RayleighInScatter(A, B, Wavelength.x, angle),
                RayleighInScatter(A, B, Wavelength.y, angle),
                RayleighInScatter(A, B, Wavelength.z, angle));

        return Multiply(
                AddColors(MieColor, RayleighColor),
                r.scene.sun.color);

    }

    private static float MieInScatter(Vector3f A, Vector3f B, float wavelength, float theta) {
        return MiePhaseFunction(theta, Mie_G) * KMie * Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public float evaluate(Object[] args) {
                        // Point n along the camera vector
                        Vector3f P_n = (Vector3f) args[0];

                        // The camera position
                        Vector3f camera = r.scene.camera.center;

                        // Ray from a point along the ray to the sun
                        Ray sunRay = new Ray(P_n, r.scene.sun.d);
                        Vector3f sun = r.scene.intersectSky(sunRay).pos;

                        float opticalDepth = (float) exp(-height(P_n) * scaleOverScaleDepth);
                        float outScatter = (float)exp(-OpticalDepth(P_n, sun) -OpticalDepth(P_n, camera));
                        return opticalDepth * outScatter;
                    }
                },
                A, B, samplesPerInScatterRay
        );
    }

    private static float RayleighInScatter(Vector3f A, Vector3f B, float wavelength, float theta) {
        return RayleighPhaseFunction(theta) * KRayleigh(wavelength) * Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public float evaluate(Object[] args) {
                        // Point n along the camera vector
                        Vector3f P_n = (Vector3f) args[0];

                        // The camera position
                        Vector3f camera = r.scene.camera.center;

                        // Ray from a point along the ray to the sun
                        Ray sunRay = new Ray(P_n, r.scene.sun.d);
                        Vector3f sun = r.scene.intersectSky(sunRay).pos;

                        float opticalDepth = (float) exp(-height(P_n) * scaleOverScaleDepth);
                        float outScatter = (float)exp(-OpticalDepth(P_n, sun) -OpticalDepth(P_n, camera));
                        return opticalDepth * outScatter;
                    }
                },
                A, B, samplesPerInScatterRay
        );
    }
    //endregion

    private static float OpticalDepth(Vector3f A, Vector3f B) {
        return Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public float evaluate(Object[] args) {
                        Vector3f v = (Vector3f) args[0];
                        float sampleHeight = height(v);
                        return (float) exp(-sampleHeight * scaleOverScaleDepth);
                    }
                },
                A, B, samplesPerOutScatterRay
        );
    }


    /**
     * Gets the altitude of a point in the atmosphere.
     *
     * @param pos a point in the atmosphere.
     * @return the altutide of this point [0,1) iif
     * the point is contained within the atmosphere.
     */
    public static float height(Vector3f pos) {
        return Subtract(pos, terrain.center).length() - terrain.radius;
    }
}
