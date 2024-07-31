package io.mj.dijkstra.sample;

import backend.Vertex;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PathDetails {
    private static Stage stage;
    private static List<Vertex> shortestPath;
    public static void showStage(List<Vertex> shortestPath) {
        if (Objects.equals(PathDetails.shortestPath, shortestPath)) {
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
        PathDetails.shortestPath = shortestPath;
        Scene scene = createScene();
        stage = new Stage();
        stage.getIcons().add(Main.icon);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setTitle("Shortest Path Details");
        stage.show();
    }

    private static Scene createScene(){
        VBox gridContainer = new VBox(15);
        gridContainer.setAlignment(Pos.CENTER);
        Label gridLabel = new Label("You can see the path details below");
        gridLabel.setFont(new Font(16));
        gridLabel.setPadding(new Insets(5));

        GridPane grid = getInfoGridReport();
        grid.setStyle("-fx-border-radius: 5px; -fx-border-width: 1px; -fx-border-color: #000000; -fx-max-width: 1200px; -fx-padding: 20px 5px");
        gridContainer.getChildren().addAll(gridLabel, grid);
        Scene scene = new Scene(gridContainer);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("myStyles.css")).toExternalForm());
        return scene;
    }
    private static GridPane getInfoGridReport() {
        GridPane mainBox = new GridPane();
        mainBox.setVgap(25);
        mainBox.setHgap(50);
        mainBox.addRow(0, new Label("Route no."));
        mainBox.addRow(0, new Label("Source Country"));
        mainBox.addRow(0, new Label("Destination Country"));
        mainBox.addRow(0, new Label("Distance (km)"));
        mainBox.addRow(0, new Label("Cumulative Distance (km)"));
        mainBox.addRow(0, new Label("Using Route"));
        mainBox.setAlignment(Pos.CENTER);

        for (Node node : mainBox.getChildren())
            node.getStyleClass().add("custom-row");

        Iterator<Vertex> iterator = shortestPath.iterator();
        Vertex source  = iterator.next();

        for (int i = 1; iterator.hasNext(); i++) {
            Vertex next = iterator.next();
            mainBox.addRow(i, new Label("#"+i));
            mainBox.addRow(i, new Label(source.getCountryName()));
            mainBox.addRow(i, new Label(next.getCountryName()));
            mainBox.addRow(i, new Label(roundTwoDigitsString(next.getDistance()-source.getDistance())));
            mainBox.addRow(i, new Label(roundTwoDigitsString(next.getDistance())));
            mainBox.addRow(i, new Label(i==1? "":("#"+(i-1))));

            source = next;
        }

        return mainBox;
    }
    private static String roundTwoDigitsString(double value){
        return ""+Math.round(value*100)/100.0;
    }
}
