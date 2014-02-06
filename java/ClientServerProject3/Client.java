/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.imageio.ImageIO;
/**
 *
 * @author Devin
 */
public class Client {
    private static String hostname;
    private static int port;
    private static int pictures = 1;
    /**
     * @param args the command line arguments
     */
    public Client(){
        setup();
    }
    
    public static void main(String[] args) {
        if (args.length > 0){
            hostname = args[0];
            port = Integer.parseInt(args[1]);
            new Client();
        } else {
            System.out.println("Arguments entered: " 
                    + args.length);
        }
    }
    
    public void setup(){
        try {
            Socket socket = new Socket(hostname, port);
            DataOutputStream toServer = new DataOutputStream(
                    socket.getOutputStream());
            DataInputStream fromServer = new DataInputStream(
                    socket.getInputStream());
	    Scanner scanner = new Scanner(System.in);
	    while (socket.isConnected() && !socket.isClosed()){
	    	    printPrompt();
	            String input = scanner.nextLine();
		    System.out.println();
	            if (input.equalsIgnoreCase("A")){
	                toServer.writeInt(0);fromServer.readBoolean();
	                System.out.println("Rover has turned left 90 degrees.");
	            } else if (input.equalsIgnoreCase("B")){
	                toServer.writeInt(1);
	                fromServer.readBoolean();
	                System.out.println("Rover has turned right 90 degrees.");
	            } else if (input.equalsIgnoreCase("C")){
	                toServer.writeInt(2);
			int numBytes = fromServer.readInt();
			byte[] byteArray = new byte[numBytes];
			int numRead = 0, leftToRead = numBytes;
			while (leftToRead > 0){
				int read = fromServer.read(byteArray, numRead, leftToRead);
				if (read < 0){
					break;
				}
				numRead += read;
				leftToRead -= read;
			}
	                ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(byteArray);
			BufferedImage image = ImageIO.read(byteArrayIn);
	                ImageIO.write(image, "JPEG", new File("./client/Image" + pictures + ".jpg"));
	                System.out.println("Image" + pictures++ + ".jpg saved.");
	            } else if (input.equalsIgnoreCase("D")){
	                toServer.writeInt(3);
	                int direction = fromServer.readInt();
	                System.out.println("Rover is currently facing "
	                        + direction + " degrees.");
	            } else if (input.equalsIgnoreCase("E")){
	                toServer.writeInt(4);
	                int temp = fromServer.readInt();
	                System.out.println("Air temperature at Mars rover is " 
	                        + temp + " C.");
	            } else if (input.equalsIgnoreCase("F")){
    	   	         toServer.writeInt(5);
			 socket.close();
        	         System.out.println("Disconnected from Mars rover.");
			 break;
        	    } else {
	   	 	System.out.println("Invalid command entered.");
	  	   }
	    }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
    public void printPrompt(){
    	System.out.println();
        System.out.print(
                "Choose from the list of commands:\n"
                + "\tA. Turn left 90 degrees.\n"
                + "\tB. Turn right 90 degrees.\n"
                + "\tC. Take a picture.\n"
                + "\tD. Get direction.\n"
                + "\tE. Get the air temperature.\n"
                + "\tF. Exit.\n"
                + "Enter choice: ");
    }
}
