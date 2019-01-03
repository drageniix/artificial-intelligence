package search_interfaces;


public abstract class Problem <STATETYPE extends State<STATETYPE, NODETYPE, PLAYER, MAP>, 
	NODETYPE extends Node<?>, 
	PLAYER extends Player<STATETYPE, NODETYPE, PLAYER, MAP>, 
	MAP extends Map<STATETYPE>>{
	
	private Goal<STATETYPE, NODETYPE, PLAYER, MAP> goal;
	private Heuristic<STATETYPE, MAP> heuristic;
	protected static Node<?>[] goalState;

	public Problem(Goal<STATETYPE, NODETYPE, PLAYER, MAP> goal, Heuristic<STATETYPE, MAP> heuristic){
		this.goal = goal;
		this.heuristic = heuristic;
	}
	
	public Goal<STATETYPE, NODETYPE, PLAYER, MAP> getGoal(){return goal;}
	public Heuristic<STATETYPE, MAP> getHeuristic(){return heuristic;}
	public abstract PLAYER getPlayer(int index);
	public abstract MAP getMap();
}
