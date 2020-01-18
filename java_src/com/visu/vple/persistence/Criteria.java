package com.visu.vple.persistence;

public class Criteria {

    String columnName;
    String value;
    QueryConstants queryConstant;

    public Criteria(String columnName, String value, QueryConstants queryConstant)
    {
        this.columnName = columnName;
        this.value = value;
        this.queryConstant = queryConstant;
    }

    @Override
    public String toString()
    {
        return queryConstant.equals(QueryConstants.NOT_EQUAL) || queryConstant.equals(QueryConstants.NOT_IN) ? "NOT" : "" + " " + columnName + getQueryConstantString() + "\'" + value + "\'";
    }

    private String getQueryConstantString()
    {
        if(queryConstant.equals(QueryConstants.EQUAL) || queryConstant.equals(queryConstant.equals(QueryConstants.NOT_EQUAL)))
        {
            return "=";
        }
        return "";
    }
}
