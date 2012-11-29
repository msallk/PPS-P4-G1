package cell.g5;

public class Trading {
	public static int findCheapestIndex(double[] value) {
		if (value.length == 0)
			throw new IllegalArgumentException();
		
		double cheapest = Double.MAX_VALUE;
		int cheapestIdx = -1;
		
		for (int idx = 0; idx < value.length; idx++) {
			if (value[idx] < cheapest) {
				cheapest = value[idx];
				cheapestIdx = idx;
			}
		}
		
		return cheapestIdx;
	}
	
	/* Note that this doesn't care at all about rates. Could go bankrupt. */
	public static void tradeAllBut(int[] buy, int[] sell, int[] sack, int[] keep, double[] rates) {
		/* delta = diff. between have and desired. positive delta = sell off that marble, negative = buy */
		int[] delta = new int[keep.length];
		for (int i = 0; i < keep.length; i++) {
			delta[i] = sack[i] - keep[i];
		}
		
		for (int i = 0; i < delta.length; i++) {
			if (delta[i] > 0)
				sell[i] = delta[i];
			else
				buy[i] = -delta[i];
		}
		
		double net = 0;
		for (int i = 0; i < delta.length; i++) {
			net += rates[i] * sell[i];
			net -= rates[i] * buy[i];
		}
		
		if (net < 0) {
			// we've gone bankrupt
		} else {
			int cheapestIdx = findCheapestIndex(rates);
			int buyCheapest = (int) (net / rates[cheapestIdx]);
			buy [cheapestIdx] += buyCheapest;
		}
	}
	
	/* Returns true and buy/sell arrays if a win is possible; else returns false and leaves stuff unchanged. */
	public static boolean tradeVictoryAndCheck(int[] buy, int[] sell, int[] sack, int startMarbles, double[] rates) {
		int[] initialBuy = buy;
		int[] initialSell = sell;
		buy = new int[buy.length];
		sell = new int[sell.length];
		
		/* delta = diff. between have and desired. positive delta = sell off that marble, negative = buy */
		final int targetMarbles = startMarbles * 4;
		int[] keep = new int[] {targetMarbles, targetMarbles, targetMarbles, targetMarbles, targetMarbles, targetMarbles};
		
		int[] delta = new int[keep.length];
		for (int i = 0; i < keep.length; i++) {
			delta[i] = sack[i] - keep[i];
		}
		
		for (int i = 0; i < delta.length; i++) {
			if (delta[i] > 0)
				sell[i] = delta[i];
			else
				buy[i] = -delta[i];
		}
		
		double net = 0;
		for (int i = 0; i < delta.length; i++) {
			net += rates[i] * sell[i];
			net -= rates[i] * buy[i];
		}
		
		if (net > 0) {
			System.arraycopy(sell, 0, initialSell, 0, sell.length);
			System.arraycopy(buy, 0, initialBuy, 0, buy.length);
			return true;
		} else {
			return false;
		}
	}
}
