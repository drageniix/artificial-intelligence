
package search_implementations;

import java.util.Arrays;

import search_interfaces.Action;
import search_interfaces.Heuristic;
import search_interfaces.State;

public class GridState extends State<GridState, GridNode, GridToken, GridMap> {
	int[] borders;
	int goalNodeIndex, goalNode;
	GridNode[][] grid;
	int direction, scaleX, scaleY;
	
	public GridState(GridState parent, Action<GridState, GridNode, GridToken, GridMap> action, double cost, GridNode...i) {
		super(parent, action, cost, i);
		if(scaleX == 0 || scaleY == 0){
			if(parent != null){
				this.direction = parent.direction;
				this.scaleX = parent.scaleX;
				this.scaleY = parent.scaleY;
			} else {
				this.direction = 0;
				this.scaleX = 1;
				this.scaleY = 1;
			}
		}
	}
	
	public GridState(GridState parent, Action<GridState, GridNode, GridToken, GridMap> action, double cost, int direction, int scaleX, int scaleY, GridNode...i) {
		super(parent, action, cost, i);
		if(parent != null){
			this.scaleX = parent.scaleX * scaleX;
			this.scaleY = parent.scaleY * scaleY;
			this.direction = parent.direction + direction;
			if(this.direction >= 360){
				this.direction -= 360;
			} else if(this.direction <= -360){
				this.direction += 360;
			}
		} else {
			this.scaleX = scaleX;
			this.scaleY = scaleY;
		}
	}
	
	public GridState(GridNode...i){
		this(null, null, 0, i);		
	}
	
	public void setState(GridNode[] newNodes, GridMap map){
		setState(newNodes);
		borders = setBorders(getState());
		grid = setGrid(map, borders);
	}
	
	public int getDirection(){return direction;}
	public int getScaleX(){return scaleX;}
	public int getScaleY(){return scaleY;}
	
	public int[] getBorders(){
		return borders == null ? borders = setBorders(getState()) : borders;}
	public GridNode[][] getGrid(GridMap map){
		return grid == null ? grid = setGrid(map, getBorders()) : grid;}

	public int getGoalNodeIndex(){return goalNodeIndex;}
	public void setGoalNodeIndex(int setting){goalNodeIndex = setting;}
	public int getGoalNode(){return goalNode;}
	public void setGoalNode(int setting){goalNode = setting;}
	
	protected static int[] setBorders(GridNode[] state){
		int[] borders = new int[9];
		if(state.length > 0){
			borders[0] = state[0].getX();
			borders[1] = state[0].getX();
			borders[2] = state[0].getY();
			borders[3] = state[0].getY();
			borders[4] = 1;
			borders[5] = 1;
			borders[8] = state[0].isOccupied() ? 1 : 0;
			
			for(int i = 1; i < state.length; i++){
				if(state[i] != null){
					int x = state[i].getX();
					int y = state[i].getY();
					if(x < borders[0]){borders[0] = x;}
					if(x > borders[1]){borders[1] = x;}
					if(y < borders[2]){borders[2] = y;}
					if(y > borders[3]){borders[3] = y;}
					if(state[i].isOccupied()){borders[8] = 1;}
				}
			}
			
			borders[4] += borders[1] - borders[0];
			borders[5] += borders[3] - borders[2];
			borders[6] = (borders[0] + borders[1])/2;
			borders[7] = (borders[2] + borders[3])/2;	
		}
		
		return borders;
	}
	
	protected static GridNode[][] setGrid(GridMap map, int[] borders){
		GridNode[][] grid = new GridNode[borders[5]][borders[4]];
		int start = borders[2] * map.getNodesWidth() + borders[0];
		for(int row = 0; row < borders[5]; row++){
			for(int col = 0; col < borders[4]; col++){
				grid[row][col] = map.getNode(start + col + row * map.getNodesWidth());
			}	
		}
		return grid;
	}	
	
	public String toString(){
		return (Arrays.toString(getState()));
	}

	
	@Override
	public <H extends Heuristic<GridState, GridMap>> void calculateHeuristic(H h, GridToken p, GridMap m) {
		setHeuristic(h.getHeuristic(m, this) + getCost());	
	}
}
