package de.andreasschick.langton.gui;

import de.andreasschick.langton.application.Ant;
import de.andreasschick.langton.application.Position;
import de.andreasschick.langton.hsqldb.MovementRow;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZoomableCanvas extends Canvas {

    private final Logger log = LogManager.getLogger(this.getClass());

    private final double OUTLINE_WIDTH = 0.05;
    private final double VERTICAL_OFFSET_TEXT_LESSTHAN2ANTS = 0.2;
    private final double VERTICAL_OFFSET_TEXT_MORETHAN2ANTS = 0.7;
    private final double HORIZONTAL_OFFSET_TEXT = 0.5;
    private final double FONT_SIZE = 0.2;
    private final Color OUTLINE_COLOR = Color.WHITE;
    private byte numberOfAnts = 0;
    private boolean isDrawn;

    // stateOfPixel[x][y] can have one of the states: 0 = no ants, 1 = one ant,
    // 2 = two ants, 3 = three ants, 4 = four ants visited this pixel, 5 = 1 or more ants were there, but pixel is grayed out (not focused)
    private static short[][] stateOfPixels;

    // amountOfVisits[x][xpos][ypos] holds the amount of visits of ant x at
    // position xpos, ypos
    private static short[][][] amountOfVisits;

    private int middleX;
    private int middleY;
    private int widthOfWholeCanvas;
    private int heightOfWholeCanvas;

    private int scale = 1;

    public ZoomableCanvas(int width, int height, byte numberOfAnts) {
        this.setHeight(500);
        this.setWidth(500);
        this.middleX = width / 2;
        this.middleY = height / 2;
        this.widthOfWholeCanvas = width;
        this.heightOfWholeCanvas = height;
        this.numberOfAnts = numberOfAnts;
        this.isDrawn = false;

        ZoomableCanvas.amountOfVisits = new short[numberOfAnts][width][height];

        initStateOfPixels(width, height);
        try {
            drawCanvas(width / 2 - 250, height / 2 - 250);
        }catch (RuntimeException e){

        }


    }

    private void initStateOfPixels(int width, int height) {
        stateOfPixels = new short[width][height];
        for (int arraywidth = 0; arraywidth < stateOfPixels.length - 1; arraywidth++) {
            for (int arrayheight = 0; arrayheight < stateOfPixels[arraywidth].length - 1; arrayheight++) {
                stateOfPixels[arraywidth][arrayheight] = 0;
            }
        }
    }

    void drawCanvas(int startX, int startY) {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());
        gc.setFont(new Font(scale * FONT_SIZE));

        int verticalPixel, horizontalPixel = 0;
        for (int x = 0; x < this.getWidth(); x += scale) {
            verticalPixel = 0;
            for (int y = 0; y < this.getHeight(); y += scale) {
                if (stateOfPixels[horizontalPixel + startX][verticalPixel + startY] == 5){
                    gc.setFill(new Color(0.816,0.808,0.808,0.7));
                    gc.fillRect(x, y, scale, scale);

                    gc.setFill(OUTLINE_COLOR);
                    gc.fillRect(x, y, scale, OUTLINE_WIDTH * scale);
                    gc.fillRect(x, y, OUTLINE_WIDTH * scale, scale);
                }else {
                    switch (numberOfAnts) {
                        case 0:
                            gc.setFill(Color.WHITE);
                            gc.fillRect(x, y, scale, scale);
                            break;
                        case 1:
                            if (amountOfVisits[0][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.BLACK);
                                gc.fillRect(x, y, scale, scale);

                                gc.setFill(Color.WHITE);
                                gc.fillText(String.valueOf(amountOfVisits[0][horizontalPixel + startX][verticalPixel + startY]),
                                        x + 0.1 * scale, y + 0.25 * scale);
                            }

                            gc.setFill(OUTLINE_COLOR);
                            gc.fillRect(x, y, scale, OUTLINE_WIDTH * scale);
                            gc.fillRect(x, y, OUTLINE_WIDTH * scale, scale);
                            break;
                        case 2:
                            if (amountOfVisits[0][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.BLACK);
                                gc.fillRect(x, y, scale, 0.5 * scale);

                                gc.setFill(Color.WHITE);
                                gc.fillText(String.valueOf(amountOfVisits[0][horizontalPixel + startX][verticalPixel + startY]),
                                        x + OUTLINE_WIDTH * scale, y + VERTICAL_OFFSET_TEXT_LESSTHAN2ANTS * scale);
                            }

                            if (amountOfVisits[1][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.RED);
                                gc.fillRect(x, y + (0.5 * scale), scale, 0.5 * scale);

                                gc.setFill(Color.BLACK);
                                gc.fillText(String.valueOf(amountOfVisits[1][horizontalPixel + startX][verticalPixel + startY]),
                                        x + OUTLINE_WIDTH * scale, y + VERTICAL_OFFSET_TEXT_MORETHAN2ANTS * scale);
                            }

                            gc.setFill(OUTLINE_COLOR);
                            gc.fillRect(x, y, scale, OUTLINE_WIDTH * scale);
                            gc.fillRect(x, y, OUTLINE_WIDTH * scale, scale);
                            break;
                        case 3:
                            if (amountOfVisits[0][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.BLACK);
                                gc.fillRect(x, y, scale, 0.5 * scale);

                                gc.setFill(Color.WHITE);
                                gc.fillText(String.valueOf(amountOfVisits[0][horizontalPixel + startX][verticalPixel + startY]),
                                        x + OUTLINE_WIDTH * scale, y + VERTICAL_OFFSET_TEXT_LESSTHAN2ANTS * scale);
                            }

                            if (amountOfVisits[1][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.RED);
                                gc.fillRect(x, y + (0.5 * scale), 0.5 * scale, 0.5 * scale);

                                gc.setFill(Color.BLACK);
                                gc.fillText(String.valueOf(amountOfVisits[1][horizontalPixel + startX][verticalPixel + startY]),
                                        x + OUTLINE_WIDTH * scale, y + VERTICAL_OFFSET_TEXT_MORETHAN2ANTS * scale);
                            }

                            if (amountOfVisits[2][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.BLUE);
                                gc.fillRect(x + (0.5 * scale), y + (0.5 * scale), 0.5 * scale, 0.5 * scale);

                                gc.setFill(Color.WHITE);
                                gc.fillText(String.valueOf(amountOfVisits[2][horizontalPixel + startX][verticalPixel + startY]),
                                        x + HORIZONTAL_OFFSET_TEXT * scale, y + VERTICAL_OFFSET_TEXT_MORETHAN2ANTS * scale);
                            }

                            gc.setFill(OUTLINE_COLOR);
                            gc.fillRect(x, y, scale, OUTLINE_WIDTH * scale);
                            gc.fillRect(x, y, OUTLINE_WIDTH * scale, scale);
                            break;
                        case 4:
                            if (amountOfVisits[0][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.BLACK);
                                gc.fillRect(x, y, 0.5 * scale, 0.5 * scale);

                                gc.setFill(Color.WHITE);
                                gc.fillText(String.valueOf(amountOfVisits[0][horizontalPixel + startX][verticalPixel + startY]),
                                        x + OUTLINE_WIDTH * scale, y + VERTICAL_OFFSET_TEXT_LESSTHAN2ANTS * scale);
                            }
                            if (amountOfVisits[1][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.RED);
                                gc.fillRect(x + (0.5 * scale), y, 0.5 * scale, 0.5 * scale);

                                gc.setFill(Color.BLACK);
                                gc.fillText(String.valueOf(amountOfVisits[1][horizontalPixel + startX][verticalPixel + startY]),
                                        x + HORIZONTAL_OFFSET_TEXT * scale, y + VERTICAL_OFFSET_TEXT_LESSTHAN2ANTS * scale);
                            }
                            if (amountOfVisits[2][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.BLUE);
                                gc.fillRect(x, y + (0.5 * scale), 0.5 * scale, 0.5 * scale);

                                gc.setFill(Color.WHITE);
                                gc.fillText(String.valueOf(amountOfVisits[2][horizontalPixel + startX][verticalPixel + startY]),
                                        x + OUTLINE_WIDTH * scale, y + VERTICAL_OFFSET_TEXT_MORETHAN2ANTS * scale);
                            }
                            if (amountOfVisits[3][horizontalPixel + startX][verticalPixel + startY] > 0) {
                                gc.setFill(Color.ORANGE);
                                gc.fillRect(x + (0.5 * scale), y + (0.5 * scale), 0.5 * scale, 0.5 * scale);

                                gc.setFill(Color.WHITE);
                                gc.fillText(String.valueOf(amountOfVisits[3][horizontalPixel + startX][verticalPixel + startY]),
                                        x + HORIZONTAL_OFFSET_TEXT * scale, y + VERTICAL_OFFSET_TEXT_MORETHAN2ANTS * scale);
                            }

                            gc.setFill(OUTLINE_COLOR);
                            gc.fillRect(x, y, scale, OUTLINE_WIDTH * scale);
                            gc.fillRect(x, y, OUTLINE_WIDTH * scale, scale);
                            break;
                        default:
                            gc.setFill(Color.WHITE);
                            gc.fillRect(x, y, scale, scale);
                            break;
                    }
                }
                verticalPixel++;

            }
            horizontalPixel++;
        }
        isDrawn = true;

    }

    int getScale() {
        return scale;
    }

    void setScale(int scale) {
        this.scale = scale;
    }

    int getMiddleX() {
        return middleX;
    }

    int getMiddleY() {
        return middleY;
    }

    void setMiddleX(int middleX) {
        this.middleX = middleX;
    }

    void setMiddleY(int middleY) {
        this.middleY = middleY;
    }

    public int getWidthOfWholeCanvas() {
        return widthOfWholeCanvas;
    }

    public int getHeightOfWholeCanvas() {
        return heightOfWholeCanvas;
    }

    public int getNumberOfAnts() {
        return numberOfAnts;
    }

    public static short[][][] getAmountOfVisits() {
        return amountOfVisits;
    }

    public boolean isDrawn() {
        return isDrawn;
    }

    public void unfocusPixel(short xPosition, short yPosition){
        stateOfPixels[xPosition][yPosition] = 5;
    }

    public void unfocusPixel(Position[] positions){
        for (Position pos : positions) {
            stateOfPixels[pos.getxPosition()][pos.getyPosition()] = 5;
        }
    }

    public void focusPixel(short xPosition, short yPosition){
        stateOfPixels[xPosition][yPosition] = numberOfAnts;
    }

    public void focusPixel(Position[] positions){
        for (Position pos : positions) {
            stateOfPixels[pos.getxPosition()][pos.getyPosition()] = numberOfAnts;
        }
    }

    public void setNumberOfAnts(byte numberOfAnts) {
        this.numberOfAnts = numberOfAnts;
        ZoomableCanvas.amountOfVisits = null;
        ZoomableCanvas.amountOfVisits = new short[numberOfAnts][widthOfWholeCanvas][heightOfWholeCanvas];
    }

    public void setWidthOfWholeCanvas(int widthOfWholeCanvas) {
        this.widthOfWholeCanvas = widthOfWholeCanvas;
    }

    public void setHeightOfWholeCanvas(int heightOfWholeCanvas) {
        this.heightOfWholeCanvas = heightOfWholeCanvas;
    }

    public void initializeStateOfPixels(List<Ant> ants) {
        setNumberOfAnts((byte) ants.size());
        int iteration = 0;
        for (Ant ant : ants) {
            ArrayList<MovementRow> movements = ant.getMovements();
            for (MovementRow movement : movements) {
                amountOfVisits[iteration][movement.getxPosition()][movement.getyPosition()]++;
                if (stateOfPixels[movement.getxPosition()][movement.getyPosition()] - iteration == 0) {
                    stateOfPixels[movement.getxPosition()][movement.getyPosition()] += 1;
                }
            }
            iteration++;
        }

    }


    public void reset() {
        for (int ant = 0; ant < amountOfVisits.length; ant++) {
            for (int xpos = 0; xpos < amountOfVisits[ant].length; xpos++) {
                for (int ypos = 0; ypos < amountOfVisits[ant][xpos].length; ypos++) {
                    amountOfVisits[ant][xpos][ypos] = 0;
                }
            }
        }

        initStateOfPixels(stateOfPixels.length, stateOfPixels.length);
    }

    public void unfocusAllPixel() {
        for (int x = 0; x < stateOfPixels.length; x++){
            for (int y = 0; y < stateOfPixels[x].length; y++){
                for (int ant = 0; ant < numberOfAnts; ant++){
                    if (amountOfVisits[ant][x][y] > 0){
                        stateOfPixels[x][y] = 5;
                    }
                }

            }
        }
    }

    public void redraw() {
        Platform.runLater(() -> drawCanvas(this.getMiddleX()-(int)this.getWidth()/scale/2, this.getMiddleY()-(int)this.getHeight()/scale/2));
    }
}
