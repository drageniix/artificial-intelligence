package search_implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import search_implementations.GridNode.Terrain;
import search_implementations.GridProblem.DPAD;
import search_implementations.GridProblem.SPREAD;
import search_interfaces.Action;
import search_interfaces.Player;

public class GridToken extends Player<GridState, GridNode, GridToken, GridMap>{
	private GridMap map;
	private GridState location;
	private List<Action<GridState, GridNode, GridToken, GridMap>> actions;
	private GridNode[] targetNodes;
	private GridToken targetToken;
	
	public GridToken(GridMap map, GridNode...location){
		this.location = new GridState(location);
		this.map = map;
		map.mark(1, this.location);
		actions = new ArrayList<>();
    }
	
	public GridState getState(){return location;}
	private void setLocation(GridState location){this.location = location;}
	public boolean canTraverse(GridNode node){
		return node.getTerrain() == Terrain.GRASS;}
	
	public void competeAgainst(GridToken token){
		actions.clear();
		actions.add(map.findMove(this, token));
		System.out.println(map + "\n" + actions);
	}
	
	public void navigateTo(GridToken token){
		targetToken = token;
		targetNodes = token.getState().getState();
		actions = map.findPath(this, targetNodes);
		System.out.println(map + "\n" + location + " --->> " + actions + "\n");
	}

	public void takeActions(int amount){
		for(int i = (amount == -1 ? actions.size() : amount); i > 0; i--){
			if(!actions.isEmpty()){
				map.mark(0, this.location);
				GridState state = actions.get(0) == null ? null : actions.remove(0).getNewState(map, location, this);
				if(targetToken != null && (state == null || !Arrays.equals(targetNodes, targetToken.getState().getState()))){
					actions.clear();
					map.mark(1, this.location);
					navigateTo(targetToken);
					takeActions(amount == -1 ? -1 : i);
					break;
				} else if (state != null){
					setLocation(state);
					map.mark(1, this.location);
					if(actions.isEmpty()){
						targetToken = null;
						targetNodes = null;	
						break;
					}
				} else {
					actions.clear();
					map.mark(1, this.location);
					break;
				}
			}
		}
	}

	@Override
	public Action<GridState, GridNode, GridToken, GridMap>[] getActions() {
		return 	SPREAD.values();
	}
}