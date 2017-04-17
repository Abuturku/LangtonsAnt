package de.andreasschick.langton.test;


import de.andreasschick.langton.gui.GraphicalUserInterface;
import javafx.application.Application;
import org.junit.*;

import static org.junit.Assert.*;

public class GraphicalUserInterface_Test {

    private volatile boolean success = false;

    @Test
    public void testLaunchOfApplication() {
        // Wrapper thread.
        Thread thread = new Thread(() -> {
            try {
                Application.launch(GraphicalUserInterface.class);
                success = true;
            } catch(Throwable t) {
                if(t.getCause() != null && t.getCause().getClass().equals(InterruptedException.class)) {
                    success = true;
                    return;
                }
            }
        });

        thread.setDaemon(true);
        thread.start();

        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {

        }

        thread.interrupt();

        try {
            thread.join(1);
        } catch(InterruptedException ex) {

        }

        assertTrue(success);
    }
}
