package com.mkyong.rest;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class MySQLJava {
 
    enum TestTableColumns{
        id,TEXT;
    }
 
    private final String jdbcDriverStr;
    private final String jdbcURL;
 
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
 
    public MySQLJava(String jdbcDriverStr, String jdbcURL){
        this.jdbcDriverStr = jdbcDriverStr;
        this.jdbcURL = jdbcURL;
    }
 
    public String readData(String message) {
    	String res = "message received was:"+message;
        try {
            Class.forName(jdbcDriverStr);
            connection = DriverManager.getConnection(jdbcURL);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from javaTestDB.TEST_TABLE;");
            getResultSet(resultSet);
            preparedStatement = connection.prepareStatement("insert into javaTestDB.TEST_TABLE values (default,?)");
            preparedStatement.setString(1,res);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = e.getMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = e.getMessage();
		}finally{
            close();
        }
        return res;
    }
 
    private void getResultSet(ResultSet resultSet) throws Exception {
        while(resultSet.next()){
            Integer id = resultSet.getInt(TestTableColumns.id.toString());
            String text = resultSet.getString(TestTableColumns.TEXT.toString());
            System.out.println("id: "+id);
            System.out.println("text: "+text);
        }
    }
 
    private void close(){
        try {
            if(resultSet!=null) resultSet.close();
            if(statement!=null) statement.close();
            if(connection!=null) connection.close();
        } catch(Exception e){}
    }
}