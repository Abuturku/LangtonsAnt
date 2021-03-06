package de.andreasschick.langton.application;

import de.andreasschick.langton.gui.ZoomableCanvas;
import de.andreasschick.langton.hsqldb.HSQLDB;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import java.sql.SQLException;

public class StatisticalAnalysisEngine {
    private int countOfRightTurns;
    private int countOfLeftTurns;
    private short[][][] amountOfVisits;

    public StatisticalAnalysisEngine(int maxStep, String focus, byte numberOfAnts, int applicationId) throws SQLException {
        if (focus.equals("0")) {
            this.countOfLeftTurns = 0;
            this.countOfRightTurns = 0;
            for (int i = 1; i <= numberOfAnts; i++) {
                this.countOfLeftTurns += HSQLDB.getInstance().getCountOfLeftTurns(i, applicationId, maxStep);
                this.countOfRightTurns += HSQLDB.getInstance().getCountOfRightTurns(i, applicationId, maxStep);
            }
        } else {
            System.out.println(Integer.valueOf(focus));
            this.countOfLeftTurns = HSQLDB.getInstance().getCountOfLeftTurns(
                    Integer.valueOf(focus),applicationId, maxStep);
            this.countOfRightTurns = HSQLDB.getInstance().getCountOfRightTurns(
                    Integer.valueOf(focus), applicationId, maxStep);
        }

        if (amountOfVisits != null){
            this.amountOfVisits = ZoomableCanvas.getAmountOfVisits();
        }
    }

    public StatisticalAnalysisEngine(int maxStep, String focus, byte numberOfAnts, int applicationId, short[][][] amountOfVisits) throws SQLException {
        this(maxStep, focus, numberOfAnts, applicationId);
        this.amountOfVisits = amountOfVisits;
    }

    public double getRatioOfRightTurns() {
        return ((double) countOfRightTurns / (countOfLeftTurns + countOfRightTurns)) * 100.0;
    }

    public double getRatioOfLeftTurns() {
        return ((double) countOfLeftTurns / (countOfLeftTurns + countOfRightTurns)) * 100.0;
    }

    public Position[] getTopFivePixel(String focus) {
        Position[] topFive = new Position[]{new Position(0, 0, 0), new Position(0, 0, 0), new Position(0, 0, 0),
                new Position(0, 0, 0), new Position(0, 0, 0)};

        int[][] sum = sumUpAmountOfVisits(ZoomableCanvas.getAmountOfVisits(), focus);

        for (short xPosition = 0; xPosition < sum.length; xPosition++) {
            for (short yPosition = 0; yPosition < sum[xPosition].length; yPosition++) {
                short number = (short) sum[xPosition][yPosition];
                if (number > topFive[topFive.length - 1].getAmountOfVisits()) {
                    short isLower = 0;
                    for (short index = 0; index < topFive.length; index++) {
                        if (topFive[index].getAmountOfVisits() < number) {
                            isLower = index;
                            break;
                        }
                    }
                    addNumberAtPos(new Position(xPosition, yPosition, number), isLower, topFive);
                }
            }
        }
        return topFive;
    }

    private void addNumberAtPos(Position position, int pos, Position[] topFive) {

        Position tempPos = topFive[pos];
        topFive[pos] = position;
        if (pos + 1 < topFive.length) {
            addNumberAtPos(tempPos, pos + 1, topFive);
        }

    }

    public int[][] sumUpAmountOfVisits(short[][][] amountOfVisits, String focus) {
        int[][] sum = new int[amountOfVisits[0].length][amountOfVisits[0][0].length];
        int start = 0, end = amountOfVisits.length;

        if (focus.equals("all")) {
            start = 0;
            end = amountOfVisits.length;
        } else if (focus.equals("black")) {
            start = 0;
            end = 1;
        } else if (focus.equals("red")) {
            start = 1;
            end = 2;
        } else if (focus.equals("blue")) {
            start = 2;
            end = 3;
        } else if (focus.equals("orange")) {
            start = 3;
            end = 4;
        }

        for (int ant = start; ant < end; ant++) {
            for (int xPosition = 0; xPosition < amountOfVisits[ant].length; xPosition++) {
                for (int yPosition = 0; yPosition < amountOfVisits[ant][xPosition].length; yPosition++) {
                    sum[xPosition][yPosition] += amountOfVisits[ant][xPosition][yPosition];
                }
            }
        }

        return sum;
    }

    public int[][] sumUpAmountOfVisits(short[][][] amountOfVisits) {
        int[][] sum = new int[amountOfVisits[0].length][amountOfVisits[0][0].length];
        int start = 0, end = amountOfVisits.length;

        for (int ant = start; ant < end; ant++) {
            for (int xPosition = 0; xPosition < amountOfVisits[ant].length; xPosition++) {
                for (int yPosition = 0; yPosition < amountOfVisits[ant][xPosition].length; yPosition++) {
                    sum[xPosition][yPosition] += amountOfVisits[ant][xPosition][yPosition];
                }
            }
        }

        return sum;
    }

    public double getPercentageCoverageOfPaths(String focus) {
        double percentage = 0.0;
        int antIndex = 0;

        if (focus.equals("all")){
            return percentage;
        }else if (focus.equals("black")){
            antIndex = 0;
        }else if (focus.equals("red")){
            antIndex = 1;
        }else if (focus.equals("blue")){
            antIndex = 2;
        }else if (focus.equals("orange")){
            antIndex = 3;
        }

        double focusedAnt = 0;
        double allAnts = 0;

        int[][] sum = sumUpAmountOfVisits(ZoomableCanvas.getAmountOfVisits());

        for (int x = 0; x < sum.length; x++){
            for (int y = 0; y < sum[x].length; y++){
                if (ZoomableCanvas.getAmountOfVisits()[antIndex][x][y] > 0){
                    focusedAnt++;
                }
                if (sum[x][y] > 0){
                    allAnts++;
                }
            }
        }

        percentage = focusedAnt/allAnts*100.0d;

        return percentage;
    }

}
