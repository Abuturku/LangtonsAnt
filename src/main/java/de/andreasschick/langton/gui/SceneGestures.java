package de.andreasschick.langton.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class SceneGestures {

    private final Logger log = LogManager.getLogger(this.getClass());

    private static final int MAX_SCALE = 70;
    private static final int MIN_SCALE = 1;

    private DragContext sceneDragContext = new DragContext();

    private ZoomableCanvas canvas;

    SceneGestures(ZoomableCanvas canvas) {
        this.canvas = canvas;
    }

    EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            if (!event.isSecondaryButtonDown()) {
                return;
            }

            sceneDragContext.mouseAnchorX = event.getX();
            sceneDragContext.mouseAnchorY = event.getY();
        }
    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!event.isSecondaryButtonDown()) {
                return;
            }

            double offsetX = (event.getX() - sceneDragContext.mouseAnchorX);
            double offsetY = (event.getY() - sceneDragContext.mouseAnchorY);

            canvas.setMiddleX(canvas.getMiddleX()-(int)(offsetX));
            canvas.setMiddleY(canvas.getMiddleY()-(int)(offsetY));


            //Set Old mouseAnchor to current Mouse Position in the ScrollPane
            sceneDragContext.mouseAnchorX = event.getX();
            sceneDragContext.mouseAnchorY = event.getY();
            //System.out.println((canvas.getMiddleX()-canvas.getWidth()/canvas.getScale()/2)+0.5 + " " + (canvas.getMiddleY()-canvas.getHeight()/canvas.getScale()/2)+0.5);
            Platform.runLater(() -> canvas.drawCanvas((int)((canvas.getMiddleX()-canvas.getWidth()/canvas.getScale()/2)+0.5), (int)((canvas.getMiddleY()-canvas.getHeight()/canvas.getScale()/2)+0.5)));

            event.consume();
        }
    };

    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            int scale = canvas.getScale();
            int startX;
            int startY;
            if (event.getDeltaY() < 0) {
                scale -= 1;
            } else {
                scale += 1;
            }

            scale = clamp(scale, MIN_SCALE, MAX_SCALE);

            startX = canvas.getMiddleX()-(int)canvas.getWidth()/scale/2;
            startY = canvas.getMiddleY()-(int)canvas.getHeight()/scale/2;

            canvas.setScale(scale);

            Platform.runLater(() -> canvas.drawCanvas(startX, startY));

            //canvas.setPivot(f * dx, f * dy);

            event.consume();
        }
    };

    private int clamp(int value, int min, int max) {
        if (Integer.compare(value, min) < 0) {
            return min;
        }

        if (Integer.compare(value, max) > 0) {
            return max;
        }
        return value;
    }
}
