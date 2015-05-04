package edu.umass.cs390cg.atmosphere.geom.shapes;

import edu.umass.cs390cg.atmosphere.ScatteringEquations;
import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;

import javax.vecmath.Vector3d;

import edu.umass.cs390cg.atmosphere.ScatteringEquations.*;

import static edu.umass.cs390cg.atmosphere.RayTracer.*;
import static edu.umass.cs390cg.atmosphere.ScatteringEquations.*;
import static edu.umass.cs390cg.atmosphere.numerics.Vec.*;

public class Terrain extends Sphere {
    public Vector3d color;

    public Vector3d calculateShading(Ray ray, HitRecord hit) {

        //return ScatteringEquations.GetLightBeforeHittingSurface(hit);//todo change this

        if (ScatteringEquations.Debug) {
            //double depth = OpticalDepth(ray.o, hit.pos);
            //double depth = GetLinearDepth(ray.o, hit.pos);
            //ScatteringEquations.Update(depth);
            //return Vec.ColorNormalize(new Vector3d(depth, depth, depth), 0, 7);
            //Vector3d lightDir = Negate(r.scene.sun.d);

            //Vector3d lightDir = Subtract(hit.pos, new Vector3d(4, 203d, 4));
            //   double distance = 1d/Math.pow(lightDir.length(), 4);
            //lightDir.normalize();

            //return Shade(Scale(r.scene.sun.color, 1d/50), lightDir, Negate(ray.d), hit);
            return GetEmittedLight(ray, hit);

            //return GetLightRays(ray, hit);
        } else {
            return GetLightFromSurface(ray, hit);
            //return color;
            //return Add(GetLightRays(ray, hit), GetLightFromSuface(ray, hit));
        }

    }
}