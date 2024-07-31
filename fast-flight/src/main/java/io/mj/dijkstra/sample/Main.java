package io.mj.dijkstra.sample;

import backend.DijkstraDriver;
import backend.Vertex;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.*;
import java.util.stream.Collectors;


public class Main extends Application {

    public static List<Vertex> shortestPath;
    private static Label alertLabel;
    private static ComboBox<Vertex> sourceCombo;
    private static ComboBox<Vertex> destinationCombo;
    private static Button detailsButton, simulationButton;
    private final static ObservableList<Vertex> countriesList = populateSourceOptions();
    final static Image icon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("myIcon.png")));


    @Override
    public void start(Stage stage) {
        VBox mainContent = getMainContentBox();
        Scene mainScene = new Scene(mainContent, 1200.0, 800.0);
        mainScene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("myStyles.css")).toExternalForm());
        stage.getIcons().add(icon);
        stage.setScene(mainScene);
        stage.setFullScreen(true);
        stage.setTitle("Fast Flight");
        stage.setFullScreenExitHint("");
        stage.show();
    }

    private static VBox getMainContentBox() {
        VBox mainContent = new VBox(15);
        mainContent.setAlignment(Pos.CENTER);
        Label welcome = new Label("Fast Flight.");
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        welcome.setPadding(new Insets(30));
        Label instructionLabel = new Label("Choose the desired source and destination countries.");
        HBox comboBoxes = initializeComboBoxes();
        alertLabel = new Label("");
        alertLabel.setStyle("-fx-text-fill: firebrick;  -fx-font-size: 16; -fx-font-family: 'Lucia Console';");
        HBox buttonsBox = getButtonsBox();
        mainContent.getChildren().addAll(welcome, instructionLabel, comboBoxes, alertLabel, buttonsBox);
        return mainContent;
    }
    private static HBox initializeComboBoxes() {
        HBox comboBoxes = new HBox(35);
        HBox fromBox = new HBox(8);
        fromBox.setAlignment(Pos.CENTER);

        Label from = new Label("From: ");
        from.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        sourceCombo = new ComboBox<>();
        sourceCombo.setPromptText("Source");
        sourceCombo.setItems(countriesList);
        fromBox.getChildren().addAll(from, sourceCombo);

        HBox toBox = new HBox(8);
        toBox.setAlignment(Pos.CENTER);
        Label to = new Label("To: ");
        to.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        destinationCombo = new ComboBox<>();
        destinationCombo.setPromptText("Destination");
        toBox.getChildren().addAll(to, destinationCombo);

        comboBoxes.getChildren().addAll(fromBox, toBox);
        comboBoxes.setAlignment(Pos.CENTER);

        sourceCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == newValue)
                return;

            showExternalButtons(false);
            ObservableList<Vertex> adjacentList = populateDestinationOptions(newValue);
            destinationCombo.setItems(adjacentList);
            destinationCombo.getSelectionModel().clearAndSelect(0);
            showAlert(adjacentList.isEmpty()? "Unfortunately, there are no flights leaving this country.":"", true);

        });

        destinationCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == newValue)
                return;

            showExternalButtons(false);
        });
        return comboBoxes;
    }
    private static HBox getButtonsBox() {
        HBox buttonsBox = new HBox(10);
        Button startButton = new Button("Find Flights");
        detailsButton = new Button("Show Path Details");
        simulationButton = new Button("Simulate Path");
        showExternalButtons(false);

        buttonsBox.getChildren().addAll(startButton, detailsButton, simulationButton);
        buttonsBox.setAlignment(Pos.CENTER);

        startButton.setOnAction(action->{
            Vertex source = sourceCombo.getSelectionModel().getSelectedItem() ;
            Vertex destination = destinationCombo.getSelectionModel().getSelectedItem() ;

            if (source==null) {
                showAlert("Please select a source country.", true);
                showExternalButtons(false);
                return;
            }
            if (destination==null) {
                showAlert("Please select a destination country.", true);
                showExternalButtons(false);
                return;
            }

            try {
                shortestPath = DijkstraDriver.getShortestPath(source.getCountryName(), destination.getCountryName());
                showAlert("Shortest Path Found ✅", false);
                showExternalButtons(true);
            } catch (Exception e) {
                shortestPath = null;
                showAlert("Something went wrong.", true);
                showExternalButtons(false);
            }
        });

        detailsButton.setOnAction(action-> PathDetails.showStage(shortestPath));

        simulationButton.setOnAction(action-> PathSimulation.showStage(shortestPath));
        return buttonsBox;
    }
    private static ObservableList<Vertex> populateSourceOptions(){
        return FXCollections.observableArrayList(DijkstraDriver.graph.getVertices().values().stream()
                .sorted(Comparator.comparing(Vertex::getCountryName))
                .collect(Collectors.toList()));
    }
    private static ObservableList<Vertex> populateDestinationOptions(Vertex newValue) {
        Set<Vertex> set = findAllPossibleRouteFromSource(newValue);
        return FXCollections.observableArrayList(set.stream()
                .sorted(Comparator.comparing(Vertex::getCountryName))
                .collect(Collectors.toList()));
    }

    private static Set<Vertex> findAllPossibleRouteFromSource(Vertex newValue) {
        Set<Vertex> set  = new HashSet<>();
        Queue<Vertex> adjacent = new PriorityQueue<>();
        adjacent.add(newValue);
        while (!adjacent.isEmpty()) {
            Vertex V = adjacent.poll();
            V.getAdjacentVertices().keySet().stream()
                    .filter(entry  -> !set.contains(entry))
                    .forEach(entry -> {
                        set.add(entry);
                        adjacent.add(entry);
                    });
        }
        return set;
    }

    private static void showExternalButtons(boolean show){
        detailsButton.setVisible(show);
        simulationButton.setVisible(show);
        detailsButton.setManaged(show);
        simulationButton.setManaged(show);
    }
    private static void showAlert(String message, boolean isError){
        alertLabel.setText(message);
        alertLabel.setTextFill(isError? Color.FIREBRICK:Color.FORESTGREEN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
