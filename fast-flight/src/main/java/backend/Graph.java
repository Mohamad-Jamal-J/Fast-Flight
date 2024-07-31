package backend;

import java.util.*;

public class Graph {
    private Set<Vertex> knownVertices;
    private Map<String, Vertex> vertices;

    public Graph(){
        setKnownVertices(new HashSet<>());
        setVertices(new HashMap<>());
    }
    public Set<Vertex> getKnownVertices() {
        return knownVertices;
    }
    public void setKnownVertices(Set<Vertex> knownVertices) {
        this.knownVertices = knownVertices;
    }
    public Map<String, Vertex> getVertices() {
        return vertices;
    }
    public void setVertices(Map<String, Vertex> vertices) {
        this.vertices = vertices;
    }
    public void add(Vertex vertex){
        vertices.put(vertex.getCountryName(), vertex);
    }
    public Vertex get(String country){
        return vertices.getOrDefault(country,null);
    }
    public boolean contains(String country){
        return vertices.containsKey(country);
    }
    public void resetSource() throws Exception {
        Iterator<Vertex> iterator = knownVertices.iterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();

            vertex.setDistance(Double.MAX_VALUE);
            vertex.getAdjacentVertices().forEach((key, value) -> key.setDistance(Double.MAX_VALUE));
            vertex.setShortestPath(new LinkedList<>());

            iterator.remove();
        }

        if (!knownVertices.isEmpty())
            throw new Exception("There are still known vertices.");
    }

}
