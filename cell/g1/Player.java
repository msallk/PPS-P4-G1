package cell.g1;

public class Player implements cell.sim.Player, Logger {
	boolean DEBUG=true;
	
	public void log(String message){
		if(DEBUG)
			System.err.println(message);
	}
	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Direction move(int[][] map, int[] location, int[] sack,
			int[][] players, int[][] traders) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void trade(double[] rate, int[] request, int[] give) {
		// TODO Auto-generated method stub
		
	}

}
