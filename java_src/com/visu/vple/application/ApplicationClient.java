package com.visu.vple.application;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@WebServlet("/ApplicationClient")
public class ApplicationClient extends HttpServlet {
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

    public void getTabs(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        JSONArray tabs = new JSONArray();

        if(request.isUserInRole("admin"))
        {
            tabs.put(new JSONObject().put("DISPLAY_NAME", "Add Exam").put("URL", "#/addExam"));
            tabs.put(new JSONObject().put("DISPLAY_NAME", "Add Student").put("URL", "#/addStudent"));
        }
        else if(request.isUserInRole("student"))
        {
            tabs.put(new JSONObject().put("DISPLAY_NAME", "Take Exam").put("URL", "#/takeExam"));
        }
        response.getWriter().write(tabs.toString());
    }

    public void Logout(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.getSession().invalidate();
        response.getWriter().write("{}");
    }
}
