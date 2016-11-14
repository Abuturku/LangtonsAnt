package de.andreasschick.langton.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class GraphicalUserInterface extends Application {

    private final Logger log = LogManager.getLogger(this.getClass());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Loading the fxml-file
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        Scene scene = new Scene(root);

        BorderPane canvasContainer = (BorderPane) scene.lookup("#canvasContainer");

        Canvas canvas = new Canvas();
        canvas.setHeight(500);
        canvas.setWidth(500);
        canvas.setId("canvas");
        canvasContainer.setCenter(canvas);


        primaryStage.setTitle("Langton's Ant");

        //Assigning Action to Refresh-Button
        Button btnRefresh = (Button) scene.lookup("#btnRefresh");
        btnRefresh.setOnAction(event -> onRefreshClicked(scene));

        //Giving choices to ChoiceBox and selecting the first entry of the choiceBox
        setUpChoiceBox(scene);

        //Setting entries for TreeView
        setUpTreeView(scene);

        drawCanvas(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawCanvas(Scene scene) {
        Canvas canvas = (Canvas) scene.lookup("#canvas");
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //Testing purposes...
        gc.setFill(Color.WHITE);
        for (double x = 0; x < canvas.getWidth(); x += 0.25){
            for (double y = 0; y < canvas.getHeight(); y += 0.25){
                gc.fillRect(x,y,x+0.25,y+0.25);
            }
        }

        gc.setFill(Color.BLACK);
        gc.fillRect(canvas.getWidth()/2-5, canvas.getHeight()/2-5, 10, 10);
    }

    private void setUpChoiceBox(Scene scene) {
        ChoiceBox choiceBoxFocus = (ChoiceBox) scene.lookup("#choiceBoxFocus");
        choiceBoxFocus.setItems(FXCollections.observableArrayList("all", "black", "red", "blue"));
        choiceBoxFocus.getSelectionModel().selectFirst();
    }

    private void setUpTreeView(Scene scene) {
        TreeView treeRules = (TreeView) scene.lookup("#treeRules");

        TreeItem<String> rootItem = new TreeItem<>("Rules");

        rootItem.getChildren().add(new TreeItem<>("RLR\t\t\t\t\tgrows chaotically"));
        rootItem.getChildren().add(new TreeItem<>("LLRR\t\t\t\t\tgrows symmetrically"));
        rootItem.getChildren().add(new TreeItem<>("LRRRRRLLR\t\t\tfills space in a square around itself"));
        rootItem.getChildren().add(new TreeItem<>("LLRRRLRLRLLR\t\t\tcreates a convoluted highway"));
        rootItem.getChildren().add(new TreeItem<>("RRLLLRLLLRRR\t\t\tcreates a filled triangle shapethat grows and moves"));
        rootItem.getChildren().add(new TreeItem<>("L2NNL1L2L1\t\t\thexagonal grid, grows circularly"));
        rootItem.getChildren().add(new TreeItem<>("L1L2NUL2L1R2\t\thexagonal grid, spiral growth"));

        //Setting ChangeListener to TreeView using Lambda-Expression
        treeRules.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<String> selectedItem = (TreeItem<String>) newValue;
            String selectedText = selectedItem.getValue().replaceAll("[ \t]*[a-z,]*", "");

        });
        //Appending rootItem to TreeView and expanding it. Also selecting first rule (RLR) as standard
        treeRules.setRoot(rootItem);
        treeRules.getRoot().setExpanded(true);
        treeRules.getSelectionModel().select(1);
    }

    private void onRefreshClicked(Scene scene){
        TextField txtNumberOfSteps = (TextField) scene.lookup("#txtNumberOfSteps");
        int numberOfSteps = -1;
        try {
            numberOfSteps = Integer.valueOf(txtNumberOfSteps.getText());
        }catch (NumberFormatException e){
            //Create alert if input on numberOfSteps is not a number
            generateAlert(Alert.AlertType.ERROR, "Error", "Input on number of steps is not a valid input", "Please enter a number");
        }

        ChoiceBox choiceBoxFocus = (ChoiceBox) scene.lookup("#choiceBoxFocus");
        String focus = choiceBoxFocus.getSelectionModel().getSelectedItem().toString();
        String selectedRule = null;

        try{
            TreeView treeRules = (TreeView) scene.lookup("#treeRules");
            selectedRule = treeRules.getSelectionModel().getSelectedItems().toString().substring(19).replaceAll("[ \t]*[a-z,]*", "").replace("]", "");

            //If rootItem is selected
            if (selectedRule.equals("R")){
                throw new Exception();
            }
        }catch (Exception e){
            //Create alert if input on treeView is not a valid rule
            generateAlert(Alert.AlertType.ERROR, "Error", "No rule selected or selected rule is invalid", "Please select a rule in Template register below");
        }

        //System.out.println("Refresh clicked with numberOfSteps of: " + numberOfSteps + " and focus of: " + focus + " and selected rule of: " + selectedRule);
        log.info("Refresh clicked with numberOfSteps of: " + numberOfSteps + " and focus of: " + focus + " and selected rule of: " + selectedRule);

    }

    private void generateAlert(Alert.AlertType alertType, String title, String headerText, String contentText){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.show();
    }
    
}
