package cell.g1;

import java.util.ArrayList;

public class Path implements Comparable{
	int length;
	ArrayList<Node> locs;
	
	public void add(Node loc){
		locs.add(loc);
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
