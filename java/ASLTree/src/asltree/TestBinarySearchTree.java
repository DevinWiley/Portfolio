/*
 * @author Devin Wiley
 */


package asltree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Devin
 */
public class TestBinarySearchTree {
    public static void main(String[] args) throws FileNotFoundException{
        BinarySearchTree testTree1 = new BinarySearchTree();
        File file1 = new File("inputC1.txt");
        File file2 = new File("inputD1.txt");
        File file3 = new File("inputC2.txt");
        File file4 = new File("inputD2.txt");
        testTree1 = treeInputInt(file1, testTree1);
        System.out.println("Max value: " + testTree1.findMax());
        System.out.println("Min value: " + testTree1.findMin());
        Scanner scan = new Scanner(file2);
        String inputInt = scan.nextLine();
        String[] dataInt = inputInt.split(" ");
        for(int i = 0; i < dataInt.length; i++){
            int valueInt = Integer.parseInt(dataInt[i]);
            if(testTree1.contains(valueInt)){
                testTree1.remove(valueInt);
                System.out.println(valueInt + " has been removed.");
            }
        }
        System.out.println("Remaining values in Tree: ");
        testTree1.printPreorder();
        System.out.println("\nSecond Binary Tree\n");
        
        BinarySearchTree testTree2 = new BinarySearchTree();
        testTree2 = treeInputFloat(file3, testTree2);
        System.out.println("Max value: " + testTree2.findMax());
        System.out.println("Min value: " + testTree2.findMin());
        Scanner scan2 = new Scanner(file4);
        String inputfloat;
        while(scan2.hasNext()){
            inputfloat = scan2.nextLine();
            double value = Double.parseDouble(inputfloat);
            if(testTree2.contains(value)){
                testTree2.remove(value);
                System.out.println(value + " has been removed.");
            }
        }
        System.out.println("Remaining values in Tree: ");
        testTree2.printPreorder();
        System.out.println("Finished.");
    }
    public static BinarySearchTree treeInputInt(File file, BinarySearchTree tree) throws FileNotFoundException{
        String input;
        Scanner scan = new Scanner(file);
        input = scan.nextLine();
        String[] data = input.split(" ");
        for(int i = 0; i < data.length; i++){
            int value = Integer.parseInt(data[i]);
            tree.insert(value);
        }
        return tree;
    }
    public static BinarySearchTree treeInputFloat(File file, BinarySearchTree tree) throws FileNotFoundException{
        String input;
        Scanner scan = new Scanner(file);
        while(scan.hasNext()){
            input = scan.nextLine();
            double value = Double.parseDouble(input);
            tree.insert(value);
        }
        return tree;
    }
}
