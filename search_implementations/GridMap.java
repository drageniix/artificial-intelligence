package search_implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import search_implementations.GridNode.*;
import search_implementations.GridProblem.*;
import search_interfaces.AStar;
import search_interfaces.Action;
import search_interfaces.Map;
import search_interfaces.Minimax;

public class GridMap extends Map<GridState>{
	final int cellWidth = 25, cellHeight = 25;
	private GridNode[] nodes;
	private int width, height;
	
	public GridMap(int[][] absoluteMap){
		width = absoluteMap[0].length;
		height = absoluteMap.length;
		nodes = new GridNode[width * height];
        
		
		int index = 0;
       	for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                nodes[index] = new GridNode(col, row, Key.UNOCCUPIED, 
                		Terrain.values()[absoluteMap[col][row]]);
            }
        }
	}
	
	public GridMap(String mapLocation){
		try { 
			Path path = Paths.get(mapLocation);
			List<String> lines = Files.readAllLines(path);
			List<String> terrain = new ArrayList<>();
			for(String s : lines){
				Collections.addAll(terrain, s.split("[^0-9]+"));
			}
			
			width = lines.get(0).split("\\s+").length;
			height = terrain.size() / width;
			nodes = new GridNode[width * height];
	        
			
			int index = 0;
	       	for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++) {
	                nodes[index] = new GridNode(col, row, Key.UNOCCUPIED, 
	                		Terrain.values()[Integer.parseInt(terrain.get(index++))]);
	            }
	        }				
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public void setOccupied(GridNode node, boolean occupied){
		if(node != null) node.setOccupation(occupied);}
	public void setOccupied(GridState nodes, boolean occupied){
		for(GridNode node : nodes.getState()){setOccupied(node, occupied);}}
	public void setOccupied(int x, int y, boolean occupied) {
		if(withinParameters(x,y)){setOccupied(nodes[x + (width * y)], occupied);}}*/
	
	public GridNode getNode(int x, int y) {
    	return withinParameters(x,y) ? nodes[x + (width * y)] : null;}
	protected GridNode getNode(int index){
		return !(index >= nodes.length || index < 0) ? nodes[index] : null;}
	protected GridNode[] getNodes(){return nodes;}
	protected int getNodesIndexOf(GridNode node){ if(node != null)
		for(int i = 0; i < nodes.length; i++){if(nodes[i].equals(node)){return i;}}
		return -1;}
	
	public int getNodesLength(){return nodes.length;}
	public int getNodesWidth(){return width;}
	public int getNodesHeight(){return height;}
	   
    protected boolean withinParameters(int x, int y){
    	return (x >= 0 && x < width) && (y >= 0 && y < height) ? true : false;}
	 
    public enum Key implements Map.Key {UNOCCUPIED, OCCUPIED}
	@Override public void mark(int state, GridState nodes) {
		for(GridNode node : nodes.getState()){
			if(node != null) node.setOccupation(Key.values()[state]);
		}
	}
	
	public List<Action<GridState, GridNode, GridToken, GridMap>> findPath(GridToken token, GridNode[] goalState){
    	List<Action<GridState, GridNode, GridToken, GridMap>> solution = new AStar<GridState, GridNode, GridToken, GridMap>(1000, new GridProblem(GOAL.TARGET_TOKEN, HEURISTIC.TARGET_TOKEN, this, token)).initiate(-1, goalState);
    	return solution;
    }
    
    public Action<GridState, GridNode, GridToken, GridMap> findMove(GridToken token1, GridToken token2){
    	Action<GridState, GridNode, GridToken, GridMap> solution = new Minimax<GridState, GridNode, GridToken, GridMap>(3, new GridProblem(GOAL.BOARD_CONTROL, HEURISTIC.BOARD_CONTROL, this, token1, token2)).initiate();
    	return solution;
    }
    
    public String toString(){
    	String s = "";
    	int index = 0;
    	for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                s += nodes[index] + "\t";
                index++;
            }
            s += "\n";
        }
    	return s;
    }
}
