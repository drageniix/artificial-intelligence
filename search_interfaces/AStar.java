package search_interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AStar <STATETYPE extends State<STATETYPE, NODETYPE, PLAYER, MAP>, 
	NODETYPE extends Node<?>, 
	PLAYER extends Player<STATETYPE, NODETYPE, PLAYER, MAP>, 
	MAP extends Map<STATETYPE>> {
	
	protected int absoluteLimit, location;
	protected double depth;
	protected Problem<STATETYPE, NODETYPE, PLAYER, MAP> problem;
	protected List<STATETYPE> queue, explored;
	protected List<Action<STATETYPE, NODETYPE, PLAYER, MAP>> solution;
	protected MAP map;
	protected PLAYER player;
	
	public AStar(int limit, Problem<STATETYPE, NODETYPE, PLAYER, MAP> problem) {
		this.problem = problem;
		this.absoluteLimit = limit;
		this.location = 0;
	}

	@SuppressWarnings("unchecked")
	public List<Action<STATETYPE, NODETYPE, PLAYER, MAP>> initiate(double depth, NODETYPE[] goalState){
		this.depth = depth;
		this.solution = new LinkedList<>();
		this.explored = new ArrayList<>();
		this.queue = new ArrayList<>();
		this.map = problem.getMap();
		this.player = problem.getPlayer(0);
		
		STATETYPE initialState = player.getState();
		map.mark(0, initialState);
		queue.add(initialState);
		problem.getGoal().setGoal(map, player, initialState, goalState);
		if(Problem.goalState == null) return solution;
		initialState.calculateHeuristic(problem.getHeuristic(), player, map);
		
		boolean goal = false;
		for(int i = 0; i < absoluteLimit; i++){
			boolean finished = true;
			if(!problem.getGoal().test(queue.get(location))){
				for (Action<STATETYPE, NODETYPE, PLAYER, MAP> action : player.getActions()){
					STATETYPE newState = action.getNewState(map, queue.get(location), player);
					if(newState != null){
						newState.calculateHeuristic(problem.getHeuristic(), player, map);
						if((depth == -1 || getValue(newState) < depth) && !explored.contains(newState)){
							queue.add(newState);
						}
					}
				}
				if(queue.size() > 1 ){
					STATETYPE oldState = queue.get(location);
					queue.removeIf(oldState::equals);
					explored.add(oldState);
					queue.sort((STATETYPE node1, STATETYPE node2) -> getValue(node1).compareTo(getValue(node2)));
					if(queue.size() != 0) finished = false;
				}
			} else {
				goal = true;
			}
			
			if (finished || i == absoluteLimit - 1){
				if(goal){
					STATETYPE state = queue.get(location);
					while (state != null && !state.equals(initialState)){
						solution.add((Action<STATETYPE, NODETYPE, PLAYER, MAP>) state.getAction());
						state = (STATETYPE) state.getParent();
					}
					Collections.reverse(solution);
				}
				break;
			}
		}
		map.mark(1, initialState);
		return solution;
	}

	private Double getValue(STATETYPE node){
		return node.getCost() + node.getHeuristic();
	}
}