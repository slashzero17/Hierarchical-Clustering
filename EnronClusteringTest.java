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
        long startTime, endTime, runTime, overallStartTime, overallEndTime, overallRunTimeInMs, overallRunTimeInS, overallRunTimeInM;

        int numSamples = 76577;
        int numFeatures = 30109;
        int lineNo = 0;

        int subsetSampleStartingIndex = 999;
        int subsetSampleSize = 6000;
        int subsetSampleEndingIndex = subsetSampleStartingIndex + subsetSampleSize;

        byte[][] features = new byte[subsetSampleSize][numFeatures];

        overallStartTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        System.out.println("Started reading from file.");
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
        endTime = System.currentTimeMillis();
        runTime = endTime - startTime;
        reader.close();
        System.out.println("Finished reading from file in " + runTime + " ms.");

        // Running the clustering Algorithm with *some* parameters
        int maxOverHead = 2000;
        double maxOverHeadRate = 10.0;
        startTime = System.currentTimeMillis();
        System.out.println("Started clustering.");
        HierarchicalClustering clusterer = new HierarchicalClustering(features, maxOverHead, maxOverHeadRate,10);
        clusterer.runClustering();
        endTime = System.currentTimeMillis();
        runTime = endTime - startTime;
        System.out.println("Finished clustering " + runTime + " ms.");


        // Running tests on the clusters
        startTime = System.currentTimeMillis();
        System.out.println("Started running tests.");
        ArrayList<Cluster> clusters = Clusters.getClusters();
        System.out.println("Total Number Of Clusters: " + (clusters.size() - Clusters.getInvalidIds().size()));
        System.out.println();
        for(int i = 0; i < clusters.size(); i++){
            if(!Clusters.getInvalidIds().contains(i)){
                int memberSize = clusters.get(i).getMemberSize();
                int unionSize = clusters.get(i).getUnionSize();
                System.out.println("Cluster " + i + " --> Member Size: " + memberSize + " Doc Union Size: " + unionSize);
                if(memberSize > 1){
                    System.out.println("Members of this cluster: ");
                    for(int j = 0; j < memberSize; j++){
                        int memberNo = clusters.get(i).getMembers().get(j);
                        int memberDocSize = Features.docSizes[memberNo];
                        System.out.print("\t");
                        System.out.println(i + ") Sample No: " + memberNo + " --> Doc Size: " + memberDocSize +
                                " Overhead: " + (unionSize - memberDocSize) + " Overhead Percantage: " +
                                ((double)(unionSize - memberDocSize) / memberDocSize));
                    }
                }
            }
        }

        System.out.println("");
        System.out.println("Tests on Document Repetition:");
        int[] docsClustering = new int[numFeatures];
        int[] docsNaive = new int[numFeatures];
        for(int i = 0; i < clusters.size(); i++){
            if(!Clusters.getInvalidIds().contains(i)){
                for(int j = 0; j < numFeatures; j++){
                    docsClustering[j] += clusters.get(i).getUnion()[j];
                }
            }
        }
        for(int i = 0; i < numFeatures; i++){
            for(int j = 0; j  < subsetSampleSize; j++){
                docsNaive[i] += features[j][i];
            }
        }
        int numberOfUniqueRepeatedDocs = 0;
        int totalRepClustering = 0;
        int totalRepNaive = 0;
        int documentsInSample = numFeatures;
        for(int i = 0; i < numFeatures; i++){
            if (docsClustering[i] == 0){
                documentsInSample--;
            }
            else if(docsClustering[i] == 1){

            }
            else{
                //System.out.println("Document " + i + " is stored " + docsClustering[i] + " times with clustering and " +
                //                    docsNaive[i] + " times with naive approach.");
                numberOfUniqueRepeatedDocs++;
                totalRepClustering = totalRepClustering + docsClustering[i] - 1;
                totalRepNaive = totalRepNaive + docsNaive[i] - 1;
            }
        }



        System.out.println("Testing with " + subsetSampleSize + " keywords with maxOverHead " + maxOverHead +
                " and maxOverHeadRate " + maxOverHeadRate);
        System.out.println("CLUSTERING --> Total number of unique repeated documents is " + numberOfUniqueRepeatedDocs + " out of " +
                documentsInSample + " total documents.");
        System.out.println("CLUSTERING --> Total number of repeated documents is " + totalRepClustering + " out of " +
                documentsInSample + " total documents.");
        System.out.println("CLUSTERING --> Document repetition overhead is " + ((double)(totalRepClustering) / documentsInSample) + " out of " +
                documentsInSample + " total documents.");
        System.out.println("NAIVE --> Total number of repeated documents is " + totalRepNaive + " out of " +
                documentsInSample + " total documents.");
        System.out.println("NAIVE --> Document repetition overhead is " + ((double)(totalRepNaive) / documentsInSample) + " out of " +
                documentsInSample + " total documents.");

        endTime = System.currentTimeMillis();
        runTime = endTime - startTime;
        System.out.println("Finished running tests " + runTime + " ms.");

        overallEndTime = System.currentTimeMillis();
        overallRunTimeInMs = overallEndTime - overallStartTime;
        overallRunTimeInS = overallRunTimeInMs / 1000;
        overallRunTimeInM = overallRunTimeInS / 60;
        overallRunTimeInS = overallRunTimeInS % 60;
        System.out.println();
        System.out.println("Overall running time: " + overallRunTimeInM + " minutes " + overallRunTimeInS + " s.");

    }


}
