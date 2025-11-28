package ch.epfl.cs107.play.math;

import java.awt.geom.AffineTransform;
import java.io.Serializable;

/**
 * Represents an immutable 2D affine transformation.
 */
public final class Transform implements Serializable {
    
	private static final long serialVersionUID = 1;
    /** The identity transform **/
    public static final Transform I = new Transform(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    
    /** X scale */
    public final double m00;
    
    /** X shear */
    public final double m01;
    
    /** X translation */
    public final double m02;
    
    /** Y shear */
    public final double m10;
    
    /** Y scale */
    public final double m11;
    
    /** Y translation */
    public final double m12;

    /**
     * Creates a new transform.
     * @param m00 (double): X scale
     * @param m01 (double): X shear
     * @param m02 (double): X translation
     * @param m10 (double): Y shear
     * @param m11 (double): Y scale
     * @param m12 (double): Y translate
     */
    public Transform(double m00, double m01, double m02, double m10, double m11, double m12) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
    }
    
    // TODO is rigid/scale/translation/...?
    
    /** @return (Vector): X-axis, not null */
    public Vector getX() {
        return new Vector(m00, m10);
    }
    
    /** @return (Vector): Y-axis, not null */
    public Vector getY() {
        return new Vector(m01, m11);
    }
    
    /** @return (Vector): translation vector, not null */
    public Vector getOrigin() {
        return new Vector(m02, m12);
    }
    
    /** @return (double): angle, in radians */
    public double getAngle() {
        return (double)Math.atan2(m01, m00);
    }
    
    /**
     * Transforms point.
     * @param x (double): abcissa
     * @param y (double): ordinate
     * @return (Vector): transformed point, not null
     */
    public Vector onPoint(double x, double y) {
        return new Vector(
            x * m00 + y * m01 + m02,
            x * m10 + y * m11 + m12
        );
    }
    
    /**
     * Transforms point.
     * @param p (Vector): point, not null
     * @return (Vector): transformed point, not null
     */
    public Vector onPoint(Vector p) {
        return onPoint(p.x, p.y);
    }
    
    /**
     * Transforms vector.
     * @param x (double): abcissa
     * @param y (double): ordinate
     * @return (Vector): transformed vector, not null
     */
    public Vector onVector(double x, double y) {
        return new Vector(
            x * m00 + y * m01,
            x * m10 + y * m11
        );
    }
    
    /**
     * Transforms vector.
     * @param v (Vector): point, not null
     * @return (Vector): transformed vector, not null
     */
    public Vector onVector(Vector v) {
        return onVector(v.x, v.y);
    }
    
    /**
     * Appends another transform (applied after this transform).
     * @param t (Transform): transform, not null
     * @return (Transform): extended transform, not null
     */
    public Transform transformed(Transform t) {
        return new Transform(
            t.m00 * m00 + t.m01 * m10, t.m00 * m01 + t.m01 * m11, t.m00 * m02 + t.m01 * m12 + t.m02,
            t.m10 * m00 + t.m11 * m10, t.m10 * m01 + t.m11 * m11, t.m10 * m02 + t.m11 * m12 + t.m12
        );
    }
    
    /**
     * Appends translation (applied after this transform).
     * @param dx (double): X translation
     * @param dy (double): Y translation
     * @return (Transform): extended transform, not null
     */
    public Transform translated(double dx, double dy) {
        return new Transform(
            m00, m01, m02 + dx,
            m10, m11, m12 + dy
        );
    }
    
    /**
     * Appends translation (applied after this transform).
     * @param d (Vector): translation, not null
     * @return (Transform): extended transform, not null
     */
    public Transform translated(Vector d) {
        return translated(d.x, d.y);
    }
    
    /**
     * Appends scale (applied after this transform).
     * @param sx (double) X scale
     * @param sy (double) Y scale
     * @return (Transform): extended transform, not null
     */
    public Transform scaled(double sx, double sy) {
        return new Transform(
            m00 * sx, m01 * sx, m02 * sx,
            m10 * sy, m11 * sy, m12 * sy
        );
    }
    
    /**
     * Appends scale (applied after this transform).
     * @param s (double): scale
     * @return (Transform): extended transform, not null
     */
    public Transform scaled(double s) {
        return scaled(s, s);
    }
    
    // TODO scale in specific direction, according to vector?
    // TODO scale using specific center of transformation?
    
    /**
     * Appends rotation around origin (applied after this transform).
     * @param a (double): angle, in radians
     * @return (Transform): extended transform, not null
     */
    public Transform rotated(double a) {
        double c = (double)Math.cos(a);
        double s = (double)Math.sin(a);
        return new Transform(
            c * m00 - s * m10, c * m01 - s * m11, c * m02 - s * m12,
            s * m00 + c * m10, s * m01 + c * m11, s * m02 + c * m12
        );
    }
    
    /**
     * Appends rotation around specified point (applied after this transform).
     * @param a (double): angle, in radians
     * @param center (Vector): rotation axis, not null
     * @return (Transform): extended transform, not null
     */
    public Transform rotated(double a, Vector center) {
        return
            translated(-center.x, -center.y).
            rotated(a).
            translated(center);
    }
    
    // TODO 90, 180, 270 degrees rotation?
    
    // TODO flip h/v, mirror
    
    /** @return (Transform): transform inverse, not null */
    public Transform inverted() {
        double det = 1.0f / (m00 * m11 - m01 * m10);
        double a = m11 * det;
        double b = -m01 * det;
        double c = -m10 * det;
        double d = m00 * det;
        return new Transform(
            a, b, -(a * m02 + b * m12),
            c, d, -(c * m02 + d * m12)
        );
    }

    @Override
    public int hashCode() {
        return
            Double.hashCode(m00) ^ Double.hashCode(m01) ^ Double.hashCode(m02) ^ 
            Double.hashCode(m10) ^ Double.hashCode(m11) ^ Double.hashCode(m12);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Transform))
            return false;
        Transform other = (Transform)object;
        return
            m00 == other.m00 && m01 == other.m01 && m02 == other.m02 &&
            m10 == other.m10 && m11 == other.m11 && m12 == other.m12;
    }
    
    @Override
    public String toString() {
        return String.format("[%f, %f, %f, %f, %f, %f]", m00, m01, m02, m10, m11, m12);
    }
    
    /** @return (AffineTransform): AWT affine transform equivalent, not null */
    public AffineTransform getAffineTransform() {
        return new AffineTransform(
            m00, m10,
            m01, m11,
            m02, m12 
        );
    }
    
}
