package cell.g4.trade;

import cell.g4.Board;
import cell.g4.Sack;

/**
 * Maximum Rate Difference Trading Algorithm
 * Always trade the most valuable marbles for invaluable ones
 */
public class MaxRateDiffTrade extends TradeAlgo {
	public MaxRateDiffTrade(Board board, Sack sack) {
		super(board, sack);
	}
	
	@Override
	public void trade(double[] rate, int[] request, int[] give) {
		int valuable = valuableColor(rate);
		int invaluable = invaluableColor(rate);
		
		int amount = maxAmount(valuable, invaluable);
		for (int i = 0 ; i != 6 ; ++i)
			request[i] = give[i] = 0;
		give[valuable] = amount;
		request[invaluable] = (int)(amount * rate[valuable] / rate[invaluable]);
	}
	
	private int maxAmount(int valuable, int invaluable) {
		return Math.max(0, sack.getStock(valuable) - sack.getReserve(valuable));
	}
	
	private int valuableColor(double[] rate) {
		double maxrate = 0;
		int color = -1;
		for (int i = 0; i < rate.length; i++) {
			if (rate[i] > maxrate) {
				maxrate = rate[i];
				color = i;
			}
		}
		return color;
	}
	
	private int invaluableColor(double[] rate) {
		double minrate = Double.MAX_VALUE;
		int color = -1;
		for (int i = 0; i < rate.length; i++) {
			if (rate[i] < minrate) {
				minrate = rate[i];
				color = i;
			}
		}
		return color;
	}
}		
