package search_interfaces;

public interface Heuristic <STATETYPE extends State<STATETYPE, ?, ?, MAP>, MAP extends Map<STATETYPE>>{
	public double getHeuristic(MAP map, STATETYPE state);
}
