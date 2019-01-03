package search_implementations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import search_interfaces.Action;
import search_interfaces.Goal;
import search_interfaces.Heuristic;
import search_interfaces.Problem;

public class GridProblem extends Problem<GridState, GridNode, GridToken, GridMap> {
	private GridMap map;
	private GridToken[] tokens;
	
	public GridProblem(Goal<GridState, GridNode, GridToken, GridMap> goal, Heuristic<GridState, GridMap> heuristic, GridMap gmap, GridToken...tokenPlayers) {
		super(goal, heuristic);
		tokens = tokenPlayers;
		map = gmap;
	}
	
	@Override
	public GridMap getMap(){
		return map;
	}
	
	@Override
	public GridToken getPlayer(int index){
		return tokens[index];
	}
	
	private static boolean testNode(GridNode node, GridToken token){
		return(node != null && !node.isOccupied() && token.canTraverse(node));}

	public enum GOAL implements Goal<GridState, GridNode, GridToken, GridMap>{
		TARGET_TOKEN{
			@Override
			public void setGoal(GridMap map, GridToken token, GridState initialState, GridNode...relevant) {
				int[] rBorders = GridState.setBorders(relevant);
				Set<GridNode> possibleGoals = new HashSet<>();
				
				if(rBorders[8] != 1){
					Collections.addAll(possibleGoals, relevant);	
				} else {
					GridNode node;
					for(int i = 0; i < rBorders[5]; i++){
						int left = rBorders[0], right = rBorders[1];
						while(left < rBorders[1]
								&& map.getNode(left, rBorders[2] + i) != null 
								&& !map.getNode(left, rBorders[2] + i).isOccupied()){left++;}
						node = map.getNode(left - 1, rBorders[2] + i);
						possibleGoals.add(node);
						
						while(right > rBorders[0]
								&& map.getNode(right, rBorders[2] + i) != null 
								&& !map.getNode(right, rBorders[2] + i).isOccupied()){right--;}
						node = map.getNode(right + 1, rBorders[2] + i);
						possibleGoals.add(node);}
					
					for(int i = 0; i < rBorders[4]; i++){
						int top = rBorders[2], bottom = rBorders[3];
						while(top < rBorders[3]
								&& map.getNode(rBorders[0] + i, top) != null 
								&& !map.getNode(rBorders[0] + i, top).isOccupied()){top++;}
						node = map.getNode(rBorders[0] + i, top - 1);
						possibleGoals.add(node);
					 	
						while(bottom > rBorders[2]
								&& map.getNode(rBorders[0] + i, bottom) != null 
								&& !map.getNode(rBorders[0] + i, bottom).isOccupied()){bottom--;}
						node = map.getNode(rBorders[0] + i, bottom + 1);
					 	possibleGoals.add(node);}
				}
				
				possibleGoals.removeIf(node -> !testNode(node, token));
				if(!possibleGoals.isEmpty()){
					goalState = possibleGoals.toArray(new GridNode[possibleGoals.size()]);
				}	
			}
			
			@Override
			public boolean test(GridState parent) {
				return parent.getState(parent.getGoalNode()).distanceFrom(((GridNode)goalState[parent.getGoalNodeIndex()])) == 0 ? true : false;
			}
		
		}, BOARD_CONTROL{
			@Override
			public boolean test(GridState state) {
				return state.getStateLength() > goalState.length/3;
			}

			@Override
			public void setGoal(GridMap map, GridToken token, GridState initial, GridNode... goalStates) {
				Set<GridNode> possibleGoals = new HashSet<>();
				for(GridNode node : map.getNodes()){
					if(testNode(node, token)) possibleGoals.add(node);}
				goalState = possibleGoals.toArray(new GridNode[possibleGoals.size()]);
			}
		};			
	}
	
	public enum HEURISTIC implements Heuristic<GridState, GridMap>{
		TARGET_TOKEN {
			@Override
			public double getHeuristic(GridMap map, GridState state) {
				int fitness = Integer.MAX_VALUE;
				for(int j = 0; j < state.getStateLength(); j++){
					for(int i = 0; i < goalState.length; i++){
						int nFitness = ((GridNode)goalState[i]).distanceFrom(state.getState(j));
						if(nFitness < fitness){
							fitness = nFitness;
							state.setGoalNodeIndex(i);
							state.setGoalNode(j);
						}
					}
				}
				return fitness;
			}		
		}, BOARD_CONTROL {
			@Override
			public double getHeuristic(GridMap map, GridState state) {
				return state.getStateLength();
			}
		};
	}
	
	public enum TRANSLATE implements Action<GridState, GridNode, GridToken, GridMap>{
		SCALEX{
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				GridNode[] newRelevant = reverseRows(map, parent, token);
				for(GridNode node : newRelevant){if(!testNode(node, token)) return null;}
				return(new GridState(parent, this, getCost(newRelevant), 0, -1, 1, newRelevant));
			}
		}, SCALEY{
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				GridNode[] newRelevant = reverseColumns(map, parent, token);
				for(GridNode node : newRelevant){if(!testNode(node, token)) return null;}
				return(new GridState(parent, this, getCost(newRelevant), 0, 1, -1, newRelevant));
			}
		}, CW90{
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				GridNode[] oldNodes = parent.getState();
				parent.setState(transposeGrid(map, parent, token), map);
				GridNode[] newRelevant = reverseRows(map, parent, token);
				parent.setState(oldNodes, map);
				for(GridNode node : newRelevant){if(!testNode(node, token)) return null;}
				return(new GridState(parent, this, getCost(newRelevant), 90, 1, -1, newRelevant));
			}
		}, CCW90{
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				GridNode[] oldNodes = parent.getState();
				parent.setState(transposeGrid(map, parent, token), map);
				GridNode[] newRelevant = reverseColumns(map, parent, token);
				parent.setState(oldNodes, map);
				for(GridNode node : newRelevant){if(!testNode(node, token)) return null;}
				return(new GridState(parent, this, getCost(newRelevant), -90, 1, -1, newRelevant));
			}
		};
		
		@Override public double getCost(GridNode[] newState) {return 0.25;}
		
		private static GridNode[] transposeGrid(GridMap map, GridState parent, GridToken token){
			GridNode[] newNodes = new GridNode[parent.getStateLength()];
			int start = parent.getBorders()[2] * map.getNodesWidth() + parent.getBorders()[0];
			int index = 0;
			for(int row = 0; row < parent.getGrid(map).length; row++){
			    for(int col = 0; col < parent.getGrid(map)[row].length; col++) {
			    	if(parent.getStateIndexOf(parent.getGrid(map)[row][col]) != -1){
			    		newNodes[index++] = map.getNode(start + row + col * map.getNodesWidth());
			    	}
			    }
			}
			return newNodes;
		}
	
		private static GridNode[] reverseColumns(GridMap map, GridState parent, GridToken token){
			GridNode[] newNodes = new GridNode[parent.getStateLength()];
			int start = parent.getBorders()[2] * map.getNodesWidth() + parent.getBorders()[0];
			int index = 0;
			int temp = 0;
			for(int row = parent.getGrid(map).length-1; row >= 0; row--){
			    for(int col = 0; col < parent.getGrid(map)[row].length; col++) {
			        if(parent.getStateIndexOf(parent.getGrid(map)[temp][col]) != -1){
			        	newNodes[index++] = map.getNode(start + col +  row * map.getNodesWidth());
			        }
			    }
			    temp++;
			}
			return newNodes;
		}
		
		private static GridNode[] reverseRows(GridMap map, GridState parent, GridToken token){
			GridNode[] newNodes = new GridNode[parent.getStateLength()];
			int start = parent.getBorders()[2] * map.getNodesWidth() + parent.getBorders()[0];
			int index = 0;
			for(int row = 0; row < parent.getGrid(map).length; row++){
			    for(int col = 0; col < parent.getGrid(map)[row].length; col++) {
			        if(parent.getStateIndexOf(parent.getGrid(map)[row][col]) != -1){
			        	newNodes[index++] = map.getNode(start + ((parent.getGrid(map)[row].length - col - 1) + (row * map.getNodesWidth())));}
			    }
			}
			return newNodes;
		}
	}	
	
	public enum DPAD implements Action<GridState, GridNode, GridToken, GridMap>{
		UP(1) {
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				if(parent.getScaleY() == -1){
					parent = TRANSLATE.SCALEY.getNewState(map, parent, token);
					if(parent == null) return null;}
				for(int i = 0; i < parent.getStateLength(); i++){
					if(!(parent.getState(i).getY() > 0
							&& testNode(map.getNode(parent.getState(i).getX(), parent.getState(i).getY() - 1), token))){
						return null;}
				}
				GridNode[] newRelevant = new GridNode[parent.getStateLength()];
				for(int i = 0; i < parent.getStateLength(); i++){
					newRelevant[i] = map.getNode(parent.getState(i).getX(), parent.getState(i).getY() - 1);
				}
				return(new GridState(parent, this, getCost(newRelevant), newRelevant));
			}			
		}, DOWN(1){
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				if(parent.getScaleY() == 1){
					parent = TRANSLATE.SCALEY.getNewState(map, parent, token);
					if(parent == null) return null;}
				for(int i = 0; i < parent.getStateLength(); i++){
					if(!testNode(map.getNode(parent.getState(i).getX(), parent.getState(i).getY() + 1), token)){
							return null;}
				}
				GridNode[] newRelevant = new GridNode[parent.getStateLength()];
				for(int i = 0; i < parent.getStateLength(); i++){
					newRelevant[i] = map.getNode(parent.getState(i).getX(), parent.getState(i).getY() + 1);
				}
				return(new GridState(parent, this, getCost(newRelevant), newRelevant));
			}
		}, LEFT(1){
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				if(parent.getScaleY() == -1){
					parent = TRANSLATE.SCALEY.getNewState(map, parent, token);
					if(parent == null) return null;}
				if(parent.getScaleX() == 1){
					parent = TRANSLATE.SCALEX.getNewState(map, parent, token);
					if(parent == null) return null;}
				for(int i = 0; i < parent.getStateLength(); i++){
					if(!testNode(map.getNode(parent.getState(i).getX() - 1, parent.getState(i).getY()), token)){
						return null;}
				}
				GridNode[] newRelevant = new GridNode[parent.getStateLength()];
				for(int i = 0; i < parent.getStateLength(); i++){
					newRelevant[i] = map.getNode(parent.getState(i).getX() - 1, parent.getState(i).getY());
				}
				return(new GridState(parent, this, getCost(newRelevant), newRelevant));
			}
		}, RIGHT(1){
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				if(parent.getScaleY() == -1){
					parent = TRANSLATE.SCALEY.getNewState(map, parent, token);
					if(parent == null) return null;}
				if(parent.getScaleX() == -1){
					parent = TRANSLATE.SCALEX.getNewState(map, parent, token);
					if(parent == null) return null;}
				for(int i = 0; i < parent.getStateLength(); i++){
					if(!testNode(map.getNode(parent.getState(i).getX() + 1, parent.getState(i).getY()), token)){
						return null;}
				} 
				GridNode[] newRelevant = new GridNode[parent.getStateLength()];
				for(int i = 0; i < parent.getStateLength(); i++){
					newRelevant[i] = map.getNode(parent.getState(i).getX() + 1, parent.getState(i).getY());
				}
				return(new GridState(parent, this, getCost(newRelevant), newRelevant));
			}
		};
		
		double cost; DPAD(double cost){this.cost = cost;}
		public double getCost(GridNode[] newState){return cost;}
	}

	public enum SPREAD implements Action<GridState, GridNode, GridToken, GridMap>{
		UP(1) {
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				GridState temp = DPAD.UP.getNewState(map, parent, token);
				if(temp != null){
					Set<GridNode> newRelevant = new HashSet<>();
					newRelevant.addAll(Arrays.asList(parent.getState()));
					newRelevant.addAll(Arrays.asList(temp.getState()));
					GridNode[] newState = newRelevant.toArray(new GridNode[newRelevant.size()]);
					return(new GridState(parent, this, getCost(newState), newState));
				} else {
					return null;
				}
			}
		}, DOWN(1) {
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				GridState temp = DPAD.DOWN.getNewState(map, parent, token);
				if(temp != null){
					Set<GridNode> newRelevant = new HashSet<>();
					newRelevant.addAll(Arrays.asList(parent.getState()));
					newRelevant.addAll(Arrays.asList(temp.getState()));
					GridNode[] newState = newRelevant.toArray(new GridNode[newRelevant.size()]);
					return(new GridState(parent, this, getCost(newState), newState));
				} else {
					return null;
				}
			}
		}, LEFT(1) {
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				GridState temp = DPAD.LEFT.getNewState(map, parent, token);
				if(temp != null){
					Set<GridNode> newRelevant = new HashSet<>();
					newRelevant.addAll(Arrays.asList(parent.getState()));
					newRelevant.addAll(Arrays.asList(temp.getState()));
					GridNode[] newState = newRelevant.toArray(new GridNode[newRelevant.size()]);
					return(new GridState(parent, this, getCost(newState), newState));
				} else {
					return null;
				}
			}
		}, RIGHT(1) {
			@Override
			public GridState getNewState(GridMap map, GridState parent, GridToken token) {
				GridState temp = DPAD.RIGHT.getNewState(map, parent, token);
				if(temp != null){
					Set<GridNode> newRelevant = new HashSet<>();
					newRelevant.addAll(Arrays.asList(parent.getState()));
					newRelevant.addAll(Arrays.asList(temp.getState()));
					GridNode[] newState = newRelevant.toArray(new GridNode[newRelevant.size()]);
					return(new GridState(parent, this, getCost(newState), newState));
				} else {
					return null;
				}
			}
		};
			
		double cost; SPREAD(double cost){this.cost = cost;}
		public double getCost(GridNode[] newState){return cost;}
		
	}
	
}
