package cell.g4;

public class Team implements Comparable<Team> {
	private int loc[] = new int[2];
	private int tradeCount = 0;
	
	public Team() {		
	}
	
	public void updateLocation(int[] loc) {
		this.loc[0] = loc[0];
		this.loc[1] = loc[1];
	}
	
	public void addTrade() {
		tradeCount++;
	}

	public int getTradeCount() {
		return tradeCount;
	}
	
	public int[] getLocation() {
		return loc;
	}
	
	@Override
	public int compareTo(Team other) {
		if (tradeCount > other.tradeCount)
			return 1;
		else if (tradeCount < other.tradeCount)
			return -1;
		else
			return 0;
	}
	
}
