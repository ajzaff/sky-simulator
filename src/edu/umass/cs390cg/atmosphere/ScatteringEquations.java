//region
package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.geom.shapes.Sky;
import edu.umass.cs390cg.atmosphere.geom.shapes.Terrain;
import edu.umass.cs390cg.atmosphere.numerics.Function;
import edu.umass.cs390cg.atmosphere.numerics.Integrals;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import java.security.KeyPair;
import java.util.Vector;

import static edu.umass.cs390cg.atmosphere.RayTracer.*;

import static edu.umass.cs390cg.atmosphere.numerics.Vec.*;
import static java.lang.Math.*;

public class ScatteringEquations {

    //region Declarations

    public static int samplesPerInScatterRay = 10;
    public static int samplesPerOutScatterRay = 10;
    public static Sky sky;
    public static Terrain terrain;
    public static double scale; // 1 / (Outer radius - inner radius)
    public static double scaleDepth = 0.25d; // Depth of average atmospheric density, 0.25
    public static double scaleOverScaleDepth;

    public static double exposure = 2d;
    public static double Kr = 0.0025d;
    public static double Km = 0.0015d;
    public static final double Mie_G = -.8d;
    public static Vector3d Wavelength = new Vector3d(0.650d, 0.570d, 0.475d);
    /*public static Vector3d InvWavelength = new Vector3d(
            1d / Math.pow(Wavelength.x, 4),
            1d / Math.pow(Wavelength.y, 4),
            1d / Math.pow(Wavelength.z, 4));*/

    public static void Initialize(Sky sky, Terrain terrain) {
        ScatteringEquations.sky = sky;
        ScatteringEquations.terrain = terrain;
        scale = 1d / (sky.radius - terrain.radius);
        scaleOverScaleDepth = scale / scaleDepth;

        Vector3d Vec1 = new Vector3d(1,0,0);
        Vector3d Vec2 = new Vector3d(-1,0,0);

        System.out.println("Cosin " + GetVectorCos(Vec1, Vec2));
        System.out.println("Phase 1" + Phase(1, 0));
    }

    //endregion

    public static Vector3d GetSurfaceLight(Vector3d IEmitted){
        // emitted * exp( -t(Pa,Pb)) camera to ground

    }

    public static Vector3d GetLightToSurface(Vector3d Ground){
        // Is *exp(-t(Pc, Pground)
        // if shadowed return either ambient or reflected ray?

    }

    public static Vector3d GetLightRays(Ray ray, HitRecord hit){
        Vector3d A = ray.o;
        Vector3d B = hit.pos;

        Vector3d RayleighColor = new Vector3d(
                InScatter(A, B, Kr, Wavelength.x, 4d, 0),
                InScatter(A, B, Kr, Wavelength.y, 4d, 0),
                InScatter(A, B, Kr, Wavelength.z, 4d, 0));//*/
        //Vector3d RayleighColor = new Vector3d();
        Vector3d MieColor = new Vector3d(
                InScatter(A, B, Km, Wavelength.x, 0.84d, Mie_G),
                InScatter(A, B, Km, Wavelength.y, 0.84d, Mie_G),
                InScatter(A, B, Km, Wavelength.z, 0.84d, Mie_G));
        return Scale(r.scene.sun.color, Add(RayleighColor, MieColor));
    }


    private static double InScatter(Vector3d CameraPoint, Vector3d B, double KConstant, double wavelength, double KPower, double G) {
        Vector3d dir = Subtract(B, CameraPoint);
        double sampleLength = dir.length() / samplesPerInScatterRay;
        double scaledLength = sampleLength * scale;
        dir.normalize();
        dir.scale(sampleLength);

        Vector3d samplePoint = Add(CameraPoint, Scale(dir, 0.5d));

        double InscatterIntegral = 0d;
        for (int n = 0; n < samplesPerOutScatterRay; n++) {

            double density = exp(scaleOverScaleDepth * (terrain.radius - height(samplePoint, true, "InScatter")));

            Vector3d PointToSun = r.scene.intersectSky(new Ray(samplePoint, r.scene.sun.d)).pos;

            double outscatter = exp(
                    -1d * GetOutscatter(samplePoint, PointToSun, KConstant, wavelength, KPower) -
                            GetOutscatter(samplePoint, CameraPoint, KConstant, wavelength, KPower));

            InscatterIntegral += density * outscatter * scaledLength;
            samplePoint = Add(samplePoint, dir);
        }


        // cos = lightDir dot ldir
        //TODO Ensure this is correct
        Vector3d ScatterRayTowardsCamera = Subtract(CameraPoint, B);
        ScatterRayTowardsCamera.normalize();

        //V1 dot v2 when both are normalized
        double cos = GetVectorCos(ScatterRayTowardsCamera, r.scene.sun.d);
        if(!isCloseEnough(1d, r.scene.sun.d.length() * ScatterRayTowardsCamera.length(), 0.01)) //debugger
            System.out.println("Angle rays aren't normalized, is " + r.scene.sun.d.length() * ScatterRayTowardsCamera.length());
        //System.out.println("Cosine of angle is " + cos + " scat  " + ScatterRayTowardsCamera + " sun " + r.scene.sun.d);

        double Coefficients = GetK(KConstant, wavelength, KPower) * Phase(cos, G);
        return InscatterIntegral * Coefficients;
    }

    private static double OpticalDepth(Vector3d A, Vector3d B) {

        height(A, true, "OpticalA");// Debugging
        height(B, true, "OpticalB");

        Vector3d dir = Subtract(B, A);
        double sampleLength = dir.length() / samplesPerOutScatterRay;
        double scaledLength = sampleLength * scale;

        if(!isCloseEnough(scaledLength * samplesPerOutScatterRay/scale, dir.length(), 0.1d))
            System.out.println(dir.length() + " length vs segmented" + scaledLength * samplesPerOutScatterRay/scale);

        dir.normalize();
        dir.scale(sampleLength);

        Vector3d samplePoint = Add(A, Scale(dir, 0.5d));

        double value = 0d;
        for (int n = 0; n < samplesPerOutScatterRay; n++) {
            double density = exp(scaleOverScaleDepth * (terrain.radius - height(samplePoint, false, "Optical Depth")));
            value += density * scaledLength;
            samplePoint = Add(samplePoint, dir);
        }
        return value;
    }


    /**
     * Gets the vertical distance from the terrain surface
     *
     * @param pos a point in the atmosphere.
     * @return the altutide of this point [0,1) iif
     * the point is contained within the atmosphere.
     */
    public static double height(Vector3d pos, boolean checked, String from){
        double height = pos.length();
        if (height < terrain.radius - 0.8 || height > sky.radius + 0.1) {

            if(checked)
                System.out.println("From " + from + " height is" + height + " at " + pos);
        }
        return height;
    }

    public static Vector3d Phase(Vector3d A, Vector3d B, double g){
        double phase = Phase(GetVectorCos(A, B), g);
        return new Vector3d(phase, phase, phase);
    }


    private static double Phase(double cos, double g){
        double gg = g * g;

        return (3 * (1 - gg)) /
                (2 * (2 + gg)) *

                (1 + cos * cos) /
                pow(1 + gg - 2 * g * cos, 3d / 2);
    }

    private static double GetVectorCos(Vector3d A, Vector3d B){
        return A.dot(B);
    }

    private static double GetK(double KCOnstant, double wavelength, double KPower){
        return KCOnstant / (pow(wavelength, KPower));
    }


    private static double GetOutscatter(Vector3d A, Vector3d B, double KConstant, double wavelength, double KPower) {
        return 4 * PI  *GetK(KConstant, wavelength, KPower) * OpticalDepth(A, B);
    }

    public static Vector3d GetAllOutScatter(Vector3d A, Vector3d B){
        Vector3d RayleighOutScatter = new Vector3d(
                GetOutscatter(A,B, Kr, Wavelength.x, 4),
                GetOutscatter(A,B, Kr, Wavelength.y, 4),
                GetOutscatter(A,B, Kr, Wavelength.z, 4));

        Vector3d MieOutScatter = new Vector3d(
                GetOutscatter(A, B, Km, Wavelength.x, 0.84d),
                GetOutscatter(A, B, Km, Wavelength.y, 0.84d),
                GetOutscatter(A, B, Km, Wavelength.z, 0.84d));

        return Add(RayleighOutScatter, MieOutScatter);
    }

    public static Vector3d cosOfVectorsNormalized(Vector3d A, Vector3d B) {
        double value = (A.dot(B) / 2d) + 0.5d;
        return new Vector3d(value, value, value);
    }

    public static Vector3d ExposureCorrection(Vector3d color){
        // 1 - exp(-exp * color)
        return new Vector3d(
                1d - exp(color.x * -exposure),
                1d - exp(color.y * -exposure),
                1d - exp(color.z * -exposure));
    }

    public static Vector3d VecExponent(Vector3d V){
        return new Vector3d(
                exp(V.x),
                exp(V.y),
                exp(V.z));
    }

    //region OldCode
    /*
    private static double scale(double Cos) {
        double x = 1f - Cos;
        return scaleDepth * exp(-0.00287 + x * (0.459 + x * (3.83 + x * (-6.80 + x * 5.25))));
    }*/



    /*
    public static Vector3d GetInScatter(final Ray ray, HitRecord hit) {

        double rayLength = Subtract(hit.pos, ray.o).length(); // good
        double sampleLength = rayLength / samplesPerInScatterRay; // good

        Vector3d startPoint = ray.o; // good
        Vector3d endPoint = hit.pos; // good

        double cameraHeight = height(startPoint); // good
        final double cameraDepth = exp(scaleOverScaleDepth * (terrain.radius - cameraHeight));//good

        final double startAngle = ray.d.dot(startPoint) / cameraHeight; // good?
        final double startOffset = cameraDepth * scale(startAngle); // good

        //for(int i =0; i < Samples)

        Vector3d myColor = Integrals.estimateIntegral(
                new Function() {
                    @Override
                    public Vector3d evaluate(Object[] args) {

                        Vector3d samplePoint = (Vector3d) args[0];
                        double sampleHeight = height(samplePoint);
                        double sampleDepth = exp(scaleOverScaleDepth * (terrain.radius - sampleHeight));
                        double lightAngle = samplePoint.dot(r.scene.sun.d) / sampleHeight;
                        double cameraAngle = samplePoint.dot(ray.d) / sampleHeight;

                        double forwardScatter = (startOffset +
                                sampleDepth * (scale(lightAngle) - scale(cameraAngle)));


                        Vector3d lightToAttenuate =
                                Scale(
                                        Add(
                                                Scale(InvWavelength, Kr4Pi),
                                                Km4pi),
                                        -forwardScatter);

                        Vector3d addedLight = new Vector3d(
                                exp(lightToAttenuate.x),
                                exp(lightToAttenuate.y),
                                exp(lightToAttenuate.z));
                        return Scale(addedLight, sampleDepth);
                    }
                },
                startPoint, endPoint, scale, samplesPerOutScatterRay
        );

        double cos = r.scene.sun.d.dot(ray.d);
        Vector3d RayleighCoefficient = Scale(InvWavelength, KrESun * RayleighPhaseFunction(cos));
        double MieCoefficient = KmESun * MiePhaseFunction(cos, Mie_G);

        return Add(Scale(myColor, RayleighCoefficient), Scale(myColor, MieCoefficient));
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
     *//*
    public static double RayleighPhaseFunction(double cos) {
        return (3d / 4 * (1 + cos));
    }//*/

    /**
     * This calculates how much light is scattered in the
     * direction of the camera
     *
     * @param cos_theta is the angle between two rays
     * @param g         affects the symmetry of scattering
     *                  where g=0 results in symmetrical Rayleigh scattering
     *                  and -.999 < g < -.75 results in Mie aerosol scattering
     * @return
     *//*
    public static double MiePhaseFunction(double cos_theta, double g) {
        double gg = g * g;

        return (3 * (1 - gg)) / (2 * (2 + gg)) *
                (1 + cos_theta * cos_theta) /
                pow(1 + gg - 2 * g * cos_theta, 3d / 2);
    }*/

    //endregion
}
