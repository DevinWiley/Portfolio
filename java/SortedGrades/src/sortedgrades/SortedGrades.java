/*
 * Devin Wiley
 * dxw112230@utdallas.edu
 * A program that will read data from a text file and calculate the
 * numeric grade and letter grade of the students
 */
package sortedgrades;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author Devin
 */
public class SortedGrades {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        File file = null;
        String textFile;
        System.out.print("If the file is \"CS1337Asg3Data.txt\" press enter,"
                + " otherwise enter the name of the file: ");
        String input = keyboard.nextLine();
        
        if (input.length() == 0){
            textFile = "CS1337Asg3Data.txt";
        } else {
            textFile = input + ".txt";
        }       
        
        file = new File(textFile);
        Scanner fileLineCounter = new Scanner(file);
        
        int fileLines = 0;
        while (fileLineCounter.hasNext()){
            fileLines++;
            fileLineCounter.nextLine();
        }
        fileLineCounter.close();
        StudentData[] studentData = new StudentData[(fileLines - 1)];
        /*
         * Closes file after lines have been counted
         * and then reopens the file to get character pointer
         * back to beginning of file.
        */
        Scanner inputFile = new Scanner(file);
        String inputText = inputFile.nextLine();
        inputText = inputText.replaceAll("\\s", "");
        String[] inputData = inputText.split(",");
        
        int i = 0;
        while (inputFile.hasNext()){
            inputText = inputFile.nextLine();
            inputText = inputText.replaceAll("\\s", "");
            inputData = inputText.split(",");
            studentData[i] = new StudentData();
            studentData[i].setData(inputData);
            i++;
        }
        
        //Sorts students by last name in accending order A-Z
        studentData = sortStudents(studentData);
        i = 0;
        while (i < studentData.length){
            studentData[i].calcNumericGrade();
            i++;
        }
        int maxGrade = maxGrade(studentData);
        int minGrade = minGrade(studentData);
        int classAverage = average(studentData);
        
        System.out.println("Last Name\tFirstName\tNumeric Grade\tLetter Grade");
        i = 0;
        while (i < studentData.length){
            String tabLastName, tabFirstName;
            if (studentData[i].getLastName().length() <= 8){
                tabLastName = "\t\t";
            } else {
                tabLastName = "\t";
            }
            if (studentData[i].getFirstName().length() <=8){
                tabFirstName = "\t\t";
            } else {
                tabFirstName = "\t";
            }
            System.out.println(studentData[i].getLastName()
                    + tabLastName
                    + studentData[i].getFirstName()
                    + tabFirstName
                    + studentData[i].getNumericGrade()
                    + "\t\t"
                    + studentData[i].getLetterGrade());
            i++;
        }
        System.out.println("\nThe class average was : " + classAverage);
        System.out.println("\nThe highest grade was earned by ");
        System.out.println(studentData[maxGrade].getLastName() + ", "
                + studentData[maxGrade].getFirstName() + " "
                + "who got a : " + studentData[maxGrade].getNumericGrade());
        System.out.println("\nThe lowest grade was earned by ");
        System.out.println(studentData[minGrade].getLastName() + ", "
                + studentData[minGrade].getFirstName() + " "
                + "who got a : " + studentData[minGrade].getNumericGrade());
        System.out.println();
        boolean endSearch = false;
        try {
            while (!endSearch){
                String inputStr = JOptionPane.showInputDialog(null, "Enter the last"
                        + " name of the student you wish to find.");
                inputStr = inputStr.replaceAll("\\s", "");
                if (inputStr.length() > 0){
                    int index = binarySearch(inputStr, studentData);
                    if (index < 0){
                        JOptionPane.showMessageDialog(null, inputStr + " was not found.");
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Last Name, First Name: Grade\n"
                                + studentData[index].getLastName() + ", "
                                + studentData[index].getFirstName() + ": "
                                + studentData[index].getNumericGrade() + "    "
                                + studentData[index].getLetterGrade());
                    }
                } else {
                    endSearch = true;
                }
            }
            
        } catch (NullPointerException e){
            
        }
        System.exit(0);
    }
    
    public static StudentData[] sortStudents(StudentData[] studentData){
        int i = 0;
        StudentData temp = new StudentData();
        while (i < studentData.length){
            for (int j = 0; j < (studentData.length - 1); j++){
                if (studentData[j].getLastName()
                        .compareToIgnoreCase(
                        studentData[(j+1)].getLastName()) > 0) {

                    temp = studentData[j];
                    studentData[j] = studentData[(j + 1)];
                    studentData[(j + 1)] = temp;
                }
            }
            
            i++;
        }
        return studentData;
    }
    
    public static int binarySearch(String searchName, StudentData[] studentData){
        int index = -1;
        int startPoint = 0, endPoint = (studentData.length - 1);
        boolean found = false;
        
        while (startPoint <= endPoint && !found){
            int midPoint = (startPoint + endPoint) / 2;
            if (searchName.
                    compareToIgnoreCase(studentData[midPoint].getLastName()) > 0){
                startPoint = (midPoint + 1);
            } else if (searchName.
                    compareToIgnoreCase(studentData[midPoint].getLastName()) < 0){
                endPoint = (midPoint - 1);
            } else if (searchName.compareToIgnoreCase(studentData[midPoint].getLastName()) == 0){
                found = true;
                index = midPoint;
            }
        }
        return index;
    }
    
    public static int maxGrade(StudentData[] studentData){
        int maxGradeIndex = 0;
        for (int i = 0; i < studentData.length; i++){
            if (studentData[i].getNumericGrade() >
                    studentData[maxGradeIndex].getNumericGrade()){
                maxGradeIndex = i;
            }
        }
        return maxGradeIndex;
    }
    
    public static int minGrade(StudentData[] studentData){
        int minGradeIndex = 0;
        for (int i = 0; i < studentData.length; i++){
            if (studentData[i].getNumericGrade() <
                    studentData[minGradeIndex].getNumericGrade()){
                minGradeIndex = i;
            }
        }
        return minGradeIndex;
    }
    
    public static int average(StudentData[] studentData){
        int accumulator = 0;
        for (int i = 0; i < studentData.length; i++){
            accumulator += studentData[i].getNumericGrade();
        }
        int classAverage = (int) (accumulator / studentData.length);
        return classAverage;
    }
}
