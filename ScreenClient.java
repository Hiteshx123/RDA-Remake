package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.zip.DataFormatException;

class ScreenClient extends Thread{

    DatagramSocket client;
    byte[] imageBuffer;
    int port = 1500;
    JFrame frame;


    public static void main(String[] args){
        ScreenClient a = new ScreenClient(1500);
        a.start();
    }
    ScreenClient(int port) {
        this.port = port;
        try {
            client = new DatagramSocket(this.port);
        } catch (SocketException E){
            System.out.println(E);
        }
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException E){
            System.out.println(E);
        }
        byte[] buf = "Hey it works".getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5000);
        try {
            client.send(packet);
        } catch (IOException E){
            System.out.println(E);
        }
    }

     ScreenClient(){
        try {
            client = new DatagramSocket(port);
        } catch (SocketException E){
            System.out.println(E);
        }
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException E){
            System.out.println(E);
        }
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5000);
        try {
            client.send(packet);
        } catch (IOException E){
            System.out.println(E);
        }
    }

    @Override
    public void run(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException E) {
            System.out.println(E);
        }
        Rectangle screenRect = new Rectangle(100, 100);
        CreateJFrame(screenRect);
        Image a = new BufferedImage(1,1,1);
        ImageIcon  c = new ImageIcon(a);
        JLabel b = new JLabel(c);
        imageBuffer = new byte[65508];
        DatagramPacket packet = new DatagramPacket(imageBuffer, imageBuffer.length);
        int i  = 0;
        while(true){
            i++;
            if(screenRect.width != frame.getWidth() || screenRect.height != frame.getHeight()){
                screenRect = new Rectangle((int) (0.95*frame.getWidth()), (int) (0.95*frame.getHeight()));
            }
            try {
                System.out.println(imageBuffer.length);
                client.receive(packet);
                System.out.println("end");
                c.setImage(ImageResizer.resize(createImageFromBytes(/*CompressionUtils.decompress(*/packet.getData()/*)*/), screenRect.width , screenRect.height));
                b.setIcon(c);
            } catch (IOException E){
                System.out.println(E);
            }
            frame.repaint();
        }

    }

    public void CreateJFrame(Rectangle screenRect) {
        frame = new JFrame();
        frame.setSize(screenRect.width,screenRect.height);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setVisible(true);
    }

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
             return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
