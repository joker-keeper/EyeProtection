package com.example.eyeprotection.camera;

import java.util.concurrent.LinkedBlockingQueue;

public class gazePointQueue {
    private static final int MAX_SIZE = 50;
    private static LinkedBlockingQueue<Point> queue = new LinkedBlockingQueue<>(MAX_SIZE);
    public static void addGazePoint(double x,double y){
        try{
            Point gazePoint = new Point(x,y);
            queue.put(gazePoint);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public static Point getGazePoint(){
        try {
            return queue.take();
        }catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isEmpty(){
        return queue.isEmpty();
    }

    public static boolean isFull(){
        return queue.size() >= queue.remainingCapacity();
    }

    public static int getSize() {
        return queue.size();
    }
}
