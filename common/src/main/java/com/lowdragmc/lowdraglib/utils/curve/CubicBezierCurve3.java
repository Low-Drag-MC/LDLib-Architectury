package com.lowdragmc.lowdraglib.utils.curve;

import com.lowdragmc.lowdraglib.utils.Interpolations;
import com.lowdragmc.lowdraglib.utils.Vector3;

/**
 * @author KilaBash
 * @date 2022/6/17
 * @implNote CubicBezierCurve3
 */
public class CubicBezierCurve3 extends Curve{
    public final Vector3 v0, v1, v2, v3;

    public CubicBezierCurve3(Vector3 start, Vector3 control1, Vector3 control2, Vector3 end) {
        this.v0 = start;
        this.v1 = control1;
        this.v2 = control2;
        this.v3 = end;
    }

    @Override
    public Vector3 getPoint(float t) {
        return new Vector3(
                Interpolations.CubicBezier(t, v0.x, v1.x, v2.x, v3.x),
                Interpolations.CubicBezier(t, v0.y, v1.y, v2.y, v3.y),
                Interpolations.CubicBezier(t, v0.z, v1.z, v2.z, v3.z)
        );
    }
}
