package cell.g4;

public class BoardAnalyzer {

	public double[] getColorDistribution(int[][] board){
		double[] color= new double[6];
		for(int i=0; i < board.length ; i++){
			for(int j=0; j < board[0].length; j++){
				switch (board[i][j]) {
				case 0:
					color[0]++;
					break;
				case 1:
					color[1]++;
					break;
				case 2:
					color[2]++;
					break;
				case 3:
					color[3]++;
					break;
				case 4:
					color[4]++;
					break;
				case 5:
					color[5]++;
					break;
				default:
					break;
				}
			}
		}
		
		int sum=0;
		for(int i=0;i<color.length;i++){
			sum+=color[i];
		}
		
		for(int i=0; i <color.length;i++){
			color[i] /= sum;
		}
		return color;
	}
	
	public int[] marbleCostBetweenAnB(Board board, int[] pointA, int[] pointB){
		return null;
	}
	
	public int dimensionOfMap(int[][] board){
		return board.length;
	}
	//based on current location, the value of marble changes


//	public int Lepercaun


}
