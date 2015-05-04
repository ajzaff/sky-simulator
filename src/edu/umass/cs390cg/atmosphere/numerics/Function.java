package edu.umass.cs390cg.atmosphere.numerics;

import javax.vecmath.Vector3d;

public interface Function {
   Vector3d evaluate(Object[] args);
}

/*
return Integrals.estimateIntegral(
  new Function() {
    @Override
    public float evaluate(Object[] args) {
      Vector3f pos = (Vector3f) args[0];


    }
  },
  new Vector3f(),
  new Vector3f(4,1,2),
  .01
);
*/