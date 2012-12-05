package cell.g4.trade;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;

public class WinningTrade extends TradeAlgo {

	public WinningTrade(Board board, Sack sack, Player player) {
		super(board, sack, player);
	}

	@Override
	public void trade(double[] rate, int[] request, int[] give,
			int[] savedLocation, int[][] savedPlayers, int[][] savedTraders) {
		for (int i = 0; i < 6; i++) {
			request[i] = 0;
			give[i]	= 0;
		}
			
		for (int i = 0; i < 6; i++) {
			int remain = sack.getStock(i) - Sack.WinningStock;
			if (remain > 0) {
				give[i] = remain;
			}
			else {
				request[i] = -remain;
			}
		}
	}
	

	@Override
	public boolean toUse(double[] rate, int[] request, int[] give,
			int[] savedLocation, int[][] savedPlayers, int[][] savedTraders) {
		int giveValue = 0;
		int requestValue = 0;
		for (int i = 0; i < 6; i++) {
			int remain = sack.getStock(i) - Sack.WinningStock;
			if (remain > 0) {
				giveValue += remain * rate[i];
			}
			else {
				requestValue += -remain * rate[i];
			}
		}
		
		if (giveValue > requestValue + 0.8)
			return true;
		else
			return false;
	}

}
