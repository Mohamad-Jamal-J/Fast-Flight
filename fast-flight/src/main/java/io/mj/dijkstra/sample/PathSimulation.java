package io.mj.dijkstra.sample;

import backend.Vertex;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapView;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;
import java.util.Queue;
import java.util.Objects;
import java.util.LinkedList;
import java.util.Iterator;


public class PathSimulation {
    private static Stage stage;
    private static List<Vertex> shortestPath;
    static StackPane root;
    static ScrollPane scrollPane;
    static VBox mainBox, movesBox;
    static Label totalDistance, addedDistance;
    static Queue<CustomLayer> queue;
    static MapView map;
    static Timeline timeline;
    static Button animationButton;
    public static void showStage(List<Vertex> shortestPath) {
        if (Objects.equals(PathSimulation.shortestPath, shortestPath)) {
            if (stage.isShowing())
                stage.requestFocus();
            else {
                stage.show();
                stage.setFullScreen(true);
            }
            return;
        }
        if (stage!=null)
            stage.close();
        PathSimulation.shortestPath = shortestPath;
        Scene scene = createScene();
        stage = new Stage();
        stage.getIcons().add(Main.icon);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setTitle("Shortest Path Simulation");
        stage.show();
    }
    private static Scene createScene(){
        StackPane box = getMainBox();
        Scene scene = new Scene(box);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("myStyles.css")).toExternalForm());
        return scene;
    }
    private static StackPane getMainBox() {
        mainBox = new VBox(40);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPrefSize(1200, 650);
        mainBox.setMaxWidth(800);

        VBox feedbackControlsBox = getFeedbackControlsBox();
        VBox mapContainer = getMapContainer();
        mainBox.getChildren().addAll(mapContainer, feedbackControlsBox);

        root = new StackPane(mainBox);
        root.setStyle("-fx-background-color: #e8e8e8;");
        StackPane.setAlignment(mainBox, Pos.CENTER);
        return root;
    }
    private static VBox getMapContainer() {
        VBox mapContainer = new VBox();
        mapContainer.setStyle("-fx-border-radius: 5px; -fx-border-width: 1px; -fx-border-color: #000000");
        mapContainer.setAlignment(Pos.CENTER);
        Label mainLabel = new Label("You can see the flight path below");
        mainLabel.setFont(new Font(16));
        mainLabel.setPadding(new Insets(5));
        map = createMapView();
        mapContainer.getChildren().addAll(mainLabel, map);
        return mapContainer;
    }
    private static VBox getFeedbackControlsBox() {
        VBox feedbackControlsBox = new VBox(12);
        feedbackControlsBox.setAlignment(Pos.CENTER);
        HBox feedbackContainer = getFeedbackContainer();
        animationButton = new Button("Animate the Path");
        animationButton.setOnAction(actionEvent -> {
            animationButton.setDisable(true);
            animateMapViewQueue();
        });
        animationButton.setPrefSize(250,50);
        animationButton.setFont(new Font(18));
        feedbackControlsBox.getChildren().addAll(feedbackContainer, animationButton);
        return feedbackControlsBox;
    }
    private static HBox getFeedbackContainer() {
        HBox feedbackContainer = new HBox(20);
        feedbackContainer.setAlignment(Pos.BOTTOM_CENTER);
        GridPane resultsGrid = getGridPane();
        VBox logsContainer = getLogsContainer();
        feedbackContainer.getChildren().addAll(resultsGrid, logsContainer);
        return feedbackContainer;
    }
    private static void addDividerLine(VBox movesBox) {
        Line divider = new Line(0, 0, 390,0);
        divider.setStyle("-fx-stroke: #402a23;");
        movesBox.getChildren().add(divider);
    }
    private static void refreshScrollView() {
        scrollPane.applyCss();
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
    private static VBox getLogsContainer() {
        VBox logsContainer = new VBox(4);
        logsContainer.setStyle("-fx-border-radius: 5px; -fx-border-width: 1px; -fx-border-color: #000000");

        logsContainer.setPrefSize(400, 134);
        logsContainer.setAlignment(Pos.TOP_CENTER);

        Label movesLogLabel = new Label("Travel Log:");
        movesLogLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-font-family: 'Lucida Console';");

        Line divider = new Line(0, 0, logsContainer.getPrefWidth()-10, 0);
        divider.setStyle("-fx-stroke: #525252;");

        logsContainer.getChildren().addAll(movesLogLabel, divider);

        movesBox = new VBox(10);
        movesBox.setAlignment(Pos.TOP_LEFT);
        movesBox.setStyle("-fx-border-color: rgba(255,255,255,0); -fx-border-width: 0px; -fx-background-color: rgba(255,255,255,0);");

        scrollPane = new ScrollPane();
        scrollPane.setContent(movesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(134);
        logsContainer.getChildren().add(scrollPane);
        return logsContainer;
    }
    private static GridPane getGridPane() {
        GridPane resultsGrid = new GridPane();
        resultsGrid.setAlignment(Pos.CENTER);
        resultsGrid.setPrefSize(350, 134);
        resultsGrid.setStyle("-fx-border-radius: 5px; -fx-border-width: 1px; -fx-border-color: #000000");

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setMinWidth(10);
        col1.setMaxWidth(165);
        col1.setHalignment(HPos.CENTER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.SOMETIMES);
        col2.setMinWidth(10);
        col2.setMaxWidth(165);

        resultsGrid.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setMinHeight(10);
        row1.setPrefHeight(30);
        row1.setVgrow(Priority.ALWAYS);

        resultsGrid.getRowConstraints().addAll(row1, row1, row1);

        Label resultLabel = new Label("Result");

        resultLabel.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(resultLabel, 2);
        resultsGrid.add(resultLabel, 0, 0);

        Label currentDistanceLabel = new Label("Added Distance: ");

        resultsGrid.add(currentDistanceLabel, 0, 1);

        Label totalDistanceLabel = new Label("Total Distance: ");
        resultsGrid.add(totalDistanceLabel, 0, 2);

        addedDistance = new Label("0 km");
        resultsGrid.add(addedDistance, 1, 1);


        totalDistance = new Label("0 km");
        resultsGrid.add(totalDistance, 1, 2);
        return resultsGrid;

    }
    private static String roundTwoDigitsString(double value){
        return Math.round(value*100)/100.0+" km";
    }
    private static MapView createMapView() {
        MapView map = new MapView();
        map.setCursor(Cursor.OPEN_HAND);
        queue = new LinkedList<>();
        map.setPrefSize(650, 400);

        Iterator<Vertex> iterator = shortestPath.iterator();
        Vertex prev = iterator.next(), next;
        CustomLayer mapLayer = new CustomLayer(prev);
        map.addLayer(mapLayer);
        queue.add(mapLayer);

        while (iterator.hasNext()){
            next = iterator.next();
            updateFeedbackBox(next, prev);
            mapLayer = new CustomLayer(prev, next);
            map.addLayer(mapLayer);
            queue.add(mapLayer);
            prev = next;

        }
        mapLayer.marker.setFill(Color.RED);
        map.setCenter(0, 0);
        map.setZoom(2);
        map.requestFocus();
        return map;
    }
    private static void updateFeedbackBox(Vertex next, Vertex prev) {
        double cumDistance = next.getDistance();
        String differenceDistance = roundTwoDigitsString(cumDistance - prev.getDistance());

        addedDistance.setText(differenceDistance);
        totalDistance.setText(roundTwoDigitsString(cumDistance));
        Label label = new Label("  "+ prev.getCountryName()+" >> "+ next.getCountryName()+": "
                +differenceDistance);
        label.setStyle("-fx-font-size: 15px; -fx-font-style: none; -fx-font-weight: normal");
        movesBox.getChildren().add(label);
        addDividerLine(movesBox);
        refreshScrollView();
    }
    private static void resetFeedbackBox() {
        totalDistance.setText("0 km");
        addedDistance.setText("0 km");
        movesBox.getChildren().clear();
    }
    private static void animateEdge(Line edge, Point2D sourcePoint, Point2D adjacentPoint){
        edge.setEndX(sourcePoint.getX());
        edge.setEndY(sourcePoint.getY());

        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.2),
                new KeyValue(edge.endXProperty(), adjacentPoint.getX()),
                new KeyValue(edge.endYProperty(), adjacentPoint.getY())
        ));
        timeline.play();
    }
    private static void animateMapViewQueue() {
        resetMapViewQueue();
        resetFeedbackBox();
        Iterator<CustomLayer> iterator = queue.iterator();
        final CustomLayer[] prev = {iterator.next()};
        map.addLayer(prev[0]);
        throbLayer(prev[0]);

        final long[] lastUpdate = {System.nanoTime()};
        final long SECOND_NANOS = 1600_000_000L;

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate[0] >= SECOND_NANOS) {
                    lastUpdate[0] = now;
                    if (iterator.hasNext()) {
                        CustomLayer next = iterator.next();
                        updateFeedbackBox(next.adjacent, next.source);
                        animateEdge(next.edge, next.sourcePoint, next.adjacentPoint);
                        map.addLayer(next);
                        throbLayer(next.marker);
                        prev[0] = next;
                    } else {
                        stop();
                        animationButton.setDisable(false);
                    }
                }
            }
        };
        timer.start();
    }
    private static void throbLayer(Node layer) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(500), layer);
        scaleUp.setToX(1.5);
        scaleUp.setToY(1.5);
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), layer);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        SequentialTransition pulse = new SequentialTransition(scaleUp, scaleDown);
        pulse.setCycleCount(2);
        pulse.play();
    }
    private static void resetMapViewQueue() {
        for (CustomLayer customLayer: queue)
            map.removeLayer(customLayer);
    }
    private static class CustomLayer extends MapLayer {
        final Circle marker;
        private final Vertex source;
        private Vertex adjacent;
        Line edge;
        private StackPane labelStack;
        Point2D sourcePoint, adjacentPoint ;
        public CustomLayer(Vertex source){
            marker = new Circle(6.5, Color.GRAY);
            this.source = source;
            getChildren().add(marker);
        }
        public CustomLayer(Vertex source,  Vertex adjacent){
            this(source);
            edge = new Line();
            edge.setStrokeWidth(3);
            getChildren().add(edge);
            this.adjacent = adjacent;
            TextFlow textFlow = getTextFlow(source, adjacent);
            labelStack = new StackPane(textFlow);
            getChildren().add(labelStack);
        }
        private static TextFlow getTextFlow(Vertex source, Vertex adjacent) {
            Text text = new Text(roundTwoDigitsString(adjacent.getDistance()- source.getDistance()));
            text.setFont(Font.font("Arial", FontWeight.BLACK, 13));

            TextFlow textFlow = new TextFlow(text);
            textFlow.setTextAlignment(TextAlignment.CENTER);

            textFlow.setMinWidth(80);
            textFlow.setStyle("-fx-background-color: rgba(255,255,255,0.52); -fx-background-radius: 15px");
            return textFlow;
        }
        @Override
        protected void layoutLayer() {
            sourcePoint = getMapPoint(source.getLatitude(), source.getLongitude());
            if (adjacent==null) {
                marker.setFill(Color.FORESTGREEN);
                marker.toFront();
                marker.setTranslateX(sourcePoint.getX());
                marker.setTranslateY(sourcePoint.getY());
            }else{
                adjacentPoint = getMapPoint(adjacent.getLatitude(), adjacent.getLongitude());

                marker.setTranslateX(adjacentPoint.getX());
                marker.setTranslateY(adjacentPoint.getY());

                edge.setStartX(sourcePoint.getX());
                edge.setStartY(sourcePoint.getY());

                edge.setEndX(adjacentPoint.getX());
                edge.setEndY(adjacentPoint.getY());


                Point2D midPoint = sourcePoint.midpoint(adjacentPoint);
                Point2D directionVector = adjacentPoint.subtract(sourcePoint);
                Point2D normalizedDirection = directionVector.normalize();
                Point2D perpendicularVector = new Point2D(-normalizedDirection.getY(), normalizedDirection.getX());
                double shiftAmount = 15.0;
                Point2D shiftedMidPoint = midPoint.add(perpendicularVector.multiply(shiftAmount));

                labelStack.setTranslateX(shiftedMidPoint.getX());
                labelStack.setTranslateY(shiftedMidPoint.getY());
            }
        }
    }
}


