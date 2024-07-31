package backend;

import files.FileReader;
import java.io.File;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DijkstraDriver {
    public static Graph graph = new Graph();
    private static Vertex prevSearch;
    private final static String filePath = "src/main/java/files/countries.txt";

    static {
        try {
            graph = new Graph();
            File file = new File(filePath);
            FileReader.readFile(file, graph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<Vertex> getShortestPath(String sourceName, String destinationName) throws Exception {
        if (!graph.contains(sourceName))
            throw new IllegalArgumentException("Source country isn't in the file.");

        if (!graph.contains(destinationName))
            throw new IllegalArgumentException("Destination country isn't in the file.");

        Vertex source = graph.get(sourceName);
        Vertex destination = graph.get(destinationName);

        Dijkstra(source);
        List<Vertex> shortestPath =  Stream.concat(destination.getShortestPath().stream(), Stream.of(destination))
                                           .collect(Collectors.toList());
        printPath(shortestPath);
        return shortestPath;
    }

    public static void main(String[] args){
        graph.getVertices().forEach((y,x)->
                                    System.out.println(y+ ": " +x.getShortestPath().stream()
                                                .map(e->e+":"+e.getDistance()).collect(Collectors.toList())));
      }

    private static void Dijkstra(Vertex source) throws Exception {
        if (prevSearch!=source){
            if (prevSearch!=null)
                graph.resetSource();
            prevSearch = source;
            traverseAdjacentVertices(source);
        }
    }
    private static void traverseAdjacentVertices(Vertex source) {
        source.setDistance(0);
        Queue<Vertex> unknownVertices = new PriorityQueue<>();
        unknownVertices.add(source);
        while (!unknownVertices.isEmpty()) {
            Vertex V = unknownVertices.poll();
            V.getAdjacentVertices().entrySet().stream()
                    .filter(entry  -> !graph.getKnownVertices().contains(entry.getKey()))
                    .forEach(entry -> {
                        evaluateDistanceAndPath(entry.getKey(), entry.getValue(), V);
                        unknownVertices.add(entry.getKey());
                    });
            graph.getKnownVertices().add(V);
        }
    }
    private static void evaluateDistanceAndPath(Vertex adjacent, double distanceVW, Vertex V) {
        if (V.getDistance() + distanceVW < adjacent.getDistance()) {
            adjacent.setDistance(V.getDistance() + distanceVW);
            adjacent.setShortestPath(
                    Stream.concat(V.getShortestPath().stream(), Stream.of(V))
                          .collect(Collectors.toList())
            );
        }
    }
    private static void printPath(List<Vertex> path){
        StringBuilder sb  = new StringBuilder();
        path.forEach(entry-> sb.append(entry).append(": ").append(entry.getDistance()).append(" -> "));
        System.out.println(sb.substring(0, sb.length()-4));
    }
}