package search_interfaces;

public abstract class State <STATETYPE extends State<STATETYPE, NODETYPE, PLAYER, MAP>, 
	NODETYPE extends Node<?>, 
	PLAYER extends Player<STATETYPE, NODETYPE, PLAYER, MAP>, 
	MAP extends Map<STATETYPE>>{
	
	private State<STATETYPE, NODETYPE, PLAYER, MAP> parent;
	private double cost, heuristic;
	private int depth;
	private Action<?, NODETYPE, ?, MAP> action;
	private NODETYPE[] state;
	
	public State(State<STATETYPE, NODETYPE, PLAYER, MAP> parent, Action<?, NODETYPE, ?, MAP> action, double cost, NODETYPE[] state){
		this.parent = parent;
		this.state = state;
		this.action = action;
		if(parent == null){	
			this.depth = 0;
			this.cost = cost;
		} else {
			this.depth = 1 + parent.depth;
			this.cost = cost + parent.cost;
		}
	}
	
	protected void setState(NODETYPE[] newNodes){
		this.state = newNodes;}
	public NODETYPE[] getState(){
		return state;}
	public int getStateLength(){
		return state.length;}
	public NODETYPE getState(int index){
		if(state.length <= index)
			return null;
		return state[index];}
	public int getIndexOfState(NODETYPE[] nodes, int index){
		for(int i = 0; i < nodes.length; i++){
			if(nodes[i].equals(state[index])){
				return i;
			}
		}
		return -1;
	}
	public int getStateIndexOf(Object obj){
		for(int i = 0; i < state.length; i++){
			if(state[i].equals(obj)){
				return i;
			}
		}
		return -1;
	}
	
	public abstract <H extends Heuristic<STATETYPE, MAP>> void calculateHeuristic(H h, PLAYER p, MAP m);
	public void setHeuristic(Double heuristic){this.heuristic = heuristic;}
	
	public State<STATETYPE, NODETYPE, PLAYER, MAP> getParent(){ return parent; }
	public int getDepth() { return depth; } 
	public Action<?, NODETYPE, ?, MAP> getAction(){ return action; }
	public Double getCost(){ return cost; }
	public Double getHeuristic(){ return heuristic;}
	@SuppressWarnings("unchecked")
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof State)) return false;
	    State<STATETYPE, NODETYPE, PLAYER, MAP> otherState = (State<STATETYPE, NODETYPE, PLAYER, MAP>)other;
	    if(getAction() != otherState.getAction()){
			return false;
		}
	    for(int i = 0; i < otherState.getStateLength(); i++){
			if(!otherState.getState(i).equals(getState(i))){
				return false;
			}
		}
		return true;
	}
}