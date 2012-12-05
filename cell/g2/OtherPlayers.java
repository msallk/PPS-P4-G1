package cell.g2;

import java.util.LinkedList;
import java.util.Vector;

class Position
{
	int x;
	int y;
	Position(int a,int b)
	{
		x=a;
		y=b;
	}
	int getX()
	{
		return x;
	}
	int getY()
	{
		return y;
	}
}
public class OtherPlayers 
{
	int n =0;
	int our_player_number = -1;
	LinkedList<LinkedList<Position>> previous_positions = new LinkedList<LinkedList<Position>>();
	
	OtherPlayers(int num)
	{
		n=num;
		for(int i = 0 ; i < n ;i++)
		{
			previous_positions.add(new LinkedList<Position>());
		}
	}
	
	void getOurPlayerNumber(int players[][], int location[])
	{
		int cnt =0;
		for(int i =0;i<n;i++)
		{
			if(players[i][0] == location[0] && players[i][1] == location[1])
			{
				our_player_number = i;
				cnt++;
			}
		}
		if(cnt >1)
			our_player_number = -1;
	}
	void addCurrentPosition(int players[][], int location[])
	{
		getOurPlayerNumber(players , location);
		Print.printStatement("\n"+n+"\n");
		for(int i = 0 ; i < n ;i++)
		{
			previous_positions.get(i).add(new Position(players[i][0],players[i][1]));
		}
	}
	boolean isPlayerMovingTowards(int i,int trader_number,int traders[][])
	{
		//CHECK FOR MOVEMENT OF PLAYER i
		Print.printStatement("TRADER NO : "+i);
		double distance[]={9999,9999,9999};
		boolean flag =true;
		for(int j=0 ; j<3 && previous_positions.get(i).size()-j-1>0; j++)
		{
			Print.printStatement("CHECK");
			Print.printStatement(previous_positions.get(i).size()-j-1+"");
			Print.printStatement("CHECK123254");
			distance[j]=getDistance(previous_positions.get(i).get(previous_positions.get(i).size()-j-1) , traders[trader_number]);
		}
		Print.printStatement("WATEVER");
		for(int j=0;j<2;j++)
		{			if(distance[j]>distance[j+1])
				flag=false;
		}
		return flag;
	}
	double getDistance(Position p,int y[])
	{
		return Math.sqrt((p.getX()-y[0])*(p.getX()-y[0]) + (p.getY()-y[1])*(p.getY()-y[1]));
		
	}
	boolean checkPlayerMovement(int trader_number,Floyd shortest, int traders[][])
	{
		if(our_player_number == -1)
			return false;
		for(int i=0;i<n;i++)
		{
			if(i==our_player_number)
				continue;
			boolean flag;
			Print.printStatement("MOVING");
			flag = isPlayerMovingTowards(i,trader_number,traders);
			Print.printStatement("MOVING");
			Print.printStatement(flag+"");
			if(flag)
			{
				Vector<Integer> v = shortest.getShortestPath(previous_positions.get(i).get(previous_positions.get(i).size()-1).getX(), traders[trader_number][0] , previous_positions.get(i).get(previous_positions.get(i).size()-1).getY() ,traders[trader_number][1]);
				Vector<Integer> v1 = shortest.getShortestPath(previous_positions.get(our_player_number).get(previous_positions.get(our_player_number).size()-1).getX(), traders[trader_number][0] , previous_positions.get(our_player_number).get(previous_positions.get(our_player_number).size()-1).getY() ,traders[trader_number][1]);
				if(v.size()< v1.size())
					return true;
			}
		}
		return false;
	}
}
