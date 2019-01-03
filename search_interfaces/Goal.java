package search_interfaces;

public interface Goal <STATETYPE extends State<STATETYPE, NODETYPE, PLAYER, MAP>, NODETYPE extends Node<?>, PLAYER extends Player<STATETYPE, NODETYPE, PLAYER, MAP>, MAP extends Map<STATETYPE>>{
	public boolean test(STATETYPE state);
	@SuppressWarnings("unchecked")
	public void setGoal(MAP map, PLAYER player, STATETYPE initial, NODETYPE...goalState);
}
