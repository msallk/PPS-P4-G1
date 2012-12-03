package cell.g1;

import java.util.ArrayList;

public class Path implements Comparable<Path>, Cloneable{
	int length;
	ArrayList<Node> locs;
	
	public Path(){
		locs=new ArrayList<Node>();
	}
	
	public void add(Node loc){
		locs.add(loc);
		length=locs.size();
	}

	@Override
	public int compareTo(Path arg0) {
		if(this.length>arg0.length)
			return 1;
		else if (this.length<arg0.length)
			return -1;
		else 
			return 0;
	}

	public Object clone(){
		try {
			Path newPath=(Path) super.clone();
			newPath.locs=(ArrayList<Node>) locs.clone();
			return newPath;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void remove(Node node) {
		locs.remove(node);
		length=locs.size();
	}
	
	public String toString(){
		return locs.toString();
	}
	
}
