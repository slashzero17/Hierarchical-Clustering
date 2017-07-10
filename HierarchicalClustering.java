import java.util.*;

/**
 * Created by Mirac Vuslat Basaran on 03-Jul-17.
 */
public class HierarchicalClustering {
    private int[][] distances;
    private int numFeatures;
    private int numSamples;
    private int maxOverHead; // Maximum overhead for each query
    private int maxDocRepetition; // Maximum number of times a document is repeated



    public HierarchicalClustering(byte[][] features, int maxOverHead, int maxDocRepetition){
        Features.setFeatures(features);
        this.maxOverHead = maxOverHead;
        this.maxDocRepetition = maxDocRepetition;
        numSamples = features.length;
        numFeatures = features[0].length;
        distances = new int[numSamples][numFeatures];
        Clusters.setClusters(new ArrayList<Cluster>());
        Clusters.setInvalidIds(new HashSet<Integer>(numSamples));
    }

    // Main method to run the clustering algorithm
    public void runClustering(){
        calculateInitialDistances(); // Setting initial clusters/distances

        // Clustering Loop
        while(true){
            Pair closestDistancePair = findClosestClusters();
            if(closestDistancePair != null){
                updateDistances(closestDistancePair);
            }
            else{
                break;
            }
        }
    }

    // Calculating initial distances between samples/clusters,
    // as well as creating the initial clusters
    private void calculateInitialDistances(){
        for(int i = 0; i < numSamples; i++){
            Clusters.getClusters().add(new Cluster(i));

            for(int j = 0; j < numSamples; j++){
                if(i == j){
                    distances[i][i] = 0;
                }
                else{
                    int dist = 0;
                    for(int k = 0; k < numFeatures; k++){
                        if(Features.getFeatures()[i][k] == 0 && Features.getFeatures()[j][k] == 1){
                            dist++;
                        }
                    }
                    distances[i][j] = dist;
                }
            }
        }
    }

    // Updating distances after merging two clusters
    private void updateDistances(Pair clusterIds){
        int cl1 = clusterIds.getNum1();
        int cl2 = clusterIds.getNum2();

        Clusters.getClusters().get(cl1).addMember(cl2);

        for(int i = 0; i < Clusters.getClusters().size(); i++){
            if(!Clusters.getInvalidIds().contains(new Integer(i))){
                int distCl1ToI = 0;
                int distIToCl1 = 0;
                for(int k = 0; k < numFeatures; k++){
                    if(Clusters.getClusters().get(cl1).getUnion()[k] == 0 && Clusters.getClusters().get(i).getUnion()[k] == 1){
                        distCl1ToI++;
                    }
                    else if(Clusters.getClusters().get(cl1).getUnion()[k] == 1 && Clusters.getClusters().get(i).getUnion()[k] == 0){
                        distIToCl1++;
                    }
                }
                distances[cl1][i] = distCl1ToI;
                distances[i][cl1] = distIToCl1;
            }
        }

        Clusters.addInvalidId(new Integer(cl2));
    }

    // TODO: Finished?
    // TODO: Overhead Check
    private Pair findClosestClusters(){
        int cl1, cl2;
        int minDist = distances[0][1];
        cl1 = 0;
        cl2 = 1;
        for(int i = 0; i < Clusters.getClusters().size(); i++){
            if(!Clusters.getInvalidIds().contains(new Integer(i))){
                for(int j = 0; j < Clusters.getClusters().size(); j++){
                    if(!Clusters.getInvalidIds().contains(new Integer(j))){
                        if(distances[i][j] < minDist){
                            minDist = distances[i][j];
                            cl1 = i;
                            cl2 = j;
                        }
                    }
                }
            }
        }

        if(maxOverHead < minDist){
            return null;
        }
        else{
            return new Pair(cl1, cl2);
        }
    }

}
