package cell.g4.movement;

import java.util.List;

import cell.g4.Path;
import cell.sim.Player.Direction;

public class NextTrader {
	private int tid;
	private boolean conflict = false;
	private Path storedPath;
	
	public NextTrader(int tid) {
		this.tid = tid;
	}
	
	public int getTid() {
		return tid;
	}
	
	public boolean isConflict() {
		return conflict;
	}
	
	public Path getStoredPath() {
		return storedPath;
	}
}
