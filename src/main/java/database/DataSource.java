package database;

import org.apache.commons.dbcp.BasicDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSource {

    private static DataSource dataSource;
    private BasicDataSource basicDataSource = new BasicDataSource();
    Properties dbProperties = new Properties();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db.properties");

    /**
     * The constructor for the datasource connection
     */
    private DataSource() {

        try{
            dbProperties.load(inputStream);
        }catch(IOException e){
            e.printStackTrace();
        }

        basicDataSource.setDriverClassName(dbProperties.getProperty("db.driverClassName"));
        basicDataSource.setUrl(dbProperties.getProperty("db.url"));
        basicDataSource.setUsername(dbProperties.getProperty("db.userName"));
        basicDataSource.setPassword(dbProperties.getProperty("db.password"));

        basicDataSource.setInitialSize(10);
        basicDataSource.setMaxActive(100);

    }

    /**
     * returns the dataSource object, instantiates if null
     * @return dataSource
     */
    public static DataSource getInstance() {

        if(dataSource == null)
            dataSource = new DataSource();
        return dataSource;

    }

    /**
     * getter for the basicDataSource
     * @return basicDataSource object
     */
    public BasicDataSource getBasicDataSource(){
        return basicDataSource;
    }




}
