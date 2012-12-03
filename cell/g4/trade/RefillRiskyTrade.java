package cell.g4.trade;

import cell.g4.Board;
import cell.g4.Sack;

public class RefillRiskyTrade extends TradeAlgo {
	
	public RefillRiskyTrade(Board board, Sack sack) {
		super(board, sack);
	}

	@Override
	public void trade(double[] rate, int[] request, int[] give) {
		//find the cheapest marble
		int invaluable = invaluableColor(rate);
		
		int totalValue = 0;
		
		for (int i = 0 ; i != 6 ; ++i)
			request[i] = give[i] = 0;
		
		for (int i=0; i != 6; ++i){
			if(i == invaluable)
				continue;
			else {
				int amount = maxAmountToGive(i);
				give[i] = amount;
				totalValue += (amount * rate[i]);
				//request[invaluable] = request[invaluable] + (int)(amount * rate[i] / rate[invaluable]);
			}
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
}
