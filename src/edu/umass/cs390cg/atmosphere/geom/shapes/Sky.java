package edu.umass.cs390cg.atmosphere.geom.shapes;

import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogParser;
import edu.umass.cs390cg.atmosphere.ScatteringEquations;
import edu.umass.cs390cg.atmosphere.geom.HitRecord;
import edu.umass.cs390cg.atmosphere.geom.Ray;
import edu.umass.cs390cg.atmosphere.numerics.Vec;

import javax.vecmath.Vector3d;

public class Sky extends Sphere {
    public Vector3d color;
    public static double lowestHeight = 10000d;

    double LargestValue = -2000000d;

    public Vector3d calculateShading(Ray ray, HitRecord hit) {

        if (ScatteringEquations.Debug) {
            //double depth = ScatteringEquations.OpticalDepth(ray.o, hit.pos);
            double depth = ScatteringEquations.GetLinearDepth(ray.o, hit.pos);
            ScatteringEquations.Update(depth);
            //return Vec.ColorNormalize(new Vector3d(depth, depth, depth), 0, 7);

            return ScatteringEquations.GetLightRays(ray, hit);
        } else {

            return ScatteringEquations.GetLightRays(ray, hit);
        }

    }
}