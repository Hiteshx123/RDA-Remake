package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class Sharer_Server extends Thread{
    DatagramSocket server;
    byte[] imageBuffer;

    int port = 5000;

    public static void main(String[] args) {
	    Sharer_Server server = new Sharer_Server();
	    server.start();
    }

    @Override
    public void run() {
        byte[] inputTest = new byte[256];
        DatagramPacket packet = new DatagramPacket(inputTest, inputTest.length);
        try {
            server.receive(packet);
        } catch (IOException E){
            System.out.println(E);
        }
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        System.out.println(new String(packet.getData()));
        ScreenCapture capture = new ScreenCapture();
        capture.start();
        try{
            Thread.sleep(3000);
        } catch (InterruptedException E){
            System.out.println(E);
        }
        while(true){
            try {
                imageBuffer = /*CompressionUtils.compress(*/ChangeImage(ImageResizer.resize(addCursor(capture.pic), 720, 480))/*)*/;
            } catch (IOException E){
                System.out.println(E);
            }
            packet = new DatagramPacket(imageBuffer, imageBuffer.length, address, port);
            System.out.println(imageBuffer.length);
            try {
                server.send(packet);
            } catch (IOException E){
                System.out.println(E);
            }
        }
    }

     Sharer_Server(int port) {
        this.port = port;
        try {
            server = new DatagramSocket(this.port);
        } catch (SocketException E){
            System.out.println(E);
        }
    }

    Sharer_Server(){
        try {
            server = new DatagramSocket(this.port);
        } catch (SocketException E){
            System.out.println(E);
        }
    }

    class ScreenCapture extends Thread {
        protected BufferedImage pic;
        private Rectangle screenRect = new Rectangle(1920,1080);
        public void run() {
            while(true) {
                pic = JNAScreenShot.getScreenshot(screenRect);
            }
        }
    }

    byte[] ChangeImage(BufferedImage image){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", out);
            ImageIO.write(image, "jpeg", new File("C:\\Users\\Hitesh Ale (Main)\\IdeaProjects\\BetterRDA\\1.jpeg"));
        } catch (IOException E){
            System.out.println(E);
        }
        return out.toByteArray();
    }

    BufferedImage addCursor(BufferedImage image){
        Image cursor = null;
        try {
            cursor = ImageIO.read(new File("D:\\Downloads\\click_2-512.png"));
        } catch (IOException E){
            System.out.println(E);
        }
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(cursor, x, y, 16, 16, null);
        return image;
    }

}
