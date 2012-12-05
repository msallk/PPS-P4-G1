package cell.g4.trade;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;

/*
 *  Trading as much as we can within a threshold 
 */
public class DefaultTrade extends TradeAlgo {
	
	public DefaultTrade(Board board, Sack sack, Player player) {
		super(board, sack, player);
	}

	@Override
	public void trade(double[] rate, int[] request, int[] give,
			int[] savedLocation, int[][] savedPlayers, int[][] savedTraders) {
		//find the cheapest marble
		int invaluable = invaluableColor(rate);
		
		int totalValue = 0;
		
		for (int i = 0 ; i != 6 ; ++i)
			request[i] = give[i] = 0;
		
		for (int i=0; i != 6; ++i){
			int amount = maxAmountToGive(i);
			give[i] = amount;
			totalValue += (amount * rate[i]);
		}
		
		
		// first replenish
		for (int i = 0; i < 6; i++) {
			if (sack.getStock(i) < sack.getReserve(i)) {
				request[i] = Math.min((int)(totalValue / rate[i]), sack.getReserve(i) - sack.getStock(i));
				totalValue -= (request[i] * rate[i]);
			}
		}
		
		// then buy invaluable
		request[invaluable] += (int)(totalValue / rate[invaluable]);
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
	
	
	private int maxAmountToGive(int color) {
		return Math.max(0, sack.getStock(color) - sack.getReserve(color));
	}

	@Override
	public boolean toUse(double[] rate, int[] request, int[] give,
			int[] savedLocation, int[][] savedPlayers, int[][] savedTraders) {
		return true;
	}
}
