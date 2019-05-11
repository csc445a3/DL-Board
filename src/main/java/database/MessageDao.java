package database;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MessageDao {

    //Query Strings
    private static String INSERT_MESSAGE = "insert into message (user_id, message_content) values (?,?)";
    private static String SELECT_ALL_MESSAGES = "select * from message";

    //Variables for the database
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    BasicDataSource basicDataSource = DataSource.getInstance().getBasicDataSource();


    /**
     * Gets connection to the database and returns it
     * @return connection to the database
     * @throws SQLException if an error connecting to the database
     */
    private Connection getConnection() throws SQLException {

        return basicDataSource.getConnection();

    }

    /**
     * closes connections from the variables for the database
     */
    private void closeConnection() {
        try{
            if (connection != null)
                connection.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (resultSet != null)
                resultSet.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Add a message to the database
     * @param userID the user id which is an int
     * @param messageContent the message to be inserted
     */
    public void addMessage(int userID, String messageContent) {

        try{

            connection = getConnection();
            preparedStatement = connection.prepareStatement(INSERT_MESSAGE);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, messageContent);
            preparedStatement.execute();

        }catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            closeConnection();
        }

    }

    /**
     * Get all the messages from the database and insert them into a list
     * @return the list of all the messages in the database
     */
    public List<String> selectAllMessages() {

        List<String> messageList = null;

        try{

            connection = getConnection();
            preparedStatement = connection.prepareStatement(INSERT_MESSAGE);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String messageContent = resultSet.getString(3);
                if (messageContent != null && !messageContent.isEmpty())
                    messageList.add(messageContent);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            closeConnection();
        }

        return messageList;
    }



}
