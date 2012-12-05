package cell.g4.movement;

public class TraderLocator {
	
	// Version 1: always clean all queues and reinsert
	public void update(TraderQueue queue, int[] location, int[][] players, int[][] traders) {
		// first check current queue, see if we need to remove
		// then check if we should add traders
		// reorder those traders
	
		insertTraders(queue, location, players, traders);
		
	}
	
	// Simulate current strategy
	private void insertTraders(TraderQueue queue, int[] location, int[][] players, int[][] traders) {
		for (int i = 0; i < traders.length; i++) {
			
		}
	}
}
