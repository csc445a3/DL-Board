package database;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    //insert statements
    private static String INSERT_USER = "insert into user (user_name) values (?)";
    private static String UPDATE_USERNAME = "update user set user_name = ? where user_id = ?";
    private static String SELECT_USER = "select user_id from user where user_name = ?";

    //Connection properties for the class
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    BasicDataSource basicDataSource = DataSource.getInstance().getBasicDataSource();


    /**
     * Gets connection to the database
     * @return basicdatasource connection
     * @throws SQLException when no connection made
     */
    private Connection getConnection() throws SQLException {

        return basicDataSource.getConnection();

    }

    /**
     * Closes the connection to the database
     */
    private void closeConnections() {
        try {

            if (preparedStatement != null)
                preparedStatement.close();
            if (connection != null)
                connection.close();
            if (resultSet != null)
                resultSet.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds a user to the database takes a username, userid is procedurally
     * generated in the database
     * @param userName String the user wants their username to be
     */
    public void addUser(String userName) {
        try{
            connection = getConnection();
            preparedStatement = connection.prepareStatement(INSERT_USER);
            preparedStatement.setString(1, userName);
            preparedStatement.execute();

        }catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            closeConnections();
        }
    }

    /**
     * updates a username, user must have their userid in order to do so
     * @param userName new username
     * @param userID userid that is generated when the new user is created
     */
    public void updateUserName(String userName, int userID) {

        try{
            connection = getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_USERNAME);
            preparedStatement.setString(1, userName);
            preparedStatement.setInt(2, userID);
            preparedStatement.execute();

        }catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            closeConnections();
        }

    }

    /**
     * Finds the userID based on the userName
     * @param userName the string of the username
     * @return userID from the database based on userName
     * @throws SQLException when issue connecting to database
     */
    public String selectUserID(String userName) {

        String userID = "";
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(SELECT_USER);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
            userID = resultSet.getString(1);
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            closeConnections();
        }
        return userID;
    }


}
