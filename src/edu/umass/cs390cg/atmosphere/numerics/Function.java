package edu.umass.cs390cg.atmosphere.numerics;

import javax.vecmath.Vector3d;

public interface Function {
   Vector3d evaluate(Object[] args);
}