import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Mirac Vuslat Basaran on 07-Jul-17.
 */
public class EnronClusteringTest {

    public static void main(String[] args) throws IOException{
        String enronFeatureFileName = "C:\\Users\\Mirac Vuslat Basaran\\IdeaProjects\\Clustering\\features.txt";
        BufferedReader reader = new BufferedReader(new FileReader(enronFeatureFileName));

        String line = null;
        Scanner scan = null;


        int numSamples = 76577;
        int numFeatures = 30109;
        int lineNo = 0;

        int subsetSampleStartingIndex = 999;
        int subsetSampleSize = 1000;
        int subsetSampleEndingIndex = subsetSampleStartingIndex + subsetSampleSize;

        byte[][] features = new byte[subsetSampleSize][numFeatures];

        while((line = reader.readLine()) != null && lineNo < subsetSampleEndingIndex){
            if(lineNo >= subsetSampleStartingIndex){
                int index = 0;
                scan = new Scanner(line);
                scan.useDelimiter(" ");
                while(scan.hasNext()){
                    String data = scan.next();
                    features[lineNo - subsetSampleStartingIndex][index++] = Byte.parseByte(data);
                }
            }

            lineNo++;
        }

        reader.close();


        // Running the clustering Algorithm with *some* parameters
        HierarchicalClustering clusterer = new HierarchicalClustering(features, 300, 10);
        clusterer.runClustering();


        // Running tests on the clusters
        ArrayList<Cluster> clusters = Clusters.getClusters();
        System.out.println("Total Number Of Clusters: " + (clusters.size() - Clusters.getInvalidIds().size()));
        System.out.println();
        for(int i = 0; i < clusters.size(); i++){
            if(!Clusters.getInvalidIds().contains(i)){
                System.out.println("Cluster " + i + " --> Member Size: " + clusters.get(i).getMemberSize() + " Doc Union Size: " + clusters.get(i).getDocSize());
            }
        }
    }

}
