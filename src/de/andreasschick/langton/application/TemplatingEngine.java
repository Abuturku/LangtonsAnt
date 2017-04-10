package de.andreasschick.langton.application;

import de.andreasschick.langton.gui.GraphicalUserInterface;
import de.andreasschick.langton.gui.ZoomableCanvas;
import javafx.scene.control.Alert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplatingEngine {
    private Application application;
    private ZoomableCanvas zoomableCanvas;
    private Document doc;
    private Template template;

    public TemplatingEngine(ZoomableCanvas canvas, Application application) {
        this.zoomableCanvas = canvas;
        this.application = application;
        parseXMLToObject();
    }

    public void searchForPixelsInHalfs() {

        zoomableCanvas.unfocusAllPixel();
        int middle = zoomableCanvas.getWidthOfWholeCanvas() / 2;
        for (short xposition = 0; xposition < middle; xposition++) {
            for (short yposition = 0; yposition < middle * 2; yposition++) {

                byte visitedBy = 0;

                for (byte ant = 0; ant < zoomableCanvas.getNumberOfAnts(); ant++) {
                    if (ZoomableCanvas.getAmountOfVisits()[ant][xposition][yposition] == ZoomableCanvas.getAmountOfVisits()[ant][zoomableCanvas.getWidthOfWholeCanvas() - 1 - xposition][yposition]) {
                        visitedBy++;
                    }
                }
                if (visitedBy == zoomableCanvas.getNumberOfAnts()) {
                    zoomableCanvas.focusPixel(xposition, yposition);
                    zoomableCanvas.focusPixel((short) (zoomableCanvas.getWidthOfWholeCanvas() - 1 - xposition), yposition);
                }
            }
        }

        zoomableCanvas.redraw();
    }

    public void searchForXMLTemplate(boolean numbersAreRelevant) {
        boolean[] focusPixels = new boolean[application.getNumberOfAnts()];

        for (int antid = 0; antid < application.getNumberOfAnts(); antid++) {
            Position[] positions = template.getPositions().get(antid);
            short[][][] amountOfVisits = ZoomableCanvas.getAmountOfVisits();
            boolean focusPixel = true;
            for (Position position : positions) {
                boolean focusTemp = false;
                if (numbersAreRelevant) {
                    if (amountOfVisits[antid][position.getxPosition()][position.getyPosition()] == position.getAmountOfVisits()) {
                        focusTemp = true;
                    }
                } else {
                    if (amountOfVisits[antid][position.getxPosition()][position.getyPosition()] > 0) {
                        focusTemp = true;
                    }
                }
                focusPixel = focusTemp && focusPixel;
            }
            focusPixels[antid] = focusPixel;
        }

        boolean focus = true;
        for (boolean focusPixel : focusPixels) {
            if (!focusPixel) {
                focus = false;
            }
        }

        if (focus) {

            zoomableCanvas.unfocusAllPixel();
            Position[] positions = template.getPositions().get(0);

            zoomableCanvas.focusPixel(positions);
            zoomableCanvas.redraw();
        } else {
            GraphicalUserInterface.generateAlert(Alert.AlertType.INFORMATION, "Information", "Couldn't find template", "Couldn't find template defined in " + doc.getDocumentURI());
        }

    }

    public void searchForLongestDrift(String focus) {
        byte antid = 0;
        switch (focus) {
            case "all":
                GraphicalUserInterface.generateAlert(Alert.AlertType.INFORMATION, "Information", "Can't search for longest drift", "Please select an Ant to focus first");
                return;
            case "black":
                break;
            case "red":
                antid = 1;
                break;
            case "blue":
                antid = 2;
                break;
            case "orange":
                antid = 3;
                break;
        }

        short[][][] amountOfVisits = ZoomableCanvas.getAmountOfVisits();
        int maxStep = 0;
        DriftDirection longestDriftDirection = null;
        Position longestDriftStartPosition = null;

        for (short xposition = 2; xposition < amountOfVisits[antid].length; xposition++) {
            for (short yposition = 2; yposition < amountOfVisits[antid][0].length; yposition++) {
                if (amountOfVisits[antid][xposition][yposition] > 0) {
                    int driftStepX = 0, driftStepY = 0;
                    while (amountOfVisits[antid][xposition + driftStepX][yposition + driftStepY] >= amountOfVisits[antid][xposition + driftStepX - 1][yposition + driftStepY - 1]) {
                        driftStepX++;
                        driftStepY++;
                    }
                    if (Math.abs(driftStepX) > maxStep) {
                        maxStep = Math.abs(driftStepX);
                        longestDriftDirection = DriftDirection.DOWNRIGHT;
                        longestDriftStartPosition = new Position(xposition, yposition, 1);
                    }

                    driftStepX = 0;
                    driftStepY = 0;
                    while (amountOfVisits[antid][xposition - driftStepX][yposition - driftStepY] >= amountOfVisits[antid][xposition - driftStepX - 1][yposition - driftStepY - 1]) {
                        driftStepX--;
                        driftStepY--;
                    }
                    if (Math.abs(driftStepX) > maxStep) {
                        maxStep = Math.abs(driftStepX);
                        longestDriftDirection = DriftDirection.UPLEFT;
                        longestDriftStartPosition = new Position(xposition, yposition, 1);
                    }

                    driftStepX = 0;
                    driftStepY = 0;
                    while (amountOfVisits[antid][xposition + driftStepX][yposition - driftStepY] >= amountOfVisits[antid][xposition + driftStepX - 1][yposition - driftStepY - 1]) {
                        driftStepX++;
                        driftStepY--;
                    }
                    if (Math.abs(driftStepX) > maxStep) {
                        maxStep = Math.abs(driftStepX);
                        longestDriftDirection = DriftDirection.UPRIGHT;
                        longestDriftStartPosition = new Position(xposition, yposition, 1);
                    }

                    driftStepX = 0;
                    driftStepY = 0;
                    while (amountOfVisits[antid][xposition - driftStepX][yposition + driftStepY] >= amountOfVisits[antid][xposition - driftStepX - 1][yposition + driftStepY - 1]) {
                        driftStepX--;
                        driftStepY++;
                    }
                    if (Math.abs(driftStepX) > maxStep) {
                        maxStep = Math.abs(driftStepX);
                        longestDriftDirection = DriftDirection.DOWNLEFT;
                        longestDriftStartPosition = new Position(xposition, yposition, 1);
                    }
                }
            }
        }
        focusLongestDrift(longestDriftDirection, longestDriftStartPosition, maxStep);
    }

    private void focusLongestDrift(DriftDirection longestDriftDirection, Position longestDriftStartPosition, int maxStep) {
        int xstep = 0, ystep = 0;

        switch (longestDriftDirection){
            case DOWNLEFT:
                xstep = -1;
                ystep = 1;
                break;
            case DOWNRIGHT:
                xstep = 1;
                ystep = 1;
                break;
            case UPLEFT:
                xstep = -1;
                ystep = -1;
                break;
            case UPRIGHT:
                xstep = 1;
                ystep = -1;
                break;
        }
        Position[] driftPositions = new Position[maxStep];
        driftPositions[0] = new Position(longestDriftStartPosition.getxPosition(), longestDriftStartPosition.getyPosition(), 1);
        for (int step = 1; step < maxStep; step++){
            driftPositions[step] = new Position(longestDriftStartPosition.getxPosition()+(step*xstep),longestDriftStartPosition.getyPosition()+(step*ystep),1);
        }
        zoomableCanvas.unfocusAllPixel();
        zoomableCanvas.focusPixel(driftPositions);
        zoomableCanvas.redraw();

    }

    private void parseXMLToObject() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            switch (application.getNumberOfAnts()) {
                case 1:
                    doc = db.parse("resources/1ant.xml");
                    break;
                case 2:
                    doc = db.parse("resources/2ant.xml");
                    break;
                case 3:
                    doc = db.parse("resources/3ant.xml");
                    break;
                case 4:
                    doc = db.parse("resources/4ant.xml");
                    break;
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();

        NodeList antList = doc.getElementsByTagName("ant");


        List<Position[]> positionList = new ArrayList<>();

        for (int id = 0; id < antList.getLength(); id++) {
            Node antNode = antList.item(id);
            NodeList positionNodeList = antNode.getChildNodes();

            ArrayList<Position> positionArray = new ArrayList<>();

            for (int position = 0; position < positionNodeList.getLength(); position++) {
                Node positionNode = positionNodeList.item(position);

                if (positionNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element posElement = (Element) positionNode;

                    positionArray.add(new Position(Integer.valueOf(posElement.getElementsByTagName("xPosition").item(0).getTextContent()),
                            Integer.valueOf(posElement.getElementsByTagName("yPosition").item(0).getTextContent()),
                            Integer.valueOf(posElement.getElementsByTagName("amountOfVisits").item(0).getTextContent())));

                }
            }
            Position[] array = positionArray.toArray(new Position[positionArray.size()]);
            positionList.add(array);
        }

        template = new Template(positionList);
    }
}
