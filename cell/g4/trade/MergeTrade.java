package cell.g4.trade;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;

public class MergeTrade extends TradeAlgo {
	private MaxRateDiffTrade diffTrading;
	private WinningTrade winningTrading;
	private RiskyTrade riskTrading;
	private RefillRiskyTrade refillTrading;
	private TradeAlgo current;
	
	
	public MergeTrade(Board board, Sack sack) {
		super(board, sack);
		diffTrading = new MaxRateDiffTrade(board, sack);
		riskTrading = new RiskyTrade(board, sack);
		refillTrading = new RefillRiskyTrade(board, sack);
		winningTrading = new WinningTrade(board, sack);
		
		current = refillTrading;
	}

	@Override
	public void trade(double[] rate, int[] request, int[] give) {
		if (winningTrading.canWin(rate)) {
			current = winningTrading;
		}
		
		current.trade(rate, request, give);
	}	
}
