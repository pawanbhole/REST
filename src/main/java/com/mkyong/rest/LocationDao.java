package com.mkyong.rest;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class LocationDao {
 
    enum TestTableColumns{
        id,TEXT;
    }
 
    private final String jdbcDriverStr;
    private final String jdbcURL;
 
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
 
    public LocationDao(String jdbcDriverStr, String jdbcURL){
        this.jdbcDriverStr = jdbcDriverStr;
        this.jdbcURL = jdbcURL;
    }
 
    public MyLocation readData(String userId) {
    	MyLocation myLocation= new MyLocation();
        try {
            Class.forName(jdbcDriverStr);
            connection = DriverManager.getConnection(jdbcURL);
            preparedStatement = connection.prepareStatement("SELECT * FROM `javaTestDB`.`lastlocation` WHERE user_id = ? and timestamp = (SELECT MAX(timestamp) FROM `javaTestDB`.`lastlocation`)");
            preparedStatement.setString(1,userId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
            	myLocation.setLatitude(resultSet.getDouble("latitude"));
            	myLocation.setLongitude(resultSet.getDouble("longitude"));
            	myLocation.setUserId(resultSet.getString("user_id"));
            	myLocation.setTimestamp(resultSet.getLong("timestamp"));
            }
            System.out.println(myLocation);
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
            close();
        }
        return myLocation;
    }
    
    public MyLocation writeData(MyLocation myLocation) {
        try {
            Class.forName(jdbcDriverStr);
            connection = DriverManager.getConnection(jdbcURL);
            preparedStatement = connection.prepareStatement("INSERT INTO `javaTestDB`.`lastlocation` (`user_id`, `timestamp`, `latitude`, `longitude`) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, myLocation.getUserId());
            preparedStatement.setLong(2, myLocation.getTimestamp());
            preparedStatement.setDouble(3, myLocation.getLatitude());
            preparedStatement.setDouble(4, myLocation.getLongitude());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
            close();
        }
        return myLocation;
    }
 
    private void close(){
        try {
            if(resultSet!=null) resultSet.close();
            if(statement!=null) statement.close();
            if(connection!=null) connection.close();
        } catch(Exception e){}
    }
}