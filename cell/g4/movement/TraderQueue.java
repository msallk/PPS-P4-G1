package cell.g4.movement;

import java.util.ArrayList;
import java.util.List;

// trader queue keeps the traders that are "controlled" by us
public class TraderQueue {
	private List<NextTrader> nextTraders;
	
	public TraderQueue() {
		nextTraders = new ArrayList<NextTrader>();
	}
	
	public void clean() {
		nextTraders.clear();
	}
	
	public boolean isEmpty() {
		return nextTraders.size() == 0;				
	}
	
	public void addTrader(NextTrader trader) {
		nextTraders.add(trader);
	}
	
	public NextTrader first() {
		if (isEmpty())
			return null;
		return nextTraders.get(0);
	}
	
	public void removeFirst() {
		assert(!isEmpty());

		nextTraders.remove(0);
	}
}
