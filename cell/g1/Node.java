package cell.g1;

public class Node {
	int color;
	int x;
	int y;
	Node pi;
	
	public Node(int x, int y, int color){
		this.x=x;
		this.y=y;
		this.color=color;
	}
	
	public void setPi(Node n){
		this.pi=n;
	}
	
	public Node getPi(){
		return pi;
	}
	
	public int getColor(){
		return color;
	}
	
	public int[] getLocation(){
		return new int[]{y,x};
	}
}
