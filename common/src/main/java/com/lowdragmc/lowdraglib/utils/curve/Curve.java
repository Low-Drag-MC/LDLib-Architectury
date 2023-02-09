package com.lowdragmc.lowdraglib.utils.curve;

import com.lowdragmc.lowdraglib.utils.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/6/16
 * @implNote Curve
 */
public abstract class Curve {

    public abstract Vector3 getPoint(float t);

    public List<Vector3> getPoints(int size) {
        List<Vector3> points = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            points.add(getPoint(i * 1f / size));
        }
        return points;
    }

}
