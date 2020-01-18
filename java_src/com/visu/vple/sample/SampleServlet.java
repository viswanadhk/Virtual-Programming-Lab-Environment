package com.visu.vple.sample;

import java.io.*;

public class SampleServlet{

    public static void main(String[] args) throws Exception {
//        String input1 = "1", input2 = "2";
//        Process process = Runtime.getRuntime().exec("D:\\Test.exe");
//        BufferedWriter writer = new BufferedWriter(
//                new OutputStreamWriter(process.getOutputStream())
//        );
//        writer.write(input1);
//        writer.newLine();
//        writer.write(input2);
//        writer.newLine();
//        writer.close();
//        BufferedReader brResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String outputRun;
//        while((outputRun = brResult.readLine())!=null)
//        {
//            System.out.println("Output Run = " + outputRun);
//        }
//        brResult.close();
//        compileAndRun("jkl", null);
//        String str1 = "a" + "\n" + "b", str2 = "a" + "\n" + "b";
//        System.out.println(str1.equalsIgnoreCase(str2));
        String str = "#include<stdio.h>\\nint main()\\n{\\nreturn 0;\\n}";
//        System.out.println(str);
        System.out.println(str.replace("\\n", "\n"));
    }

    public static final String WORKING_DIR="D:";
    public static final String COMPILER_C= "D:\\mingw64\\bin\\gcc";
    public static final String COMPILER_CPP="D:\\mingw64\\bin\\g++";
    public static final String COMPILER_JAVA="javac";

    public static void compileAndRun(String codeToCompile, String testCases) throws Exception
    {
//        String[] inputs = testCases.split("\n");
        String command = COMPILER_CPP + " -o " + "D:\\" + codeToCompile+" "+ "D:\\"+codeToCompile+".c";
        System.out.println(command);
        Process process = Runtime.getRuntime().exec(command);
//        BufferedWriter inputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//        for(String input : inputs)
//        {
//            inputWriter.write(input);
//            inputWriter.newLine();
//        }
//        inputWriter.close();
//        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String tempOutput, finalOutput = "";
//        while((tempOutput = outputReader.readLine())!=null)
//        {
//            finalOutput+=tempOutput+"\n";
//        }
//        outputReader.close();
        String line;
        BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = bre.readLine()) != null)
        {
            System.out.println(line);
        }
        bre.close();
    }
}
