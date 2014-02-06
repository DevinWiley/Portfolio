/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Random;
import javax.imageio.ImageIO;
/**
 *
 * @author Devin
 */
public class Server {
    private static int port;
    private int direction = 0;
    
    /**
     * @param args the command line arguments
     */
    public Server(){
        setup();
    }
    
    public static void main(String[] args) {
        if (args.length > 0){
            System.out.println(args[0]);
            port = Integer.parseInt(args[0]);
            new Server();
        } else {
            System.out.println("Arguments entered: " 
                    + args.length);
        }
    }
    public void setup(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");
            DataInputStream fromClient = new DataInputStream(
                    socket.getInputStream());
            while (socket.isConnected()){
                int request = fromClient.readInt();
                DataOutputStream toClient = new DataOutputStream(
                        socket.getOutputStream());
                switch (request){
                    case 0:
                        turnLeft();
                        toClient.writeBoolean(true);
                        System.out.println("rover turn left 90 degrees.");
                        break;
                    case 1:
                        turnRight();
                        toClient.writeBoolean(true);
                        System.out.println("Rover turns right 90 degrees.");
                        break;
                    case 2:
                        ImageIO.write(takePicture(), "JPEG", toClient);
                        System.out.println("Rover sends image.");
                        break;
                    case 3:
                        toClient.writeInt(getDirection());
                        System.out.println("Rover current direction is "
                                + getDirection() + ".");
                        break;
                    case 4:
                        int temp;
                        toClient.writeInt(temp = getAirTemp());
                        System.out.println("Rover sends air temperature of "
                                + temp + "C.");
                        break;
                    case 5:
                        socket.close();
                        System.out.println("Client disconnected.");
                        break;
                }
            }  
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
    
    public void turnLeft(){
        direction = (direction + 450) % 360;
    }
    public void turnRight(){
        direction = (direction + 270) % 360;
    }
    public BufferedImage takePicture() throws IOException{
        BufferedImage image = null;
        switch (getDirection() % 360){
            case 0:
                image = ImageIO.read(new File("image1.jpg"));
                break;
            case 90:
                image = ImageIO.read(new File("image2.jpg"));
                break;
            case 180:
                image = ImageIO.read(new File("image3.jpg"));
                break;
            case 270:
                image = ImageIO.read(new File("image4.jpg"));
                break;
        }
        return image;
    }
    public int getDirection(){
        return direction;
    }
    public int getAirTemp(){
        int temp = (int)(Math.random() * 50.0);
        int negative = new Random().nextInt(2);
        if (negative == 0) {
            temp *= -1;
        }
        return temp;
    }
}
