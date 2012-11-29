package cell.g1;

public class TradeAnalyzer {

	Graph g;
	public TradeAnalyzer(Graph g){
		this.g=g;
	}
	
	//TODO: Find the minimum stock for each kind of marble
	public double[] getRatio(int[] location, int[] dest){
		Node[][] nodes = g.getMapnodes();
		int[] db = new int[6];
		for(int i=0; i<6; i++){
			db[i]=0;
		}
		double[] ratio = new double[6];
		for(int i = 0; i<nodes.length; i++){
			System.out.println();
			for(int j=0; j<nodes.length; j++){
				System.out.print(nodes[i][j].getColor());
				switch(nodes[i][j].getColor()){
				case 0: db[0]++;
				   break;
				case 1: db[1]++;
				   break;
				case 2: db[2]++;
				   break;				     
				case 3: db[3]++;
				   break;				
				case 4: db[4]++;
				   break;
				case 5: db[5]++;
				   break;				   
				}
			}			
		}
		int total =0;
		for(int i=0; i<6; i++){
			total+=db[i];
		}
		System.out.println("toal " + total);
		for(int i=0; i<6; i++){
			ratio[i]= db[i]*6.0/total;
		}
		return ratio;
	}
	
}
