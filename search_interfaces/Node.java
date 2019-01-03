package search_interfaces;

public abstract class Node<TYPE extends Comparable<TYPE>> implements Comparable<Node<TYPE>> {
	public abstract TYPE get();
	public abstract boolean equals(Object other);
	
	@Override
	public String toString(){
		return String.valueOf(get());
	}
	
	@Override
	public int compareTo(Node<TYPE> o) {
		if (get().equals(o.get())) {
	        return 0;
	    }
	    if (get() == null) {
	        return -1;
	    }
	    if (o.get() == null) {
	        return 1;
	    }
		return get().compareTo(o.get());
	}

}
