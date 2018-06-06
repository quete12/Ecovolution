/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.temporary;

/**
 * This class serves the only purpose of playing with arrays and testing their behaviour in for-loops.
 *
 * Conclusion: The for-each loop on an empty array results in a null value (like for(i=0; i<arr.length(); i++) and accessing values with arr[i]).
 * The for-each loop will return all elements (null and non-null values)
 * @author Quentin
 */
public class TestArrays {

    private String txt;

    public TestArrays(String txt) {
        this.txt = txt;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestArrays[] tests = new TestArrays[5];
        for (TestArrays test : tests) {
            System.out.println(test);
        }
            System.out.println("\n\n");
        for (int i = 0; i < tests.length; i++) {
            if (i % 2 == 0) {
                tests[i] = new TestArrays("I'm number: " + i);
            }
        }
        for (TestArrays test : tests) {
            System.out.println(test);
        }
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    @Override
    public String toString() {
        return "Test{" + "txt=" + txt + '}';
    }

}
