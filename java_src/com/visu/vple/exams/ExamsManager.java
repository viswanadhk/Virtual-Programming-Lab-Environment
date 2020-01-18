package com.visu.vple.exams;

import com.visu.vple.application.GeneralUtil;
import com.visu.vple.execution.Worker;
import com.visu.vple.persistence.Criteria;
import com.visu.vple.persistence.DBUtil;
import com.visu.vple.persistence.QueryConstants;
import com.visu.vple.persistence.Tables;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ExamsManager")
public class ExamsManager extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String method = request.getParameter("methodToCall");
            Method method1 = this.getClass().getMethod(method, HttpServletRequest.class, HttpServletResponse.class);
            method1.invoke(this, request, response);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void getExams(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        List<String> selectColumns = new ArrayList<>();
        selectColumns.add("EXAM_ID");
        selectColumns.add("EXAM_NAME");
        selectColumns.add("DURATION");
        selectColumns.add("NO_OF_QUESTIONS");
        JSONArray exams = DBUtil.get(Tables.EXAMS, selectColumns, null);
        response.getWriter().write(exams.toString());
    }

    public void getAvailableExams(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        JSONObject result = new JSONObject();
        List<String> selectColumns = new ArrayList<>();
        selectColumns.add("EXAM_ID");
        selectColumns.add("EXAM_FINISHED");
        // TODO - Remove hardcoded Student ID, get it from the login session params
        System.out.println("rem = "+request.getRemoteUser());
        Criteria criteria = new Criteria("STUDENT_ID", request.getRemoteUser(), QueryConstants.EQUAL);
        JSONArray tempExams = DBUtil.get(Tables.EXAMS_VS_STUDENTS, selectColumns, criteria);
        JSONArray exams = new JSONArray();
        for(int i=0;i<tempExams.length();i++)
        {
            if(!Boolean.parseBoolean(tempExams.getJSONObject(i).get("EXAM_FINISHED").toString()))
            {
                exams.put(tempExams.getJSONObject(i));
            }
        }
        result.put("STATUS", exams.length()>0);
        result.put("EXAM_ID", exams.length()>0 ? exams.getJSONObject(0).getString("EXAM_ID") : -1);
        response.getWriter().write(result.toString());
    }

    public void startExam(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        JSONObject inputParams = new JSONObject(URLDecoder.decode(request.getParameter("req"), "utf-8"));
        JSONObject result = new JSONObject();
        List<String> selectColumns = new ArrayList<>();
        selectColumns.add("QUESTION_ID");
        selectColumns.add("DESCRIPTION");
        selectColumns.add("SCORE");
        // TODO - Remove hardcoded Student ID, get it from the login session params
        Criteria criteria = new Criteria("EXAM_ID", inputParams.getString("EXAM_ID"), QueryConstants.EQUAL);
        JSONArray questions = DBUtil.get(Tables.QUESTIONS, selectColumns, criteria);
        result.put("RESULT", questions);
        response.getWriter().write(result.toString());
    }

    public void saveExam(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        JSONObject retVal = new JSONObject();
        JSONObject inputParams = new JSONObject(URLDecoder.decode(request.getParameter("req"), "utf-8"));
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        List<String> columns = new ArrayList<>();
        columns.add("EXAM_ID");
        columns.add("EXAM_NAME");
        columns.add("DURATION");
        columns.add("NO_OF_QUESTIONS");
        List<Object> values = new ArrayList<>();
        values.add(inputParams.getString("EXAM_ID"));
        values.add(inputParams.getString("EXAM_NAME"));
        values.add(inputParams.getString("DURATION"));
        values.add(inputParams.getString("NO_OF_QUESTIONS"));
        DBUtil.add(Tables.EXAMS, columns, values);

        columns = new ArrayList<>();
        columns.add("EXAM_ID");
        columns.add("DESCRIPTION");
        columns.add("EXPECTED_OUTPUT");
        columns.add("TEST_CASES");
        columns.add("SCORE");

        JSONArray questions = inputParams.getJSONArray("QUESTIONS");

        for(int i=0;i<questions.length();i++)
        {
            JSONObject obj = questions.getJSONObject(i);
            values = new ArrayList<>();
            values.add(inputParams.getString("EXAM_ID"));
            values.add(obj.getString("DESCRIPTION"));
            values.add(obj.getString("EXPECTED_OUTPUT"));
            values.add(obj.getString("TEST_CASES"));
            values.add(obj.getString("SCORE"));

            DBUtil.add(Tables.QUESTIONS, columns, values);
        }

        JSONArray students = inputParams.getJSONArray("STUDENTS");

        columns = new ArrayList<>();
        columns.add("EXAM_ID");
        columns.add("STUDENT_ID");

        for(int i=0;i<students.length();i++)
        {
            String studentID = students.getString(i);
            values = new ArrayList<>();
            values.add(inputParams.getString("EXAM_ID"));
            values.add(studentID);

            DBUtil.add(Tables.EXAMS_VS_STUDENTS, columns, values);
        }

        retVal.put("STATUS", true);
        response.getWriter().write(retVal.toString());
    }

    public void runCode(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        JSONObject retVal = new JSONObject();
        boolean status = false;
        String errorMsg = "";
        JSONObject inputParams = new JSONObject(URLDecoder.decode(request.getParameter("req"), "utf-8"));

        try {
            JSONArray lines = inputParams.getJSONArray("CODE");
            String code = "";

            for(int index=0;index<lines.length();index++)
            {
                JSONObject obj = lines.getJSONObject(index);
                String line = URLDecoder.decode(GeneralUtil.replaceChar(obj, "CODE"), "utf-8");
                code = code + line + "\n";
            }

            status = compileAndRun(inputParams.getString("QUESTION_ID"), code, inputParams.getInt("LANGUAGE"));

            if(!status)
            {
                errorMsg = "Test cases did not match !!";
            }
        }
        catch (Exception ex) {
            errorMsg = ex.getMessage();
        }

        retVal.put("STATUS", status);
        retVal.put("RESULT", errorMsg);
        response.getWriter().write(retVal.toString());
    }

    public static boolean compileAndRun(String questionID, String code, int language) throws Exception
    {
        boolean status = false;

        List<String> columns = new ArrayList<>();
        columns.add("TEST_CASES");
        columns.add("SCORE");
        columns.add("EXPECTED_OUTPUT");
        Criteria criteria = new Criteria("QUESTION_ID", questionID, QueryConstants.EQUAL);
        JSONObject question = DBUtil.get(Tables.QUESTIONS, columns, criteria).getJSONObject(0);

        String output = Worker.compileAndRun(code, question.getString("TEST_CASES"), language);
        String[] expectedOutputLines = question.getString("EXPECTED_OUTPUT").split("\n");
        String[] originalOutputLInes = output.split("\n");

        for(int i = 0;i<expectedOutputLines.length;i++)
        {
            status = expectedOutputLines[i].equalsIgnoreCase(originalOutputLInes[i]);
        }

        return status;
    }

    public void saveCode(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        boolean status = false, examFinished = false;
        String errorMsg = null;
        JSONObject retVal = new JSONObject();

        try {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            retVal = new JSONObject();
            JSONObject inputParams = new JSONObject(URLDecoder.decode(request.getParameter("req"), "utf-8"));

            List<String> selectColumns = new ArrayList<>();
            selectColumns.add("QUESTION_ID");
            selectColumns.add("DESCRIPTION");
            selectColumns.add("SCORE");
            Criteria criteria = new Criteria("EXAM_ID", inputParams.getString("EXAM_ID"), QueryConstants.EQUAL);
            JSONArray questions = DBUtil.get(Tables.QUESTIONS, selectColumns, criteria);

            JSONArray lines = inputParams.getJSONArray("CODE");
            String code = "";
            for(int index=0;index<lines.length();index++)
            {
                JSONObject obj = lines.getJSONObject(index);
                String line = URLDecoder.decode(GeneralUtil.replaceChar(obj, "CODE"), "utf-8");
                code = code + line + "\n";
            }

            String examId = inputParams.getString("EXAM_ID");

            List<String> columns = new ArrayList<>();
            columns.add("UNIQUE_KEY");
            columns.add("STUDENT_ID");
            columns.add("EXAM_ID");
            criteria = new Criteria("STUDENT_ID", request.getRemoteUser(), QueryConstants.EQUAL);
            JSONArray exams = DBUtil.get(Tables.EXAMS_VS_STUDENTS, columns, criteria);
            JSONObject exam = null;

            for(int i=0;i<exams.length();i++)
            {
                JSONObject tempExam = exams.getJSONObject(i);
                if(tempExam.getString("EXAM_ID").equalsIgnoreCase(examId))
                {
                    exam = tempExam;
                    break;
                }
            }

            status = compileAndRun(inputParams.getString("QUESTION_ID"), code, inputParams.getInt("LANGUAGE"));

            if(exam!=null && status)
            {
                String uniqueKey = exam.get("UNIQUE_KEY").toString();

                columns = new ArrayList<>();
                columns.add("EXAM_VS_STUDENTS_FK");
                columns.add("QUESTION_ID");
                columns.add("ANSWER");
                List<Object> values = new ArrayList<>();
                values.add(uniqueKey);
                values.add(inputParams.getString("QUESTION_ID"));
                values.add(code);
                DBUtil.add(Tables.STUDENT_ANSWERS, columns, values);

                String score = null;

                for(int i=0;i<questions.length();i++)
                {
                    if(inputParams.getString("QUESTION_ID").equalsIgnoreCase(questions.getJSONObject(i).getString("QUESTION_ID")))
                    {
                        score = questions.getJSONObject(i).get("SCORE").toString();
                        updateScore(uniqueKey, score);
                        break;
                    }
                }

                columns = new ArrayList<>();
                columns.add("EXAM_VS_STUDENTS_FK");
                columns.add("QUESTION_ID");
                columns.add("ANSWER");
                criteria = new Criteria("EXAM_VS_STUDENTS_FK", exam.get("UNIQUE_KEY").toString(), QueryConstants.EQUAL);
                JSONArray submittedAnswers = DBUtil.get(Tables.STUDENT_ANSWERS, columns, criteria);
                if(questions.length()==submittedAnswers.length())
                {
                    examFinished = true;
                    updateExamFinished(uniqueKey);
                }
            }
            else {
                throw new Exception("Test cases did not match !!");
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        finally {
            retVal.put("STATUS", status);
            retVal.put("ERROR_MSG", errorMsg);
            retVal.put("EXAM_FINISHED", examFinished);
        }

        response.getWriter().write(retVal.toString());
    }

    public static void updateScore(String uniqueKey, String score)
    {
//        Map<String,Object> map = new HashMap<>();
//        map.put("EXAM_FINISHED", true);
//        Criteria criteria = new Criteria("UNIQUE_KEY", uniqueKey, QueryConstants.EQUAL);
//        DBUtil.update(Tables.EXAMS_VS_STUDENTS, map, criteria);
    }

    public static void updateExamFinished(String uniqueKey)
    {
        Map<String,Object> map = new HashMap<>();
        map.put("EXAM_FINISHED", 1);
        Criteria criteria = new Criteria("UNIQUE_KEY", uniqueKey, QueryConstants.EQUAL);
        DBUtil.update(Tables.EXAMS_VS_STUDENTS, map, criteria);
    }
}
