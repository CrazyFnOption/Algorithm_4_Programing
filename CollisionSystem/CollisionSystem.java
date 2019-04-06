import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;

public class CollisionSystem {
    protected MinPQ<Collison_Event> pq;
    //相当于一个总控时间的一个参数，后面所有关于时间的参数都需要加上一个这个
    protected double t = 0.0;
    protected Ball[] balls;
    //这里就相当于一个控制每一帧数的频率控制
    private static final double HZ = 0.5;

    public CollisionSystem(Ball[] balls) {
        this.balls = balls.clone();
    }

    //这里新加上的一个参数 limit 是控制一些完全没必要的事件
    //意思就是剔除那些需要太长时间才会相撞的球体

    protected void pridict (Ball b,double limit) {
        if (b == null) return;

        for (int i = 0;i < balls.length;i++){
            double dt = b.timeToHitBall(balls[i]);
            if (t + dt <= limit)
                pq.insert(new Collison_Event(b,balls[i],dt + t));
        }
        double dxt = b.timeToHitHorizontalWall() + t;
        double dyt = b.timeToHitVerticalWall() + t;
        if (dxt <= limit)
            pq.insert(new Collison_Event(b,null,dxt));
        if (dyt <= limit)
            pq.insert(new Collison_Event(null,b,dyt));
    }

    protected void redrew(double limit) {
        StdDraw.clear();
        for (int i = 0;i < balls.length;i++) {
            balls[i].draw();
        }
        StdDraw.show();
        StdDraw.pause(20);
        if (t < limit) {
            pq.insert(new Collison_Event(null,null,t + 1.0 / HZ));
        }
    }

    protected void simulate (double limit) {
        pq = new MinPQ<Collison_Event>();
        for (int i = 0; i < balls.length; i++) {
            pridict(balls[i],limit);
        }
        pq.insert(new Collison_Event(null,null,0));

        while (!pq.isEmpty()) {
            Collison_Event event = pq.delMin();

            if (!event.isValid()) continue;

            //将其他的点全部移动
            for (int i = 0; i < balls.length; i++) {
                balls[i].move(event.time - t);
            }

            t = event.time;

            if (event.a != null && event.b != null) event.a.bounceOff(event.b);
            else if (event.a == null && event.b != null) event.b.bounceOffVerticalWall();
            else if (event.a != null && event.b == null) event.a.bounceoffHorizontalWall();
            else if (event.a == null && event.b == null) redrew(limit);

            pridict(event.a,limit);
            pridict(event.b,limit);
        }
    }




    private static class Collison_Event implements Comparable<Collison_Event>{
        private final Ball a,b;
        private final double time;
        private final int countA,countB;

        public Collison_Event (Ball a,Ball b,double time) {
            this.time = time;
            this.a = a;
            this.b = b;
            if (a != null) countA = a.count();
            else           countA = -1;
            if (b != null) countB = b.count();
            else           countB = -1;
        }

        public int compareTo(Collison_Event that) {
            return Double.compare(this.time,that.time);
        }

        public boolean isValid () {
            if (this.time == Double.POSITIVE_INFINITY) return false;
            else {
                    if (a != null && a.count() != countA) return false;
                    if (b != null && b.count() != countB) return false;
                    return true;
            }
        }
    }

    public static void  main(String [] args){
        StdDraw.setCanvasSize(600,600);
        //这个是清楚缓冲 帮助改善运动的
        StdDraw.enableDoubleBuffering();
        Ball[] balls = new Ball[100];
        for (int i = 0;i < 100; i++) {
            balls[i] = new Ball();
        }
        CollisionSystem system = new CollisionSystem(balls);
        system.simulate(100000);
    }

}

