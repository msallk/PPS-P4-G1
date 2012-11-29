package cell.g1;

import java.util.ArrayList;

public class Path implements Comparable<Path>, Cloneable{
	int length;
	ArrayList<Node> locs;
	
	public void add(Node loc){
		locs.add(loc);
	}

	@Override
	public int compareTo(Path arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void remove(Node node) {
		locs.remove(node);
	}
	
}
