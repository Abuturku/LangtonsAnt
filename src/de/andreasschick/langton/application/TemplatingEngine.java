package de.andreasschick.langton.application;

import de.andreasschick.langton.gui.ZoomableCanvas;
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
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Document doc;
    private Template template;

    public TemplatingEngine(ZoomableCanvas canvas, Application application){
        this.zoomableCanvas = canvas;
        this.application = application;
        parseXMLToObject();
    }

    public void searchForPixelsInHalfs(){

        zoomableCanvas.unfocusAllPixel();
        int middle = zoomableCanvas.getWidthOfWholeCanvas()/2;
        for (short xposition = 0; xposition < middle; xposition++){
            for (short yposition = 0; yposition < middle*2; yposition++){

                byte visitedBy = 0;

                for (byte ant = 0; ant < zoomableCanvas.getNumberOfAnts(); ant++){
                    if (ZoomableCanvas.getAmountOfVisits()[ant][xposition][yposition] == ZoomableCanvas.getAmountOfVisits()[ant][zoomableCanvas.getWidthOfWholeCanvas()-1-xposition][yposition]){
                        visitedBy++;
                    }
                }
                if (visitedBy == zoomableCanvas.getNumberOfAnts()){
                    zoomableCanvas.focusPixel(xposition,yposition);
                    zoomableCanvas.focusPixel((short)(zoomableCanvas.getWidthOfWholeCanvas()-1-xposition), yposition);
                }
            }
        }

        zoomableCanvas.redraw();
    }

    public void searchForXMLTemplateWithNumbers(){

    }

    public void searchForXMLTemplate(){

    }

    public void searchForLongestDrift(){

    }

    private void parseXMLToObject(){
        dbf = DocumentBuilderFactory.newInstance();

        try {
            db = dbf.newDocumentBuilder();
            switch (application.getNumberOfAnts()){
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
        }catch (SAXException | IOException | ParserConfigurationException e){
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
