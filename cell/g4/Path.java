package cell.g4;

import java.util.ArrayList;
import java.util.List;

import cell.sim.Player.Direction;

public class Path {

	private List<Direction> dirs;
	
	public Path() {
		dirs = new ArrayList<Direction>();
	}
	
	public void addNext(Direction dir) {
		dirs.add(dir);
	}	
	
	public Direction popFirst() {
		return dirs.remove(0);
	}
	
	public List<Direction> getPath() {
		return dirs;
	}

	public int length() {
		return dirs.size();
	}
}
