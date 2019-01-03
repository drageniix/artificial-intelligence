package search_implementations;
import search_implementations.GridMap.Key;
import search_interfaces.Node;

public class GridNode extends Node<Integer>{
	private int x, y;
	private Key state;
	private Terrain terrain;
	
	protected GridNode(int x, int y, Key state, Terrain terrain){
		this.terrain = terrain;
		this.x = x;
		this.y = y;
		this.state = state;
	}
	
	public Terrain getTerrain(){return terrain;}
	public boolean isOccupied(){return state == Key.OCCUPIED;}
	public void setOccupation(Key occupied){this.state = occupied;}
	public Integer getX(){ return this.x;}
	public Integer getY(){ return this.y;}
	
	public Integer distanceFrom(GridNode other){
		return Math.abs(x - other.x) + Math.abs(y - other.y);
	}
	
	@Override
	public String toString(){
		return "(" + x + ", " + y + ") " + (isOccupied() ? " ~O~ " : terrain);}
	
	@Override public Integer get(){
		return Integer.valueOf(y * x + x);}
	
	@Override
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof GridNode)) return other.equals(get());
	    GridNode otherNode = (GridNode)other;
		return otherNode.getX() == getX() && otherNode.getY() == getY(); 
	}

	public enum Terrain{
		DIRT, GRASS, WATER, FIRE, AIR
	}
}