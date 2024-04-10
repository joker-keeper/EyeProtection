package com.example.eyeprotection.camera;

import android.graphics.Bitmap;

import java.util.concurrent.LinkedBlockingQueue;

public class photoQuene {
    private static final int MAX_SIZE = 10;
    private static LinkedBlockingQueue<byte[]> quene;
    public photoQuene(){
        quene = new LinkedBlockingQueue<>(MAX_SIZE);
    }
    public static void addPhoto(byte[] photo){
        try{
            quene.put(photo);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public static byte[] getPhoto(){
        try {
            return quene.take();
        }catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isEmpty(){
        return quene.isEmpty();
    }

    public static boolean isFull(){
        return quene.size() >= quene.remainingCapacity();
    }

    public static int getSize() {
        return quene.size();
    }
}
