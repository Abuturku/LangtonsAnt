package de.andreasschick.langton.application;

import de.andreasschick.langton.gui.ZoomableCanvas;
import javafx.scene.Scene;

public class TemplatingEngine {
    Scene scene;
    Application application;
    ZoomableCanvas zoomableCanvas;

    public TemplatingEngine(Scene scene){
        this.scene = scene;
        this.zoomableCanvas = (ZoomableCanvas) scene.lookup("#canvas");
    }

    public void searchForPixelsInHalfs(){

    }

    public void searchForXMLTemplateWithNumbers(){

    }

    public void searchForXMLTemplate(){

    }

    public void searchForLongestDrift(){

    }
}
