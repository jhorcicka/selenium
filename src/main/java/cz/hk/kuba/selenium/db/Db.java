package cz.hk.kuba.selenium.db;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Db {
    private static final String FILENAME = "postgresql.properties";
    private String driver = "";
    private String url = "";
    private String username = "";
    private String password = "";
    private static Db instance = null;
    private boolean connected = false;
    private Connection connection = null;
    private Statement statement = null;

    public static Db getInstance() {
        if (instance == null) {
            instance = new Db();
        }

        return instance;
    }

    public boolean test() {
        ResultSet rs = query("select 1+2");
        return (rs != null);
    }

    public int getCount(String tableName) {
        String alias = "entries_count";
        String sql = String.format("select count(*) as %s from %s", alias, tableName);
        int count = Integer.parseInt(getSingleValue(sql));

        return count;
    }

    public int getLastId(String tableName) {
        String alias = "last_id";
        String sql = String.format("select max(id) as %s from %s", alias, tableName);
        int lastId = Integer.parseInt(getSingleValue(sql));

        return lastId;
    }

    public void deleteLastRecord(String tableName) {
        String sql = String.format("DELETE FROM %s WHERE id = %s", tableName, getLastId(tableName));
        command(sql);
    }

    public String getSingleValue(String query) {
        String value = "";

        try {
            ResultSet rs = query(query);
            rs.next();
            value = rs.getString(1);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        if (value == null) {
            value = "0";
        }

        return value;
    }

    public void command(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException sqle) {
            error(sqle.toString());
        }
    }

    public ResultSet query(String query) {
        ResultSet result = null;

        try {
            result = statement.executeQuery(query);
        } catch (SQLException sqle) {
            error(sqle.toString());
        }

        return result;
    }

    public void connect() {
        if (connected) {
            return;
        }

        try {
            Class.forName(driver);
        } catch (Exception ex) {
            error(ex.toString());
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            connected = true;
        } catch (SQLException sqle) {
            error(sqle.toString());
        }
    }

    public void disconnect() {
        if (connected == false) {
            return;
        }

        try {
            statement.close();
        } catch (SQLException sqle) {
            error(sqle.toString());
        }

        try {
            connection.close();
        } catch (SQLException sqle) {
            error(sqle.toString());
        }
    }

    private void error(String message) {
        System.err.println("DB ERROR: " + message);
    }

    public void write(ResultSet rs) {
        try {
            while (rs.next()) {
                System.out.println("row(" + rs.getRow() + ")");
                ResultSetMetaData metaData = rs.getMetaData();
                int itemsCount = metaData.getColumnCount();

                for (int i = 1; i <= itemsCount; i++) {
                    System.out.println("column(" + i + "): " + rs.getString(i));
                }
            }
        } catch (SQLException sqle) {
            error(sqle.toString());
        }
    }

    private Db() {
        Properties props = new Properties();
        InputStream inStream;

        try {
            inStream = getClass().getClassLoader().getResourceAsStream(FILENAME);
            props.load(inStream);
            driver = props.getProperty("DRIVER");
            url = props.getProperty("URL");
            username = props.getProperty("USERNAME");
            password = props.getProperty("PASSWORD");
            inStream.close();
        } catch (IOException ioe) {
            error(ioe.toString());
        }
    }
}
