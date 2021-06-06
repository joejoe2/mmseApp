package com.joejoe2.surveyapp.util;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphTools {
    public static List<Graph<CanvasVertex, DefaultEdge>> findPurePentagonsInGraph(Graph<CanvasVertex, DefaultEdge> graph){
        List<Graph<CanvasVertex, DefaultEdge>> pentagons=new ArrayList<>();
        List<Graph<CanvasVertex, DefaultEdge>> components=findConnectedComponentsInGraph(graph);

        outer:
        for (Graph<CanvasVertex, DefaultEdge> component:components){
            if (component.vertexSet().size()==5&&component.edgeSet().size()==5){
                for (CanvasVertex vertex:component.vertexSet()){
                    if (component.degreeOf(vertex)!=2)continue outer;
                }
                pentagons.add(component);
            }
        }
        return pentagons;
    }

    public static List<Graph<CanvasVertex, DefaultEdge>> findConnectedComponentsInGraph(Graph<CanvasVertex, DefaultEdge> graph){
        List<Graph<CanvasVertex, DefaultEdge>> components=new ArrayList<>();
        List<Set<CanvasVertex>> vertexSubsets = new ConnectivityInspector(graph).connectedSets();
        for (Set<CanvasVertex> vertexSubset:vertexSubsets){
            Graph<CanvasVertex,DefaultEdge> inducedSubGraph=new AsSubgraph(graph, vertexSubset);
            components.add(inducedSubGraph);
        }
        return components;
    }

    public static boolean isPointInPolygons(float x, float y, List<float[]> polyPoints) {
        if (polyPoints.size() < 3)return false;
        Path path=new Path();
        path.moveTo(polyPoints.get(0)[0], polyPoints.get(0)[1]);
        for (int i=1;i<polyPoints.size();i++){
            path.lineTo(polyPoints.get(i)[0], polyPoints.get(i)[1]);
        }
        path.close();
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        Region r= new Region();
        r.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        return r.contains((int)x, (int)y);
    }
}
