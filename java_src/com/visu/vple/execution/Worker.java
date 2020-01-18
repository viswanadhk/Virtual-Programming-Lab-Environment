package com.visu.vple.execution;

import java.io.*;

public class Worker {

    public static final String TEMP_DIR="D:\\mingw64\\bin";
    public static final String COMPILER_C= "D:\\mingw64\\bin\\gcc";
    public static final String COMPILER_CPP="D:\\mingw64\\bin\\g++";
    public static final String COMPILER_JAVA="javac";

    public enum Languages {
        C,
        CPP,
        JAVA
    }

    private static String getExtension(int language)
    {
        switch (language)
        {
            case 0:
                return ".c";
            case 1:
                return ".cpp";
            case 2:
                return ".java";
            default:
                return "";
        }
    }

    public static void main(String[] args) throws Exception {
        String filePath = "D:\\jkl.c";
        String code = "";

        try(BufferedReader bre = new BufferedReader(new FileReader(filePath)))
        {
            String line, finalErrorOutput = "";

            while ((line = bre.readLine()) != null)
            {
                finalErrorOutput = finalErrorOutput + line + "\n";
            }

            String input = "1\n2";

            System.out.println(compileAndRun(finalErrorOutput, input, 0));
        }
    }

    public static String compileAndRun(String codeToCompile, String testCases, int language) throws Exception
    {
        String finalOutput = "";

        String fileNameWithoutExtension = Long.toString((new java.util.Date()).getTime());
        String fileName = fileNameWithoutExtension + getExtension(language);
        String filePath = TEMP_DIR+File.separator+fileName;
        String executionFilePath = TEMP_DIR+File.separator+fileNameWithoutExtension;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath)))
        {
            writer.write(codeToCompile);
        }

        String compilationCommand = COMPILER_CPP + " -static -o " + TEMP_DIR + File.separator + fileNameWithoutExtension + " "+ filePath;

        System.out.println(compilationCommand);

        Process process = Runtime.getRuntime().exec(compilationCommand);
        process.waitFor();

        try(BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream())))
        {
            String line, finalErrorOutput = "";

            while ((line = bre.readLine()) != null)
            {
                finalErrorOutput+=line;
                finalErrorOutput+="\n";
            }

            if(finalErrorOutput.length()!=0)
            {
                throw new Exception(finalErrorOutput);
            }
        }

        process = Runtime.getRuntime().exec(executionFilePath);
        String[] inputs = testCases.split("\n");

        try(BufferedWriter inputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream())))
        {
            for(String input : inputs)
            {
                inputWriter.write(input);
                inputWriter.newLine();
            }
        }

        process.waitFor();

        try(BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            String tempOutput;
            while((tempOutput = outputReader.readLine())!=null)
            {
                finalOutput+=tempOutput+"\n";
            }
        }

        return finalOutput;
    }
}
