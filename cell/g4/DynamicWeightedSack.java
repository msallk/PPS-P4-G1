package cell.g4;

public class DynamicWeightedSack extends WeightedSack {
	private int longest = -1;
	
	public DynamicWeightedSack(int[] sack, Board board) {
		super(sack, board);	
	}

	@Override
	protected double weight(int[] center, int i, int j) {
		int dist = board.mindist(center, new int[]{i,j});
		
		return longest - dist; 
	}
	
	private int getLongestDist(int[] loc) {
		int[][] map = board.getBoard();
		
		int longest = 0;
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[0].length; j++)			
				if (map[i][j]>=0 &&  board.mindist(loc, new int[]{i,j}) > longest)
					longest = board.mindist(loc, new int[]{i,j});
		return longest;
	}
	
	@Override
	public void update(int[] sack, int[] loc) {
		super.update(sack, loc);
		longest = getLongestDist(loc);
		reserves = calcWeightedReserve(loc);
		for (int i = 0; i < 6; i++)
			System.out.println(reserves[i]);
	}
			
	@Override
	public int getReserve(int color) {			
		return reserves[color];
	}	
}
