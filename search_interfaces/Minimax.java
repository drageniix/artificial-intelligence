package search_interfaces;

public class Minimax 
	<STATETYPE extends State<STATETYPE, NODETYPE, PLAYER, MAP>, 
	NODETYPE extends Node<?>, 
	PLAYER extends Player<STATETYPE, NODETYPE, PLAYER, MAP>, 
	MAP extends Map<STATETYPE>> {
	
	private double depth;
	private Problem<STATETYPE, NODETYPE, PLAYER, MAP> problem;
	private PLAYER player, opponent;
	private MAP map;
	
	public Minimax(double depthCutoff, Problem<STATETYPE, NODETYPE, PLAYER, MAP> problem) {
		this.problem = problem;
		this.depth = depthCutoff;
		this.map = problem.getMap();
	}

	public Action<STATETYPE, NODETYPE, PLAYER, MAP> initiate(){
		Action<STATETYPE, NODETYPE, PLAYER, MAP> result = null;
		this.player = problem.getPlayer(0);
		this.opponent = problem.getPlayer(1);
		
		STATETYPE playerInitialState = player.getState();
		STATETYPE opponentInitialState = opponent.getState();
		problem.getGoal().setGoal(map, player, playerInitialState, (NODETYPE[])null);
		if(Problem.goalState == null) return result;
		
		double resultValue = Double.NEGATIVE_INFINITY;
		for (Action<STATETYPE, NODETYPE, PLAYER, MAP> action : player.getActions()) {
			map.mark(1, opponentInitialState);
			map.mark(0, playerInitialState);
			STATETYPE newState = action.getNewState(map, playerInitialState, player);
			map.mark(0, opponentInitialState);
			if(newState != null){
				double value = minValue(newState, opponentInitialState, 0);
				if (value > resultValue) {
					result = action;
					resultValue = value;
				}
			}
		}
		map.mark(1, opponentInitialState);
		map.mark(1, playerInitialState);
		return result;
	}

	private double maxValue(STATETYPE playerState, STATETYPE opponentState, int cutoff) { 
		cutoff++;
		double value = Double.NEGATIVE_INFINITY;
		if(!problem.getGoal().test(playerState) && cutoff <= depth){
			for (Action<STATETYPE, NODETYPE, PLAYER, MAP> action : player.getActions()) {
				map.mark(1, opponentState);
				map.mark(0, playerState);
				STATETYPE newState = action.getNewState(map, playerState, player);
				map.mark(0, opponentState);
				if(newState != null){
					value = Math.max(value, minValue(newState, opponentState, cutoff));
				}
			}
		} else {
			value = getValue(playerState, player);
		}
		return value;		
	}

	private double minValue(STATETYPE playerState, STATETYPE opponentState, int cutoff) { 
		cutoff++;
		double value = Double.POSITIVE_INFINITY;
		if(!problem.getGoal().test(playerState) && cutoff <= depth){
			for (Action<STATETYPE, NODETYPE, PLAYER, MAP> action : opponent.getActions()) {
				map.mark(1, playerState);
				map.mark(0, opponentState);
				STATETYPE newState = action.getNewState(map, opponentState, opponent);
				map.mark(0, playerState);
				if(newState != null){
					value = Math.min(value, maxValue(playerState, newState, cutoff));
				}
			}
		} else {
			value = getValue(playerState, player);
		}
		return value;		
	}
	
	private Double getValue(STATETYPE state, PLAYER player) {
		state.calculateHeuristic(problem.getHeuristic(), player, map);
		return state.getHeuristic();
	}	
}