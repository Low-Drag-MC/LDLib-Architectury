package com.lowdragmc.lowdraglib.utils.curve;

import com.lowdragmc.lowdraglib.utils.Interpolations;
import com.lowdragmc.lowdraglib.utils.Vector3;

/**
 * @author KilaBash
 * @date 2022/6/17
 * @implNote CubicBezierCurve3
 */
public class CubicBezierCurve3 extends Curve<Vector3> {
    public Vector3 p0, c0, c1, p1;

    public CubicBezierCurve3(Vector3 start, Vector3 control1, Vector3 control2, Vector3 end) {
        this.p0 = start;
        this.c0 = control1;
        this.c1 = control2;
        this.p1 = end;
    }

    @Override
    public Vector3 getPoint(float t) {
        return new Vector3(
                Interpolations.CubicBezier(t, p0.x, c0.x, c1.x, p1.x),
                Interpolations.CubicBezier(t, p0.y, c0.y, c1.y, p1.y),
                Interpolations.CubicBezier(t, p0.z, c0.z, c1.z, p1.z)
        );
    }
}
