package com.visu.vple.persistence;

import com.sun.org.apache.bcel.internal.generic.FADD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class DBUtil {

    public static JSONArray get(String tableName, List<String> selectColumns, Criteria criteria)
    {
        JSONArray rows = new JSONArray();

        try {
            StringBuilder sql = new StringBuilder("SELECT ");
            int index=0;
            for (String selectColumn : selectColumns)
            {
                sql.append(selectColumn);
                if(index!=selectColumns.size()-1)
                {
                    sql.append(", ");
                }
                index++;
            }
            sql.append(" FROM "+tableName);
            if(criteria!=null)
            {
                sql.append(" WHERE "+criteria.toString());
            }

            System.out.println(sql.toString());

            try(Connection connection = ConnectionPool.getConnection())
            {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql.toString());

                while (result.next()){
                    JSONObject row = new JSONObject();
                    int columnIndex = 0;
                    for (String selectColumn : selectColumns)
                    {
                        row.put(selectColumn, result.getObject(selectColumn));
                        columnIndex++;
                    }
                    if(tableName.equalsIgnoreCase(Tables.QUESTIONS))
                    {
                        row.put("ALREADY_SUBMITTED", false);
                    }
                    rows.put(row);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    public static JSONArray update(String tableName, Map<String, Object> selectColumns, Criteria criteria)
    {
        JSONArray rows = new JSONArray();

        try {
            StringBuilder sql = new StringBuilder("UPDATE "+tableName+" SET ");
            int index=0;
            for (Map.Entry entry : selectColumns.entrySet())
            {
                sql.append(entry.getKey()+"="+"\'"+entry.getValue()+"\'");
                if(index!=selectColumns.size()-1)
                {
                    sql.append(",");
                }
                index++;
            }
            if(criteria!=null)
            {
                sql.append(" WHERE "+criteria.toString());
            }

            System.out.println(sql.toString());

            try(Connection connection = ConnectionPool.getConnection())
            {
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql.toString());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    public static boolean add(String tableName, List<String> columns, List<Object> values)
    {
        boolean status = false;
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName);
        sql.append(" (");
        int index=0;
        StringBuilder cardChars = new StringBuilder("(");
        for(String column :  columns)
        {
            sql.append(column);
            cardChars.append("?");
            if(index!=columns.size()-1)
            {
                sql.append(",");
                cardChars.append(",");
            }
            index++;
        }
        sql.append(")");
        cardChars.append(")");
        sql.append(" VALUES");
        sql.append(cardChars.toString());

        System.out.println(sql);

        try(Connection connection = ConnectionPool.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            index=0;
            for(Object value : values)
            {
                index++;
                statement.setObject(index, value);
            }
            int rowsInserted = statement.executeUpdate();
            status = rowsInserted>0;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return status;
    }
}
