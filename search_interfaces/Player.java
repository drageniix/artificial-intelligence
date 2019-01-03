package search_interfaces;

public abstract class Player <STATETYPE extends State<STATETYPE, NODETYPE, PLAYER, MAP>, NODETYPE extends Node<?>, PLAYER extends Player<STATETYPE, NODETYPE, PLAYER, MAP>, MAP extends Map<STATETYPE>>{
	public abstract Action<STATETYPE, NODETYPE, PLAYER, MAP>[] getActions();
	public abstract STATETYPE getState();
	
}
