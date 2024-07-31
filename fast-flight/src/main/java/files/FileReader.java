package files;
import backend.Graph;
import backend.Vertex;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class FileReader {

    public static void readFile(File file, Graph graph) {
       int numberOfVertices, numberOfEdges, linesCounter=0;
        try {
            Scanner scan = new Scanner(file);
            String s = scan.nextLine();
            String[] a = s.split(" ");
            if (a.length == 2) {
                numberOfVertices = Integer.parseInt(a[0].trim());
                numberOfEdges = Integer.parseInt(a[1].trim());
            } else
                return;
            while (scan.hasNext()) {
                linesCounter++;
                String line = scan.nextLine();
                if (line.isEmpty() || line.isBlank())
                    continue;
                if (numberOfVertices>0) {
                    String[] V = line.split(",");
                    if (!V[0].trim().isBlank() && !V[0].trim().isEmpty()
                            && !V[1].trim().isBlank() && !V[1].trim().isEmpty()
                            && !V[2].trim().isEmpty() && !V[2].trim().isBlank()) {
                        String countryName = V[0].trim();
                        double lat = Double.parseDouble(V[1].trim());
                        double lng = Double.parseDouble(V[2].trim());
                        Vertex vertex = new Vertex(countryName, lat, lng);
                        graph.add(vertex);
                        numberOfVertices--;
                    }
                } else if (numberOfEdges>0) {
                    String[] E = line.split(",");
                    if (!E[0].trim().isBlank() && !E[0].trim().isEmpty()
                            && !E[1].trim().isBlank() && !E[1].trim().isEmpty()) {
                        String countryName = E[0].trim();
                        String adjacent = E[1].trim();
                        Vertex v = graph.get(countryName);
                        Vertex w = graph.get(adjacent);
                        if (v != null && w != null) {
                            v.addAdjacent(w);
                            numberOfEdges--;
                        }else
                            System.out.println("Skipped line "+(linesCounter+1) +": "+countryName + ", " +adjacent);
                    }else{
                        System.out.println( "rejected"+ E[0] + " " + E[1]);
                    }
                }
                if (numberOfEdges == 0  && numberOfVertices == 0)
                    break;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. "+ e);
        } catch (NumberFormatException e1){
            System.out.println("Not numeric. "+ e1);
            System.out.println(linesCounter);
        }
    }

}
