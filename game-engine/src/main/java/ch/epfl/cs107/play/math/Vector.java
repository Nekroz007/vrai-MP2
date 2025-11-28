package ch.epfl.cs107.play.math;

import java.io.Serializable;

/**
 * Represents an immutable 2D doubleing-point vector.
 */
public final class Vector implements Serializable {

	private static final long serialVersionUID = 1;
    /** Small value for double precision in vector comparison */
    public static final double EPSILON = 10E-6;

    /** The zero vector (0, 0) */
    public static final Vector ZERO = new Vector(0.0, 0.0);
    /** The unit X vector (1, 0) */
    public static final Vector X = new Vector(1.0, 0.0);
    /** The unit Y vector (0, 1) */
    public static final Vector Y = new Vector(0.0, 1.0);

    public final double x;
    public final double y;
    
    /**
     * Creates a new vector.
     * @param x (double): abscissa
     * @param y (double): ordinate
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /** @return (double): abscissa */
    public double getX() {
        return x;
    }

    /** @return (double): ordinate */
    public double getY() {
        return y;
    }
    
    /** @return (double): euclidian length */
    public double getLength() {
        return (double)Math.sqrt(x * x + y * y);
    }
    
    /** @return (double): angle in standard trigonometrical system, in radians */
    public double getAngle() {
        return (double)Math.atan2(y, x);
    }
    
    /** @return (Vector): negated vector */
    public Vector opposite() {
        return new Vector(-x, -y);
    }
    
    /**
     * @param other (Vector): right-hand operand, not null
     * @return (Vector): sum, not null
     */
    public Vector add(Vector other) {
        return new Vector(x + other.x, y + other.y);
    }
    
    /**
     * @param x (double): right-hand abcissa
     * @param y (double): right-hand ordinate
     * @return (Vector): sum, not null
     */
    public Vector add(double x, double y) {
        return new Vector(this.x + x, this.y + y);
    }
    
    /**
     * @param other (Vector): right-hand operand, not null
     * @return (Vector): difference, not null
     */
    public Vector sub(Vector other) {
        return new Vector(x - other.x, y - other.y);
    }
    
    /**
     * @param x (double): right-hand abcissa
     * @param y (double): right-hand ordinate
     * @return (Vector): difference, not null
     */
    public Vector sub(double x, double y) {
        return new Vector(this.x - x, this.y - y);
    }

    /**
     * @param other (Vector): right-hand operand, not null
     * @return (Vector): component-wise multiplication, not null
     */
    public Vector mul(Vector other) {
        return new Vector(x * other.x, y * other.y);
    }

    /**
     * @param x (double): right-hand abcissa
     * @param y (double): right-hand ordinate
     * @return (Vector): component-wise multiplication, not null
     */
    public Vector mul(double x, double y) {
        return new Vector(this.x * x, this.y * y);
    }
    
    /**
     * @param s (double): right-hand operand
     * @return (Vector): scaled vector, not null
     */
    public Vector mul(double s) {
        return new Vector(this.x * s, this.y * s);
    }
    
    /**
     * @param other (Vector): right-hand operand, not null
     * @return (Vector): component-wise division, not null
     */
    public Vector div(Vector other) {
        return new Vector(x / other.x, y / other.y);
    }

    /**
     * @param x (double): right-hand abcissa
     * @param y (double): right-hand ordinate
     * @return (Vector): component-wise division, not null
     */
    public Vector div(double x, double y) {
        return new Vector(this.x / x, this.y / y);
    }
    
    /**
     * @param s (double): right-hand operand
     * @return  (Vector):scaled vector, not null
     */
    public Vector div(double s) {
        return new Vector(this.x / s, this.y / s);
    }
    
    /**
     * @param other (Vector): right-hand operand, not null
     * @return (Vector): dot product
     */
    public double dot(Vector other) {
        return x * other.x + y * other.y;
    }
    
    /**
     * @param other (Vector): right-hand operand, not null
     * @return (Vector): component-wise minimum, not null
     */
    public Vector min(Vector other) {
        return new Vector(Math.min(x, other.x), Math.min(y, other.y));
    }
    
    /** @return (double): smallest component */
    public double min() {
        return Math.min(x, y);
    }
    
    /**
     * @param other (Vector): right-hand operand, not null
     * @return (Vector): component-wise maximum, not null
     */
    public Vector max(Vector other) {
        return new Vector(Math.max(x, other.x), Math.max(y, other.y));
    }
    
    /** @return (double): largest component */
    public double max() {
        return Math.max(x, y);
    }

    /**
     * Computes unit vector of same direction, or (1, 0) if zero.
     * @return (Vector): rescaled vector, not null
     */
    public Vector normalized() {
        double length = getLength();
        if (length > 1e-6)
            return div(length);
        return Vector.X;
    }
    
    /**
     * Resizes vector to specified length, or (<code>length</code>, 0) if zero.
     * @param length (double): new length
     * @return (Vector): rescaled vector, not null
     */
    public Vector resized(double length) {
        return normalized().mul(length);
    }
    
    /**
     * Computes mirrored vector, with respect to specified normal.
     * @param normal (Vector): vector perpendicular to the symmetry plane, not null
     * @return (Vector): rotated vector, not null
     */
    public Vector mirrored(Vector normal) {
        normal = normal.normalized();
        return sub(normal.mul(2.0f * dot(normal)));
    }
	
    /**
     * Computes rotated vector, in a counter-clockwise manner.
     * @param angle (double): rotation, in radians
     * @return (Vector): rotated vector, not null
     */
	public Vector rotated(double angle) {
        double c = (double)Math.cos(angle);
        double s = (double)Math.sin(angle);
        return new Vector(x * c - y * s, x * s + y * c);
    }
    
    /** @return (Vector): vector rotated by -90°, not null */
    public Vector clockwise() {
        return new Vector(-y, x);
    }
    
    /** @return (Vector): vector rotated by 90°, not null */
    public Vector counterClockwise() {
        return new Vector(y, -x);
    }

    /** @return (Vector): a rounded vector (x and y rounded to the closest int) */
    public Vector round() {
        return new Vector(Math.round(x), Math.round(y));
    }


    /**
     * Computes linear interpolation between two vectors.
     * @param other (Vector): second vector, not null
     * @param factor (double) weight of the second vector
     * @return (Vector): interpolated vector, not null
     */
    public Vector mixed(Vector other, double factor) {
        return new Vector(x * (1.0f - factor) + other.x * factor, y * (1.0f - factor) + other.y * factor);
    }


    /// Vector implements Serializable

    @Override
    public int hashCode() {
        return Double.hashCode(x) ^ Double.hashCode(y);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Vector))
            return false;
        Vector other = (Vector)object;
        return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
    
}
