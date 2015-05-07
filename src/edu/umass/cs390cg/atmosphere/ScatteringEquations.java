//region
package edu.umass.cs390cg.atmosphere;

import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.geom.shapes.Sky;
import edu.umass.cs390cg.atmosphere.geom.shapes.Terrain;

import javax.vecmath.Vector3d;

import static edu.umass.cs390cg.atmosphere.RayTracer.*;

import static edu.umass.cs390cg.atmosphere.numerics.Vec.*;
import static java.lang.Math.*;

public class ScatteringEquations {

    //region Declarations
    public static Sky sky;
    public static Terrain terrain;
    public static double scale; // 1 / (Outer radius - inner radius)
    public static double scaleDepth = 0.25d; // Depth of average atmospheric density, 0.25
    public static double scaleOverScaleDepth;

    public static boolean Debug = false;
    // If false displa
    private static boolean RenderSimpleSky = false;
    // If false does not account for light reflected off ground
    private static boolean RenderGroundLight = true;
    // Renders shadow ground as dark red/brown
    private static boolean RenderSimpleShadowedGround = false;
    // Renders lit ground as green
    private static boolean RenderSimpleLitGround = false;

    // The this scales the atmospheric light when looking at the ground
    // When set to zero only the reflected ground light is shown.
    // If set very high the ground is drowned
    public static double lscale = 1;

    // How many times the ray trace can bounce
    public static int MaxDepth = 4;

    public static double Kr = 0.0025d;
    public static double Km = 0.0015d;
    public static final double Mie_G = -.8d;//0.650d, 0.570d, 0.475d);
    public static Vector3d Wavelength = new Vector3d(0.650d, 0.570d, 0.475d);
    public static Vector3d AmbientColor = new Vector3d(0.1d, 0.1d, 0.1d);

    public static int samplesPerInScatterRay = 10;
    public static int samplesPerOutScatterRay = 20;

    //How many antialiasing samples are taken
    public static int AAsamples = 3;
    public static double exposure = 2;

    public static String UpdateName = "Depth";
    public static double LargestVal = Double.MIN_VALUE;
    public static double SmallestVal = Double.MAX_VALUE;

    // This is used to find the min and max values for a value, used to normalize it on screen
    // For instance, if tracing optical depth this can normalize it between 0-1
    public static void Update(double val) {
        if (val > LargestVal) {
            LargestVal = val;
            //System.out.println("largest val " + val);
        }
        if (val < SmallestVal) {
            SmallestVal = val;
            //System.out.println("smallest val " + val);
        }
    }

    public static void Initialize(Sky sky, Terrain terrain) {
        ScatteringEquations.sky = sky;
        ScatteringEquations.terrain = terrain;
        scale = 1d / (sky.radius - terrain.radius);
        scaleOverScaleDepth = scale / scaleDepth;
    }

    //endregion

    //region Surface Functions

    public static Vector3d GetRayColor(Ray ray, int depth) {
        Update(depth);
        if (depth >= MaxDepth)
            return AmbientColor;

        HitRecord hit = r.scene.intersectScene(ray);
        if (hit == null) {
            System.out.println("Error, Null hit in RayColor with Ray " + ray);
            return new Vector3d();
        } else if (hit.type == HitRecord.HitType.TYPE_SKY) {
            //return new Vector3d(1,0,0);
            //double asdf = ScatteringEquations.OpticalDepth(ray.o, hit.pos);
            //return ColorNormalize(new Vector3d(depth, depth, depth), 0, 7);

            if(!RenderSimpleSky)
                return ScatteringEquations.GetLightRays(ray, hit);
            else
                return new Vector3d(0,0,1);
        } else {
            //TODO potentially add more hit types
            // Iv + Ie*out

            Vector3d lightScale = new Vector3d(lscale, lscale, lscale);
            Vector3d atmosLight = new Vector3d();
            if(lscale != 0)
                atmosLight = Scale(ScatteringEquations.GetLightRays(ray, hit), lightScale);

            if(RenderGroundLight){
                return Add(atmosLight,
                        GetLightFromSurface(ray, hit, depth));
            }
            else
                return atmosLight;
        }
    }

    public static Vector3d GetLightFromSurface(Ray ray, HitRecord hit, int depth) {
        Vector3d A = ray.o;
        Vector3d B = hit.pos;

        // exp(-t(Camera, ground))
        Vector3d outScatterToCamera = VecExponent(Scale(GetAllOutScatter(A, B), -1));

        // Get the light before it hits the material, hit the material, then reflect towards camera
        Vector3d IEmitted = GetEmittedLight(ray, hit, depth);

        // Ie * outscattering
        return Scale(IEmitted, outScatterToCamera);
    }


    public static Vector3d GetEmittedLight(Ray ray, HitRecord hit, int depth) {
        // If point is in sunlight, return Is * attenuation
        //else return ambient

        Ray RayToSun = new Ray(hit.pos, r.scene.sun.d);
        HitRecord SunHit = r.scene.intersectScene(RayToSun);
        Vector3d SunPoint = SunHit.pos;

        Vector3d lightFromSun = new Vector3d();
        if(!(RenderSimpleLitGround && RenderSimpleShadowedGround)){
            lightFromSun = GetLightRays(RayToSun, SunHit);
            lightFromSun = r.scene.sun.color;
        }

        // Reflect attenuated sunlight, Is * outscatter
        if (SunHit.type == HitRecord.HitType.TYPE_SKY) {
            // exp( -t(sun, ground))
            // Isun * outScattering

            if(!RenderSimpleLitGround){
                Vector3d outscatterCoefficient = VecExponent(Scale(GetAllOutScatter(hit.pos, SunPoint), -1));
                lightFromSun = Scale(lightFromSun, outscatterCoefficient);
                return Shade(lightFromSun, Negate(r.scene.sun.d), Negate(ray.d), hit);
            }
            else
                return new Vector3d(0,4,0);
        }
        // Else reflect the ray off the material and return atmospheric scattering
        else { // If it's shadowed

            if(!RenderSimpleShadowedGround){
                Vector3d outscatterCoefficient = VecExponent(Scale(GetAllOutScatter(hit.pos, SunPoint), -1));
                lightFromSun = Scale(lightFromSun, outscatterCoefficient);
                double cos = GetVectorCos(r.scene.sun.d, hit.normal);
                lightFromSun = Scale(lightFromSun, cos);
                return Shade(lightFromSun, Negate(r.scene.sun.d), Negate(ray.d), hit);
            }
            else
                return new Vector3d(0.16, 0.04, 0.04);
        }
    }


    public static Vector3d Shade(Vector3d lightIntensity, Vector3d LightToSurface, Vector3d SurfaceToEye, HitRecord hit) {
        Vector3d color = Scale(AmbientColor, hit.material.Ka);

        color = Scale(color, hit.material.GetNoise());

        //return color;

        // Add spec light
        if (VectorIsNonZero(hit.material.Ks)) {
            // Points towards the viewing source

            // Reflect expects dir to point to the source, reflects away
            Vector3d R = Reflect(LightToSurface, hit.normal);
            // BSpec = Intens * Ks ( max(O, R * V))^P
            double specAmt = Math.max(0, SurfaceToEye.dot(R));
            if (hit.material.phong_exp != 1) {
                specAmt = Math.pow(specAmt, hit.material.phong_exp);
            }
            if (specAmt != 0) {
                color = Add(color, Scale(Scale(lightIntensity, hit.material.Ks), specAmt));
            }
        }
        // Add diffuse light, if available
        if (VectorIsNonZero(hit.material.Kd)) {
            // BDiff = Intens * Kd * max(N * L, 0)

            double diffuseAmt = Math.max(0, hit.normal.dot(Negate(LightToSurface)));
            color = Add(color, Scale(Scale(lightIntensity, hit.material.Kd), diffuseAmt));
        }
        return color;
    }

    public static Vector3d GetLightRays(Ray ray, HitRecord hit) {
        Vector3d A = ray.o;
        Vector3d B = hit.pos;

        if(hit == null)
            System.out.println("GetLightRays hit is null");

        if(A == null)
            System.out.println("GetLightRays A is null");
        if(A == null)
            System.out.println("GetLightRays B is null");

        Vector3d RayleighColor = new Vector3d(
                InScatter(A, B, Kr, Wavelength.x, 4d, 0),
                InScatter(A, B, Kr, Wavelength.y, 4d, 0),
                InScatter(A, B, Kr, Wavelength.z, 4d, 0));
        Vector3d MieColor = new Vector3d(
                InScatter(A, B, Km, Wavelength.x, 0.84d, Mie_G),
                InScatter(A, B, Km, Wavelength.y, 0.84d, Mie_G),
                InScatter(A, B, Km, Wavelength.z, 0.84d, Mie_G));
        return Scale(r.scene.sun.color, Add(RayleighColor, MieColor));
    }

    //endregion

    //region Scatter Functions

    private static double InScatter(Vector3d CameraPoint, Vector3d B, double KConstant, double wavelength, double KPower, double G) {
        Vector3d dir = Subtract(B, CameraPoint);
        double sampleLength = dir.length() / samplesPerInScatterRay;
        double scaledLength = sampleLength * scale;
        dir.normalize();
        dir.scale(sampleLength);

        Vector3d samplePoint = Add(CameraPoint, Scale(dir, 0.5d));

        double InscatterIntegral = 0d;
        for (int n = 0; n < samplesPerOutScatterRay; n++) {

            double pointHeight = height(samplePoint, false, "InScatter");
            if(pointHeight >= sky.radius || pointHeight <= terrain.radius)
                continue;

            double density = exp(scaleOverScaleDepth * (terrain.radius - pointHeight));
            HitRecord SunHit = r.scene.intersectSky(new Ray(samplePoint, r.scene.sun.d));
            Vector3d PointToSun = SunHit.pos;

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
        if (!isCloseEnough(1d, r.scene.sun.d.length() * ScatterRayTowardsCamera.length(), 0.01)) //debugger
            System.out.println("Angle rays aren't normalized, is " + r.scene.sun.d.length() * ScatterRayTowardsCamera.length());
        //System.out.println("Cosine of angle is " + cos + " scat  " + ScatterRayTowardsCamera + " sun " + r.scene.sun.d);

        double Coefficients = GetK(KConstant, wavelength, KPower) * Phase(cos, G);
        return InscatterIntegral * Coefficients;
    }

    public static Vector3d GetAllOutScatter(Vector3d A, Vector3d B) {
        Vector3d RayleighOutScatter = new Vector3d(
                GetOutscatter(A, B, Kr, Wavelength.x, 4),
                GetOutscatter(A, B, Kr, Wavelength.y, 4),
                GetOutscatter(A, B, Kr, Wavelength.z, 4));

        Vector3d MieOutScatter = new Vector3d(
                GetOutscatter(A, B, Km, Wavelength.x, 0.84d),
                GetOutscatter(A, B, Km, Wavelength.y, 0.84d),
                GetOutscatter(A, B, Km, Wavelength.z, 0.84d));

        return Add(RayleighOutScatter, MieOutScatter);
    }

    private static double GetOutscatter(Vector3d A, Vector3d B, double KConstant, double wavelength, double KPower) {
        return 4 * PI * GetK(KConstant, wavelength, KPower) * OpticalDepth(A, B);
    }

    //endregion

    //region Helper Functions
    public static double GetLinearDepth(Vector3d A, Vector3d B) {
        return Subtract(A, B).length();
    }

    public static double OpticalDepth(Vector3d A, Vector3d B) {

        height(A, true, "OpticalA");// Debugging
        height(B, true, "OpticalB");

        //Ray from A to B
        Vector3d dir = Subtract(B, A);
        double sampleLength = dir.length() / samplesPerOutScatterRay;
        double scaledLength = sampleLength * scale;

        if (!isCloseEnough(scaledLength * samplesPerOutScatterRay / scale, dir.length(), 0.1d))
            System.out.println(dir.length() + " length vs segmented" + scaledLength * samplesPerOutScatterRay / scale);

        dir.normalize();
        dir.scale(sampleLength);

        Vector3d samplePoint = Add(A, Scale(dir, 0.5d));

        double value = 0d;
        for (int n = 0; n < samplesPerOutScatterRay; n++) {

            //todo this still generates points inside the earth. Is this a problem?
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
    public static double height(Vector3d pos, boolean checked, String from) {
        double height = pos.length();
        if (height < terrain.radius - 0.8 || height > sky.radius + 0.1) {

            if (checked)
                System.out.println("From " + from + " height is " + height + " at " + pos);
        }
        return height;
    }

    // Okayed for Rayleigh
    public static Vector3d Phase(Vector3d A, Vector3d B, double g) {
        double phase = Phase(GetVectorCos(A, B), g);
        return new Vector3d(phase, phase, phase);
    }


    private static double Phase(double cos, double g) {
        double gg = g * g;

        return (3 * (1 - gg)) /
                (2 * (2 + gg)) *

                (1 + cos * cos) /
                pow(1 + gg - 2 * g * cos, 3d / 2);
    }


    private static double GetK(double KCOnstant, double wavelength, double KPower) {
        return KCOnstant / (pow(wavelength, KPower));
    }

    public static Vector3d ExposureCorrection(Vector3d color) {
        // 1 - exp(-exp * color)
        return new Vector3d(
                1d - exp(color.x * -exposure),
                1d - exp(color.y * -exposure),
                1d - exp(color.z * -exposure));
    }

    //endregion

}
