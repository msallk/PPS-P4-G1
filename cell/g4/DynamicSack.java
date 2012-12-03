package cell.g4;

public class DynamicSack extends Sack {
	double[] dist;
	int dimension;
	public DynamicSack(int[] sack,Board board){
		super(sack,board);

		dist = board.getColorDistribution();
		dimension = board.dimension();
		
		reserves = new int[6];
		for(int i=0; i < reserves.length; i++){
			reserves[i]= (int) (dist[i] * InitialMarble * 2);
		}
	}
}


