package backend;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Vertex implements  Comparable<Vertex>{
    private final static double RADIUS_OF_EARTH = 6371; // km
    private String countryName;
    private double latitude;
    private double longitude;
    // minDistance
    private double distance = Double.MAX_VALUE;
    // adjacent vertex, the weight
    private Map<Vertex, Double> adjacentVertices;
    // path found by Dijkstra
    private List<Vertex> shortestPath;
    public Vertex(String countryName, double latitude, double longitude){
        setCountryName(countryName);
        setLatitude(latitude);
        setLongitude(longitude);
        setAdjacentVertices(new HashMap<>());
        setShortestPath(new LinkedList<>());
    }
    public void setCountryName(String countryName){
        if (countryName!=null)
            this.countryName = countryName;
    }
    public String getCountryName() {
        return countryName;
    }
    public void setLatitude(double latitude) {
        if (isValidLatitude(latitude))
            this.latitude = latitude;
        else
            throw new IllegalArgumentException("Latitude: "+latitude+" is out of range [-90,90].");
    }
    public void setLongitude(double longitude) {
        if (isValidLongitude(longitude))
            this.longitude = longitude;
        else
            throw new IllegalArgumentException("Longitude: "+longitude+" is out of range [-180,180].");
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public double getDistance() {
        return distance;
    }
    public void setAdjacentVertices(Map<Vertex, Double> map) {
        adjacentVertices = map;
    }
    public Map<Vertex, Double> getAdjacentVertices() {
        return adjacentVertices;
    }
    public void setShortestPath(List<Vertex> list) {
        shortestPath = list;
    }
    public List<Vertex> getShortestPath() {
        return shortestPath;
    }
    private static boolean isValidLatitude(double latitude){
        return latitude>=-90 && latitude<=90;
    }
    private static boolean isValidLongitude(double longitude){
        return longitude>=-180 && longitude<=180;
    }
    public void addAdjacent(Vertex W) {
        double distanceVW = setDistanceVW(this, W);
        if (distanceVW != -1)
            this.adjacentVertices.put(W, distanceVW);
        else
            System.out.println(W.countryName + " has invalid latitude or longitude");
    }
    private static double setDistanceVW(Vertex V, Vertex W){
        if (!isValidLatitude(V.latitude) || !isValidLongitude(V.longitude) ||
                !isValidLatitude(W.latitude) || !isValidLongitude(W.longitude))
           return -1;
        return HaversineDistance(V, W);
    }
    private static double HaversineDistance(Vertex V, Vertex W){
        double latitude1 = Math.toRadians(V.latitude),
               longitude1 = Math.toRadians(V.longitude),
               latitude2 = Math.toRadians(W.latitude),
               longitude2= Math.toRadians(W.longitude);

        double a = Math.sin((latitude1 - latitude2)/2) * Math.sin((latitude1 - latitude2)/2);
        a += Math.cos(latitude2)*Math.cos(latitude1)*Math.sin((longitude1-longitude2)/2)*Math.sin((longitude1-longitude2)/2);
        double c = 2* Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return RADIUS_OF_EARTH *c;
    }
    @Override
    public int compareTo(Vertex vertex) {
        return Double.compare(this.distance, vertex.distance);
    }
    @Override
    public String toString() {
        return countryName;
    }

}

