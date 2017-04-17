package de.andreasschick.langton.test;


import de.andreasschick.langton.application.Ant;
import de.andreasschick.langton.application.Application;
import de.andreasschick.langton.hsqldb.HSQLDB;
import de.andreasschick.langton.hsqldb.MovementRow;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HSQLDB_Test {

    private HSQLDB hsqldb;

    @Before
    public void setup(){
        hsqldb = HSQLDB.getInstance();
    }

    @Test
    public void singletonTest(){
        hsqldb = null;
        assertNull(hsqldb);
        hsqldb = HSQLDB.getInstance();
        assertNotNull(hsqldb);
    }

    @Test
    public void testGetAllApplications() throws SQLException{
        List<Application> applications = hsqldb.getAllApplications();
        assertEquals(4, applications.size());

    }

    @Test
    public void testGetApplication() throws SQLException{
        Application application = hsqldb.getApplication(1);
        assertEquals(1, application.getId());
    }

    @Test
    public void testGetAllAnts() throws SQLException {
        List<Ant> ants = hsqldb.getAllAnts(1, 100);
        assertEquals(4, ants.size());
    }

    @Test
    public void testGetAntMovements() throws SQLException {
        ArrayList<MovementRow> movements = hsqldb.getAntMovements(1, 1, 100);
        assertEquals(101, movements.size());
    }

    @Test
    public void testGetCountOfRightTurns() throws SQLException {
        int count = hsqldb.getCountOfRightTurns(1,1,10000);
        assertEquals(6712, count);
    }

    @Test
    public void testGetCountOfLeftTurns() throws SQLException {
        int count = hsqldb.getCountOfLeftTurns(1,1,10000);
        assertEquals(3288, count);
    }
}
