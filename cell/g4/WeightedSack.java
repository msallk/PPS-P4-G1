package cell.g4;

public class WeightedSack extends Sack {
	private int[] center = new int[2];
	
	public WeightedSack(int[] sack, Board board) {
		super(sack, board);
		center[0] = center[1] = board.getBoard().length / 2;
		super.reserves = calcWeightedReserve(center);
		for (int i = 0; i < 6; i++) {
			System.out.println("reserve " + i + ": " + reserves[i]);
		}
	}

	protected double weight(int[] center, int i, int j) {
		int longest = board.mindist(center, new int[]{0,0});
		
		int dist = board.mindist(center, new int[]{i,j});
		
		return longest - dist; 
	}
	
	protected int[] calcWeightedReserve(int[] center) {
		int[] reserves = new int[6];		
		double[] dist= new double[6];
		
		int[][] map = board.getBoard();
		for(int i=0; i < map.length; i++){
			for(int j=0; j < map[0].length; j++){
				switch (map[i][j]) {
				case 0:
					dist[0] += weight(center, i, j);
					break;
				case 1:
					dist[1] += weight(center, i, j);
					break;
				case 2:
					dist[2] += weight(center, i, j);
					break;
				case 3:
					dist[3] += weight(center, i, j);
					break;
				case 4:
					dist[4] += weight(center, i, j);
					break;
				case 5:
					dist[5] += weight(center, i, j);
					break;
				default:
					break;
				}
			}
		}
		
		dist = normalize(dist);
		
		for(int i=0; i < reserves.length; i++){
			reserves[i]= (int) (dist[i] * InitialMarble * 2);
		}
		return reserves;
	}
	
	protected double[] normalize(double[] reserves) {
		double sum = 0;
		for (int i = 0; i < reserves.length; i++)
			sum += reserves[i];

		for (int i = 0; i < reserves.length; i++)
			reserves[i] = reserves[i] / sum;
		return reserves;
	}
	
	
	@Override
	public int getReserve(int color) {			
		return reserves[color];
	}	
}
