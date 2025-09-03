package team.dovecot.ccb.common.math;

public class Vec2d {
    private double x;
    private double y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vec2d add(Vec2d vec2d) {
        return add(vec2d.x, vec2d.y);
    }

    public Vec2d add(double x, double y) {
        this.x += x;
        this.y += y;

        return this;
    }

    public Vec2d subtract(Vec2d vec2d) {
        return subtract(vec2d.x, vec2d.y);
    }

    public Vec2d subtract(double x, double y) {
        this.x -= x;
        this.y -= y;

        return this;
    }

    public Vec2d multiply(Vec2d vec2d) {
        return multiply(vec2d.x, vec2d.y);
    }

    public Vec2d multiply(double x, double y) {
        this.x *= x;
        this.y *= y;

        return this;
    }

    public Vec2d divide(Vec2d vec2d) {
        return add(vec2d.x, vec2d.y);
    }

    public Vec2d divide(double x, double y) {
        this.x /= x;
        this.y /= y;

        return this;
    }
}
