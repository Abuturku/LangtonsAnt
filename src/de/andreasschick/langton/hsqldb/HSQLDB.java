package de.andreasschick.langton.hsqldb;

import java.sql.*;

/**
 * Created by Andreas on 05.11.2016.
 */
public class HSQLDB {

    private static HSQLDB instance;

    private Connection con = null;

    public HSQLDB(){
        try{
            Class.forName("org.hsqldb.jdbcDriver");
        }catch ( ClassNotFoundException e ){
            System.err.println( "Treiberklasse nicht gefunden!" );
            return;
        }

        try{
            con = DriverManager.getConnection("jdbc:hsqldb:file:C:\\Users\\Andreas\\hsql; shutdown=true", "root", "" );
        }catch ( SQLException e ){
            e.printStackTrace();
        }
    }

    public static synchronized HSQLDB getInstance(){
        if (HSQLDB.instance == null){
            HSQLDB.instance = new HSQLDB();
        }
        return HSQLDB.instance;
    }

    public ResultSet getAnt(int antId) throws SQLException{
        String sql = "SELECT * FROM ants WHERE antId = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, antId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs;

    }
}
