/*
 * Devin Wiley
 * dxw112230@utdallas.edu
 * A class that will hold the student data from the text file that will
 * be used later for calculation.
 */
package sortedgrades;

/**
 *
 * @author Devin
 */
public class StudentData {
    private String lastName;
    private String firstName;
    private int exam1;
    private int exam2;
    private int assign1;
    private int assign2;
    private int assign3;
    private int assign4;
    private int numericGrade;
    private String letterGrade;
    
    public StudentData(){
        
    }
    
    public String getLastName(){
        return lastName;
    }
    public String getFirstName(){
        return firstName;
    }
    public String getLetterGrade(){
        return letterGrade;
    }
    public int getNumericGrade(){
        return numericGrade;
    }
    
    public void calcNumericGrade(){
        //Final Exam 50%, midterm 25%, assignments 25%
        double finalExam = exam2 * .5;
        double midterm = exam1 * .25;
        double assignments = ((assign1 + assign2 
                + assign3 + assign4) / 4) * .25;
        numericGrade = (int)(finalExam + midterm + assignments);
        calcLetterGrade();
    }
    public void calcLetterGrade(){
        if (numericGrade >= 90){
            letterGrade = "A";
        } else if (numericGrade >= 80){
            letterGrade = "B";
        } else if (numericGrade >= 70){
            letterGrade = "C";
        } else if (numericGrade >= 60){
            letterGrade = "D";
        } else {
            letterGrade = "F";
        }
    }
    
    public void setData(String[] studentData){
        lastName = studentData[0];
        firstName = studentData[1];
        int[] gradeData = new int[6];
        gradeData = stringToInt(studentData);
        exam1 = gradeData[0];
        assign1 = gradeData[1];
        assign2 = gradeData[2];
        exam2 = gradeData[3];
        assign3 = gradeData[4];
        assign4 = gradeData[5];
    }
//    public void getData(){
//        System.out.println(lastName);
//        System.out.println(firstName);
//        System.out.println(exam1);
//        System.out.println(assign1);
//        System.out.println(assign2);
//        System.out.println(exam2);
//        System.out.println(assign3);
//        System.out.println(assign4);
//    }
    
    public int[] stringToInt(String[] studentData){
        int[] gradeData = new int[6];
        int i = 0;
        while ((i < gradeData.length) && ((i+2) < studentData.length)){
            gradeData[i] = parseInt(studentData[i+2]);
            i++;
        }        
        return gradeData;
    }
    
    public int parseInt(String studentData){
        int grade = 0;
        if (studentData.length() != 0){
            grade = Integer.parseInt(studentData);
        } else {
            grade = 0;
        }
        return grade;
        
    }
}
