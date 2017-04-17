package de.andreasschick.langton.gui;

import de.andreasschick.langton.application.Ant;
import de.andreasschick.langton.application.Position;
import de.andreasschick.langton.application.StatisticalAnalysisEngine;
import de.andreasschick.langton.application.TemplatingEngine;
import de.andreasschick.langton.hsqldb.HSQLDB;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GraphicalUserInterface extends Application {

    private final Logger log = LogManager.getLogger(this.getClass());
    private StatisticalAnalysisEngine statAnalyser;
    private TemplatingEngine templatingEngine;
    private de.andreasschick.langton.application.Application application;
    private final short OFFSET_APPLICATIONID = 1;
    public ProgressBar progressBar;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	log.info("Starting GUI");
        //Loading the FXML-file
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));

        Scene scene = new Scene(root);

        application = HSQLDB.getInstance().getApplication(OFFSET_APPLICATIONID);

        ScrollPane canvasContainer = (ScrollPane) scene.lookup("#canvasContainer");

        ZoomableCanvas zoomableCanvas = new ZoomableCanvas(application.getTableauSize(), application.getTableauSize(), application.getNumberOfAnts());
        zoomableCanvas.setId("#canvas");

        SceneGestures sceneGestures = new SceneGestures(zoomableCanvas);
        zoomableCanvas.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        zoomableCanvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        zoomableCanvas.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        canvasContainer.setContent(zoomableCanvas);
        //resetGrid(zoomableCanvas);

        primaryStage.setTitle("Langton's Ant");
        
        setUpApplicationChoiceBox(scene);

        //Assigning Action to Refresh-Button
        Button btnRefresh = (Button) scene.lookup("#btnRefresh");
        btnRefresh.setOnAction(event -> onRefreshClicked(scene, zoomableCanvas));

        Button centerGridButton = (Button) scene.lookup("#centerGridButton");
        centerGridButton.setOnAction(event -> centerGrid(zoomableCanvas));

        Button resetGridButton = (Button) scene.lookup("#resetGridButton");
        resetGridButton.setOnAction(event -> resetGrid(zoomableCanvas));
        
        ChoiceBox<String> cbApplication = (ChoiceBox<String>)scene.lookup("#cbApplication");
        cbApplication.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
            int applicationId = new_value.intValue()+OFFSET_APPLICATIONID;
            try {
                application = HSQLDB.getInstance().getApplication(applicationId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            zoomableCanvas.setWidthOfWholeCanvas(application.getTableauSize());
            zoomableCanvas.setHeightOfWholeCanvas(application.getTableauSize());
            zoomableCanvas.setNumberOfAnts(application.getNumberOfAnts());
            setUpChoiceBox(scene);
        });



        TreeView<String> treeView = (TreeView<String>) scene.lookup("#treeRules");
        treeView.setOnDragDetected(event -> {
            Dragboard db = treeView.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(treeView.getSelectionModel().getSelectedIndex()));
            db.setContent(content);
            event.consume();
        });

        zoomableCanvas.setOnDragOver(event -> {
            if(event.getGestureSource() == treeView && event.getDragboard().hasString()){
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });

        ChoiceBox<String> choiceBoxFocus = (ChoiceBox<String>) scene.lookup("#choiceBoxFocus");

        zoomableCanvas.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString() && zoomableCanvas.isDrawn()){
                switch (db.getString()){
                    case "1":
                        log.info("Searching for Pixels in halfs of canvas");
                        templatingEngine.searchForPixelsInHalfs();
                        break;
                    case "2":
                        log.info("Searching for template, defined in XML without numbers");
                        templatingEngine.searchForXMLTemplate(false);
                        break;
                    case "3":
                        log.info("Searching for template, defined in XML with numbers");
                        templatingEngine.searchForXMLTemplate(true);
                        break;
                    case "4":
                        log.info("Searching for longest drift to edges");
                        templatingEngine.searchForLongestDrift(choiceBoxFocus.getSelectionModel().getSelectedItem());
                        break;
                    default:
                        break;
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        
        //Giving choices to ChoiceBox and selecting the first entry of the choiceBox
        setUpChoiceBox(scene);

        //Setting entries for TreeView
        setUpTreeView(scene);

        progressBar = (ProgressBar) scene.lookup("#progressBar");


        //primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setUpApplicationChoiceBox(Scene scene) {
		ChoiceBox<String> cbApplication = (ChoiceBox<String>)scene.lookup("#cbApplication");
		List<de.andreasschick.langton.application.Application> applList = null;
		
		try {
			applList = HSQLDB.getInstance().getAllApplications();
		} catch (SQLException e) {
			log.info(e.getMessage());
		}
		
		List<String> items = new ArrayList<>();
		
		for (de.andreasschick.langton.application.Application appl : applList) {
			items.add(appl.getId() + " - " + appl.getMovementRule() + " - " + appl.getNumberOfAnts() + " Ant" + (appl.getNumberOfAnts() > 1 ? "s" : "") + " - Grid: " + appl.getTableauSize() + "x" + appl.getTableauSize());
		}
		
		cbApplication.setItems(FXCollections.observableArrayList(items));
		cbApplication.getSelectionModel().selectFirst();
	}

	@SuppressWarnings("unchecked")
    private void setUpChoiceBox(Scene scene) {
        ChoiceBox<String> choiceBoxFocus = (ChoiceBox<String>) scene.lookup("#choiceBoxFocus");
        List<String> items = new ArrayList<>();
        items.add("all");
        if (application.getNumberOfAnts() >= 1) {
            items.add("black");
        }
        if (application.getNumberOfAnts() >= 2) {
            items.add("red");
        }
        if (application.getNumberOfAnts() >= 3) {
            items.add("blue");
        }
        if (application.getNumberOfAnts() >= 4) {
            items.add("orange");
        }
        choiceBoxFocus.setItems(FXCollections.observableArrayList(items));
        choiceBoxFocus.getSelectionModel().selectFirst();
    }

    @SuppressWarnings("unchecked")
    private void setUpTreeView(Scene scene) {
        TreeView<String> treeRules = (TreeView<String>) scene.lookup("#treeRules");

        TreeItem<String> rootItem = new TreeItem<>("Templates");

        rootItem.getChildren().add(new TreeItem<>("Split grid into two equal parts and detect symmetries"));
        rootItem.getChildren().add(new TreeItem<>("XML-defined template, amount of visits not relevant"));
        rootItem.getChildren().add(new TreeItem<>("XML-defined template, amount of visits relevant"));
        rootItem.getChildren().add(new TreeItem<>("Longest drift to edges"));

        //Setting ChangeListener to TreeView using Lambda-Expression
        treeRules.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<String> selectedItem = newValue;
            //String selectedText = selectedItem.getValue().replaceAll("[ \t]*[a-z,]*", "");

        });
        //Appending rootItem to TreeView and expanding it.
        treeRules.setRoot(rootItem);
        treeRules.getRoot().setExpanded(true);
        treeRules.getSelectionModel().select(1);
    }

    @SuppressWarnings("unchecked")
    private void onRefreshClicked(Scene scene, ZoomableCanvas zoomableCanvas) {
    	ChoiceBox<String> cbApplication = (ChoiceBox<String>) scene.lookup("#cbApplication");
    	int selectedIndex = cbApplication.getSelectionModel().getSelectedIndex();
    	log.info("-----------------------------------------------------------");
    	log.info("Selected Application with applicationId = " + selectedIndex);

    	progressBar.setProgress(0.2f);


        TextField txtNumberOfSteps = (TextField) scene.lookup("#txtNumberOfSteps");
        int numberOfSteps = -1;
        try {
            numberOfSteps = Integer.valueOf(txtNumberOfSteps.getText());
        } catch (NumberFormatException e) {
            //Create alert if input on numberOfSteps is not a number
            generateAlert(Alert.AlertType.ERROR, "Error", "Input on number of steps is not a valid input", "Please enter a number");
        }
        
        ChoiceBox<String> choiceBoxFocus = (ChoiceBox<String>) scene.lookup("#choiceBoxFocus");
        String focus = choiceBoxFocus.getSelectionModel().getSelectedItem();
        String selectedRule;
        log.info("Selected Focus: " + focus);
        progressBar.setProgress(0.3f);
        try {
            TreeView<String> treeRules = (TreeView<String>) scene.lookup("#treeRules");
            selectedRule = treeRules.getSelectionModel().getSelectedItems().toString().substring(19).replaceAll("[ \t]*[a-z,]*", "").replace("]", "");

            //If rootItem is selected
            if (selectedRule.equals("R")) {
                throw new Exception();
            }
        } catch (Exception e) {
            //Create alert if input on treeView is not a valid rule
            generateAlert(Alert.AlertType.ERROR, "Error", "No rule selected or selected rule is invalid", "Please select a rule in Template register below");
        }

        progressBar.setProgress(0.4f);
        try {
            final List<Ant> ants = HSQLDB.getInstance().getAllAnts(application.getId(), numberOfSteps);

            Platform.runLater(() -> setUpAndDrawCanvas(zoomableCanvas, ants));
        } catch (SQLException e) {
            generateAlert(Alert.AlertType.ERROR, "Error", "Couldn't get all Ants for Application 1", "Please try again");
        }



        final int finalNumberOfSteps = numberOfSteps;
        Platform.runLater(() -> runStatisticalAnalysis(scene, focus, finalNumberOfSteps));


        Platform.runLater(() -> {
            templatingEngine = new TemplatingEngine(zoomableCanvas, application);
            progressBar.setProgress(progressBar.getProgress() + 0.1f);
        });
    }

    private void setUpAndDrawCanvas(ZoomableCanvas zoomableCanvas, List<Ant> ants){
        log.info("Setting up and drawing Canvas");
        zoomableCanvas.reset();
        zoomableCanvas.initializeStateOfPixels(ants);
        zoomableCanvas.drawCanvas(zoomableCanvas.getMiddleX() - (int) zoomableCanvas.getWidth() / zoomableCanvas.getScale() / 2,
                zoomableCanvas.getMiddleY() - (int) zoomableCanvas.getHeight() / zoomableCanvas.getScale() / 2);
        progressBar.setProgress(progressBar.getProgress() + 0.3f);
    }

    private void runStatisticalAnalysis(Scene scene, String focus, int numberOfSteps){
        log.info("Running statistical Analysis");
        try {
            ChoiceBox<String> choiceBox = (ChoiceBox<String>) scene.lookup("#choiceBoxFocus");
            statAnalyser = new StatisticalAnalysisEngine(numberOfSteps, String.valueOf(choiceBox.getSelectionModel().getSelectedIndex()), application.getNumberOfAnts(), application.getId());
            setUpRatioLeftAndRightTurns(scene);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setUpTopFivePixels(scene, focus);
        setUpPercentageCoverage(scene, focus);
        setUpTopThreeHotspots(scene);
        progressBar.setProgress(progressBar.getProgress() + 0.2f);
    }

    private void setUpTopFivePixels(Scene scene, String focus) {
        final Position[] topFive = statAnalyser.getTopFivePixel(focus);
        
        BarChart<String, Number> barChart = (BarChart<String, Number>) scene.lookup("#histogram");

        barChart.setLegendVisible(false);
        barChart.setAnimated(false);

        barChart.getXAxis().setLabel("Position");
        barChart.getYAxis().setLabel("Visits");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Visits");
        for (int i = 0; i < 5; i++) {
            series.getData().add(new XYChart.Data<>((topFive[i].getxPosition() + "," + topFive[i].getyPosition()), topFive[i].getAmountOfVisits()));
        }

        barChart.getData().setAll(series);
        barChart.setBarGap(1);

    }

    private void setUpPercentageCoverage(Scene scene, String focus) {
        double percentage = statAnalyser.getPercentageCoverageOfPaths(focus);
        TextField txtFieldPercentage = (TextField) scene.lookup("#txtPercentCovOfPaths");

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        txtFieldPercentage.setText(df.format(percentage) + "%");
    }

    private void setUpTopThreeHotspots(Scene scene) {
        Position[] topFive = statAnalyser.getTopFivePixel("all");
        TextField txtTopThreeHotspots = (TextField) scene.lookup("#txtTopThreeHotspots");
        txtTopThreeHotspots.setText("(" + topFive[0].getxPosition() + "," + topFive[0].getyPosition() + ")  "
                + "(" + topFive[1].getxPosition() + "," + topFive[1].getyPosition() + ")  "
                + "(" + topFive[2].getxPosition() + "," + topFive[2].getyPosition() + ")");
    }

    public static void generateAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.show();
    }

    private void resetGrid(ZoomableCanvas canvas) {
        canvas.setScale(1);
        canvas.setMiddleX(canvas.getWidthOfWholeCanvas() / 2);
        canvas.setMiddleY(canvas.getHeightOfWholeCanvas() / 2);
        Platform.runLater(() -> canvas.drawCanvas(canvas.getWidthOfWholeCanvas() / 2 - (int) canvas.getWidth() / 2, canvas.getHeightOfWholeCanvas() / 2 - (int) canvas.getHeight() / 2));
    }

    private void centerGrid(ZoomableCanvas canvas) {
        double scale = canvas.getScale();
        canvas.setMiddleX(canvas.getWidthOfWholeCanvas() / 2);
        canvas.setMiddleY(canvas.getHeightOfWholeCanvas() / 2);
        Platform.runLater(() -> canvas.drawCanvas(canvas.getWidthOfWholeCanvas() / 2 - (int) (canvas.getWidth() / scale / 2), canvas.getHeightOfWholeCanvas() / 2 - (int) (canvas.getHeight() / scale / 2)));
    }

    private void setUpRatioLeftAndRightTurns(Scene scene) throws SQLException {
        PieChart pieChart = (PieChart) scene.lookup("#piechart");
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Right-turns", statAnalyser.getRatioOfRightTurns()),
                new PieChart.Data("Left-turns", statAnalyser.getRatioOfLeftTurns())
        );
        pieChart.setData(pieChartData);
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(false);
        pieChart.setStartAngle(90);

        Label lblRightTurns = (Label) scene.lookup("#pieChartCaptionRightTurns");
        Label lblLeftTurns = (Label) scene.lookup("#pieChartCaptionLeftTurns");

        lblRightTurns.setText(String.valueOf(Math.round(statAnalyser.getRatioOfRightTurns())) + "%");
        lblRightTurns.setVisible(true);
        lblLeftTurns.setText(String.valueOf(Math.round(statAnalyser.getRatioOfLeftTurns())) + "%");
        lblLeftTurns.setVisible(true);
    }

    private void templatingDragAndDropHandler(Scene scene){
        TreeView<String> treeView = (TreeView<String>) scene.lookup("#treeRules");
    }
}
