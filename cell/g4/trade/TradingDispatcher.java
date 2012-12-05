package cell.g4.trade;

import cell.g4.Board;
import cell.g4.Logger;
import cell.g4.Player;
import cell.g4.Sack;

public class TradingDispatcher {
	protected final static Logger logger = Logger.getLogger(TradeAlgo.class);
	
	private Player player;
	
	private WinningTrade winningTrading;
	private SafeTrade safeTrading;
	private DefaultTrade refillTrading;
	
	protected int totalTradeCount = 0;
	protected int safeTradeCount = 0;

	public TradingDispatcher(Board board, Sack sack, Player player) {
		safeTrading = new SafeTrade(board, sack, player);
		refillTrading = new DefaultTrade(board, sack, player);
		winningTrading = new WinningTrade(board, sack, player);
		
		this.player = player;
	}
	
	
	public TradeAlgo pickTradingAlgo(double[] rate, int[] request, int[] give,
			int[] savedLocation, int[][] savedPlayers, int[][] savedTraders) {
		totalTradeCount++;
		
		logger.log("ratio: " + safeTradeCount + "," + totalTradeCount);
		
		if (winningTrading.toUse(rate, request, give, savedLocation, savedPlayers, savedTraders))
			return winningTrading;
		else if (safeTrading.toUse(rate, request, give, savedLocation, savedPlayers, savedTraders)) {
			logger.log("Doing Safe Trade");
			safeTradeCount++;
			return safeTrading;
		}
		else
			return refillTrading;
	}

}
