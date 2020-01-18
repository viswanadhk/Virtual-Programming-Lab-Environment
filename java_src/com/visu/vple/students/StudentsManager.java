package com.visu.vple.students;

import com.visu.vple.persistence.DBUtil;
import com.visu.vple.persistence.Tables;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.List;

@WebServlet("/StudentsManager")
public class StudentsManager extends HttpServlet {
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

    public void getStudents(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        List<String> selectColumns = new ArrayList<>();
        selectColumns.add("STUDENT_ID");
        selectColumns.add("FIRST_NAME");
        selectColumns.add("LAST_NAME");
        JSONArray students = DBUtil.get(Tables.STUDENTS, selectColumns, null);
        response.getWriter().write(students.toString());
    }

    public static boolean studentsExists(String studentId) throws JSONException {
        List<String> selectColumns = new ArrayList<>();
        selectColumns.add("STUDENT_ID");
        JSONArray students = DBUtil.get(Tables.STUDENTS, selectColumns, null);
        for(int index=0;index<students.length();index++)
        {
            if(studentId.equalsIgnoreCase(students.getJSONObject(index).getString("STUDENT_ID")))
                return true;
        }

        return false;
    }

    public void saveStudent(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        JSONObject retVal = new JSONObject();
        JSONObject inputParams = new JSONObject(URLDecoder.decode(request.getParameter("req"), "utf-8"));
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        boolean status = false;
        if(!studentsExists(inputParams.getString("STUDENT_ID")))
        {
            List<String> columns = new ArrayList<>();
            columns.add("STUDENT_ID");
            columns.add("FIRST_NAME");
            columns.add("LAST_NAME");
            List<Object> values = new ArrayList<>();
            values.add(inputParams.getString("STUDENT_ID"));
            values.add(inputParams.getString("FIRST_NAME"));
            values.add(inputParams.getString("LAST_NAME"));
            DBUtil.add(Tables.STUDENTS, columns, values);
            columns = new ArrayList<>();
            columns.add("STUDENT_ID");
            columns.add("ROLE");
            values = new ArrayList<>();
            values.add(inputParams.getString("STUDENT_ID"));
            values.add("student");
            DBUtil.add(Tables.RIGHTS, columns, values);
            status = true;
        }
        else {
            retVal.put("ERROR_MSG", "This student has already been added");
        }
        retVal.put("STATUS", status);
        response.getWriter().write(retVal.toString());
    }
}

