import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

import java.awt.*;


public class Ball {

    //球的横纵坐标
    private double pos_x;
    private double pos_y;
    //球的横纵方向上面的速度
    private double vec_x;
    private double vec_y;
    //球的半径
    private final double radius;
    //球的质量
    private final double mass;
    //边界长度
    private final double border;
    //球的颜色
    private final Color color;
    //碰撞次数
    private int cnt = 0;
    //限定一个最大值
    private static final double INFINITY = Double.POSITIVE_INFINITY;

    public Ball () {
        pos_x     = StdRandom.uniform(0.0, 1.0);
        pos_y     = StdRandom.uniform(0.0, 1.0);
        vec_x     = StdRandom.uniform(-0.005, 0.005);
        vec_y     = StdRandom.uniform(-0.005, 0.005);
        radius    = 0.01;
        mass      = 0.5;
        border    = 1;
        color     = Color.BLACK;
    }

    public Ball (double px,double py,double vx,double vy,double radius,double mass,double border,Color color) {
        this.pos_x = px;
        this.pos_y = py;
        this.vec_x = vx;
        this.vec_y = vy;
        this.radius = radius;
        this.mass = mass;
        this.border = border;
        this.color = color;
    }

    public void draw() {
        StdDraw.filledCircle(pos_x,pos_y,radius);
        StdDraw.setPenColor(color);
    }

    public void move (double dt) {
        pos_x = pos_x + vec_x * dt;
        pos_y = pos_y + vec_y * dt;
    }

    public double timeToHitBall (Ball that) {
        if (this == that) return INFINITY;
        double dx = that.pos_x - this.pos_x;
        double dy = that.pos_y - this.pos_y;
        double dvx = that.vec_x - this.vec_x;
        double dvy = that.vec_y - this.vec_y;
        double dvdr = dx * dvx + dy * dvy;
        if (dvdr > 0) return INFINITY;
        double dvdv = dvx * dvx + dvy * dvy;
        if (dvdv == 0) return INFINITY;
        double drdr = dx * dx + dy * dy;
        double sigma = this.radius + that.radius;
        double d = (dvdr * dvdr) - dvdv * (drdr - sigma * sigma);
        if (d < 0) return INFINITY;
        return -(dvdr + Math.sqrt(d)) / dvdv;
    }

    public double timeToHitVerticalWall() {
        if (vec_x > 0)  return (border - pos_x - radius) / vec_x;
        else if (vec_x < 0) return (radius - pos_x) / vec_x;
        else return INFINITY;
    }

    public double timeToHitHorizontalWall() {
        if (vec_y > 0)  return (border - pos_y - radius) / vec_y;
        else if (vec_y < 0) return (radius - pos_y) / vec_y;
        return  INFINITY;
    }


    public void bounceOff (Ball that) {
        double dx = that.pos_x - this.pos_x;
        double dy = that.pos_y - this.pos_y;
        double dvx = that.vec_x - this.vec_x;
        double dvy = that.vec_y - this.vec_y;
        double dvdr = dx * dvx + dy * dvy;
        double dist = this.radius + that.radius;
        double J = 2 * this.mass * that.mass * dvdr / ((this.mass + that.mass) * dist);
        double Jx = J * dx / dist;
        double Jy = J * dy / dist;
        this.vec_x += Jx / this.mass;
        this.vec_y += Jy / this.mass;
        that.vec_x -= Jx / that.mass;
        that.vec_y -= Jy / that.mass;
        this.cnt ++;
        that.cnt ++;
    }

    public int count() {
        return cnt;
    }

    public void bounceOffVerticalWall () {
        this.cnt ++;
        this.vec_x = - this.vec_x;
    }

    public void bounceoffHorizontalWall () {
        this.cnt ++;
        this.vec_y = - this.vec_y;
    }
}
