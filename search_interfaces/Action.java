package search_interfaces;

public interface Action <STATETYPE extends State<STATETYPE, NODETYPE, PLAYER, MAP>, NODETYPE extends Node<?>, PLAYER extends Player<STATETYPE, NODETYPE, PLAYER, MAP>, MAP extends Map<STATETYPE>>{
	public STATETYPE getNewState(MAP map, STATETYPE parent, PLAYER player);
	public double getCost(NODETYPE[] newState);
}

