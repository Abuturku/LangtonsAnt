package de.andreasschick.langton.test;

import static org.junit.Assert.*;

import de.andreasschick.langton.application.Ant;
import de.andreasschick.langton.application.Position;
import de.andreasschick.langton.application.StatisticalAnalysisEngine;
import de.andreasschick.langton.gui.ZoomableCanvas;
import de.andreasschick.langton.hsqldb.HSQLDB;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class StatisticalAnalysisEngine_Test {
    private int maxStep = 10000;
    private String focusNumber = "0";
    private String focusString = "all";
    private byte numberOfAnts = 4;
    private int applicationId = 1;
    short[][][] amountOfVisits;
    private StatisticalAnalysisEngine statEngine;

    @Before
    public void setup() throws SQLException {

        List<Ant> ants = HSQLDB.getInstance().getAllAnts(applicationId, maxStep);
        ZoomableCanvas canvas = new ZoomableCanvas(10000, 10000, (byte) 4);
        canvas.initializeStateOfPixels(ants);
        amountOfVisits = ZoomableCanvas.getAmountOfVisits();
        statEngine = new StatisticalAnalysisEngine(maxStep, focusNumber, numberOfAnts, applicationId, amountOfVisits);
    }

    @Test
    public void testConstructor(){
        assertNotNull(statEngine);
    }

    @Test
    public void testGetRatioOfRightTurns(){
        double ratio = statEngine.getRatioOfRightTurns();
        assertEquals(66, ratio, 0.5);
    }

    @Test
    public void testGetRatioOfLeftTurns(){
        double ratio = statEngine.getRatioOfLeftTurns();
        assertEquals(34, ratio, 0.5);
    }

    @Test
    public void testGetTopFivePixel(){
        Position[] positions = statEngine.getTopFivePixel(focusString);
        assertNotNull(positions);

        assertEquals(5003, positions[0].getxPosition());
        assertEquals(5000, positions[0].getyPosition());
        assertEquals(105, positions[0].getAmountOfVisits());

        assertEquals(5002, positions[1].getxPosition());
        assertEquals(5001, positions[1].getyPosition());
        assertEquals(100, positions[1].getAmountOfVisits());

        assertEquals(5001, positions[2].getxPosition());
        assertEquals(5001, positions[2].getyPosition());
        assertEquals(97, positions[2].getAmountOfVisits());

        assertEquals(5000, positions[3].getxPosition());
        assertEquals(5000, positions[3].getyPosition());
        assertEquals(96, positions[3].getAmountOfVisits());

        assertEquals(4994, positions[4].getxPosition());
        assertEquals(4998, positions[4].getyPosition());
        assertEquals(92, positions[4].getAmountOfVisits());
    }

    @Test
    public void testSumUpAmountOfVisitsWithFocus(){
        int[][] sum = statEngine.sumUpAmountOfVisits(amountOfVisits, focusString);
        assertNotNull(sum);
    }

    @Test
    public void testSumUpAmountOfVisitsWithoutFocus(){
        int[][] sum = statEngine.sumUpAmountOfVisits(amountOfVisits);
        assertNotNull(sum);
    }

    @Test
    public void testGetPercentageCoverageOfPathsAllAnts(){
        double percentage = statEngine.getPercentageCoverageOfPaths(focusString);
        assertEquals(0, percentage, 0);
    }

    @Test
    public void testGetPercentageCoverageOfPathsBlackAnt(){
        double percentage = statEngine.getPercentageCoverageOfPaths("black");
        assertEquals(53.69, percentage, 0.01);
    }

    @Test
    public void testGetPercentageCoverageOfPathsRedAnt(){
        double percentage = statEngine.getPercentageCoverageOfPaths("red");
        assertEquals(54.52, percentage, 0.01);
    }

    @Test
    public void testGetPercentageCoverageOfPathsBlueAnt(){
        double percentage = statEngine.getPercentageCoverageOfPaths("blue");
        assertEquals(61.61, percentage, 0.01);
    }

    @Test
    public void testGetPercentageCoverageOfPathsOrangeAnt(){
        double percentage = statEngine.getPercentageCoverageOfPaths("orange");
        assertEquals(91.99, percentage, 0.01);
    }
}
