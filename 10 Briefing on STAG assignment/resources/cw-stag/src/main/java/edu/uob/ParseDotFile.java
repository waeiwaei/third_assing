package edu.uob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

public class ParseDotFile {

    //attributes to store the GameState from the XML (actions) and the DOT (entities) file
    //.DOT file (entities)
    Layout layout = new Layout();

    public static ArrayList<Hashtable<String,String>> attr = new ArrayList<>();

//    public static void main(String[] args) {
//
//        ArrayList ents = new ParseDotFile().getEntities();
//        for(int i=0; i<ents.size(); i++) {
//            System.out.println(ents.get(i));
//            System.out.println("Description " + attr.get(i).values());
//        }
//    }

    Layout getEntities(File entitiesFile){
        Parser p;
        FileReader in = null;
        File f = entitiesFile;
        p = new Parser();
        try {
            in = new FileReader(f);
            p.parse(in);

        } catch (FileNotFoundException e) {

        } catch (ParseException e2) {
        }



        ArrayList<Graph> gl = p.getGraphs();
        int clusterNum = gl.get(0).getSubgraphs().get(0).getSubgraphs().size();

        Locations location = new Locations();

        //we have gotten the cluster numbers
        for(int i = 0; i < clusterNum; i++){

            String lc = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getNodes(false).get(0).getId().getId();
            Hashtable<String,String> description = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getNodes(false).get(0).getAttributes();

            Cluster clus = new Cluster();

            layout.locations.locationsList.add(lc);
            clus.name = lc;
            clus.description = description.get("description");

            //accessing the artefacts and the furniture
            int subgraphsNum = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().size();

            ArrayList<Artefacts> artefactList = new ArrayList<>();
            ArrayList<Furniture> furnitureList = new ArrayList<>();
            ArrayList<Characters> characterList = new ArrayList<>();


            //artefacts, furniture or characters
            for(int j = 0; j < subgraphsNum; j++) {

                Characters character;
                Artefacts artefact;
                Furniture furniture;

                String idName = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getId().getId();

                if(idName.equalsIgnoreCase("characters")){
                    int nodeNum = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).size();

                    //entities within the artefacts and furniture
                    for(int k = 0; k < nodeNum; k++) {
                        String entity = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).get(k).getId().getId();
                        Hashtable <String, String> attr = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).get(k).getAttributes();

                        character = new Characters(entity, attr.get("description"));
                        Characters.listOfAllCharacterNames.add(entity);
                        characterList.add(character);

                    }


                }else if(idName.equalsIgnoreCase("artefacts")) {
                    int nodeNum = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).size();

                    //entities within the artefacts and furniture
                    for(int k = 0; k < nodeNum; k++) {
                        String entity = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).get(k).getId().getId();
                        Hashtable <String, String> attr = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).get(k).getAttributes();

                        artefact = new Artefacts(entity, attr.get("description"));

                        Artefacts.listOfAllArtefactsNames.add(entity);
                        artefactList.add(artefact);
                    }


                }else if(idName.equalsIgnoreCase("furniture")){
                    int nodeNum = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).size();

                    //entities within the artefacts and furniture
                    for(int k = 0; k < nodeNum; k++) {
                        String entity = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).get(k).getId().getId();
                        Hashtable <String, String> attr = gl.get(0).getSubgraphs().get(0).getSubgraphs().get(i).getSubgraphs().get(j).getNodes(false).get(k).getAttributes();

                        furniture = new Furniture(entity, attr.get("description"));

                        Furniture.listOfAllFurnitureNames.add(entity);
                        furnitureList.add(furniture);

                    }
                }
            }

            clus.artefacts = artefactList;
            clus.furnitures = furnitureList;
            clus.characters = characterList;
            clus.fullListEntitiesInCluster.addAll(clus.artefacts);
            clus.fullListEntitiesInCluster.addAll(clus.furnitures);
            clus.fullListEntitiesInCluster.addAll(clus.characters);

            location.clusters.add(clus);
        }

        layout.locations = location;


        ArrayList<Node> snl = gl.get(0).getNodes(false);
        ArrayList<String> ents= new ArrayList<String>();
        for(int i=0; i<snl.size(); i++) {
//            System.out.println(snl.get(i));
            if(!snl.get(i).isSubgraph()) {
                ents.add(snl.get(i).getId().getId());
                Hashtable<String, String> temp = new Hashtable<>();

                temp = snl.get(i).getAttributes();
                attr.add(temp);
            }
        }

        layout.fullEntities = ents;

        //get the paths to each entity
        ArrayList<Edge> paths = gl.get(0).getSubgraphs().get(1).getEdges();

        for(int i = 0; i < paths.size(); i++){
            String pathString = paths.get(i).toString();
            String [] pathRoutes = pathString.split("->");

            for(int j = 0; j < pathRoutes.length; j++){
                pathRoutes[j] = pathRoutes[j].replace(";", "");
                pathRoutes[j] = pathRoutes[j].replace("\s", "");
                pathRoutes[j] = pathRoutes[j].replace("\n", "");
            }

            //store the paths for each cluster location
            for(int k = 0; k < layout.locations.clusters.size(); k++){
                if(layout.locations.clusters.get(k).name.equalsIgnoreCase(pathRoutes[0])){
                    //when the clusters are the same, we want to add to that particular cluster ArrayList<String> path
                    layout.locations.clusters.get(k).paths.add(pathRoutes[1]);
                }
            }


//            Paths pat =  new Paths();
//            pat.paths.put(pathRoutes[0], pathRoutes[1]);
//
//            layout.paths.add(pat);

        }

        return layout;
    }
}



