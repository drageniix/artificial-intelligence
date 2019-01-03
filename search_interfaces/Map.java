package search_interfaces;

public abstract class Map <STATETYPE extends State<STATETYPE,?,?,?>>{
	public abstract void mark(int state, STATETYPE node);
	public interface Key{}
}