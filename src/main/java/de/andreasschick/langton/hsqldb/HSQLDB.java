package de.andreasschick.langton.hsqldb;

import de.andreasschick.langton.application.Ant;
import de.andreasschick.langton.application.Application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HSQLDB {

    private static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    private static final String DB_URL = "jdbc:hsqldb:hsql://localhost/langtonsAnt";

    private static final String USER = "Ant";
    private static final String PASS = "elefant123";

    private static HSQLDB instance;

    private Connection con = null;

    public HSQLDB(){
        try{
            Class.forName(JDBC_DRIVER);
        }catch ( ClassNotFoundException e ){
            System.err.println("Couldn't find JDBC driver class");
            return;
        }

        try{
            con = DriverManager.getConnection(DB_URL, USER, PASS );
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

    public ArrayList<MovementRow> getAntMovements(int antId, int applicationId, int maxStep) throws SQLException {
        ArrayList<MovementRow> movements = new ArrayList<>();

        //Select movements of Ant with id antId
        String selectString = "SELECT XPOSITION, YPOSITION, MOVEMENTINDEX FROM MOVEMENT WHERE ANTID = ? AND MOVEMENTINDEX <= ? AND APPLICATIONID = ? ORDER BY MOVEMENTINDEX";
        PreparedStatement selectStatement = con.prepareStatement(selectString);
        selectStatement.setInt(1, antId);
        selectStatement.setInt(2, maxStep);
        selectStatement.setInt(3, applicationId);
        ResultSet resultSet = selectStatement.executeQuery();

        while (resultSet.next()){
            movements.add(new MovementRow(antId, resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3)));
        }

        return movements;
    }
    
    public List<Application> getAllApplications() throws SQLException{
    	List<Application> list = new ArrayList<>();
    	
    	String selectString = "SELECT ID, NUMBEROFANTS, MOVEMENTRULE, TABLEAUSIZE FROM APPLICATION";
        PreparedStatement selectStatement = con.prepareStatement(selectString);
        ResultSet resultSet = selectStatement.executeQuery();

        while(resultSet.next()){
        	list.add(new Application((byte) resultSet.getInt(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getInt(1)));
        }
    	
    	return list;
    }
    
    public Application getApplication(int applicationId) throws SQLException {
        String selectString = "SELECT NUMBEROFANTS, MOVEMENTRULE, TABLEAUSIZE FROM APPLICATION WHERE ID = ?";
        PreparedStatement selectStatement = con.prepareStatement(selectString);
        selectStatement.setInt(1, applicationId);
        ResultSet resultSet = selectStatement.executeQuery();

        resultSet.next();

        return new Application((byte)resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), applicationId);
    }

    public List<Ant> getAllAnts(int applicationId, int maxStep) throws SQLException {
        List<Ant> ants = new ArrayList<>();
        int numberOfAnts = getApplication(applicationId).getNumberOfAnts();
        for (int i = 1; i <=  numberOfAnts; i++){

            ants.add(new Ant(i, applicationId, maxStep));
        }
        return ants;
    }

    public int getCountOfRightTurns(int antId, int applicationId, int maxStep) throws SQLException {
        int count = 0;
        String selectString = "SELECT DIRECTION FROM MOVEMENT WHERE ANTID = ? AND APPLICATIONID = ? AND MOVEMENTINDEX <= ?";
        PreparedStatement selectStatement = con.prepareStatement(selectString);
        selectStatement.setInt(1, antId);
        selectStatement.setInt(2, applicationId);
        selectStatement.setInt(3, maxStep);
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();
        String previousDirection = resultSet.getString(1);

        while (resultSet.next()){
            if (previousDirection.equals("DOWN") && resultSet.getString(1).equals("LEFT")){
                count++;
            }else if(previousDirection.equals("UP") && resultSet.getString(1).equals("RIGHT")){
                count++;
            }else if (previousDirection.equals("LEFT") && resultSet.getString(1).equals("UP")){
                count++;
            }else if(previousDirection.equals("RIGHT") && resultSet.getString(1).equals("DOWN")){
                count++;
            }
            previousDirection = resultSet.getString(1);
        }
        return count;
    }

    public int getCountOfLeftTurns(int antId, int applicationId, int maxStep) throws SQLException {
        int count = 0;
        String selectString = "SELECT DIRECTION FROM MOVEMENT WHERE ANTID = ? AND APPLICATIONID = ? AND MOVEMENTINDEX <= ?";
        PreparedStatement selectStatement = con.prepareStatement(selectString);
        selectStatement.setInt(1, antId);
        selectStatement.setInt(2, applicationId);
        selectStatement.setInt(3, maxStep);
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();
        String previousDirection = resultSet.getString(1);

        while (resultSet.next()){
            if (previousDirection.equals("DOWN") && resultSet.getString(1).equals("RIGHT")){
                count++;
            }else if(previousDirection.equals("UP") && resultSet.getString(1).equals("LEFT")){
                count++;
            }else if (previousDirection.equals("LEFT") && resultSet.getString(1).equals("DOWN")){
                count++;
            }else if(previousDirection.equals("RIGHT") && resultSet.getString(1).equals("UP")){
                count++;
            }
            previousDirection = resultSet.getString(1);
        }
        return count;
    }

}
