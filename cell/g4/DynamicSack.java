package cell.g4;

import java.util.Arrays;

public class DynamicSack extends Sack {
	BoardAnalyzer ba = new BoardAnalyzer();
	double[] dist;
	int dimension;
	public DynamicSack(int[] sack,Board board){
		super(sack,board);

		dist = ba.getColorDistribution(board.getBoard());
		dimension = ba.dimensionOfMap(board.getBoard());
		
		updateReserve();
	}

	@Override
	public int getReserve(int color) {
		//udpate the resrve here
		return super.getReserve(color);
	}

	private void updateReserve(){
		int[] reserve = new int[6];
		for(int i=0; i < reserve.length; i++){
			reserve[i]= (int) (dist[i] * getStock(i) * 3);
		}
		super.reserves = reserve;

	}

}


