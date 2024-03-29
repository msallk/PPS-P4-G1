

package cell.g2;

import java.util.LinkedList;
import java.util.Vector;

public class Trader
{
	private int coordinates[][];
	private int no_of_traders;
	private int range =0; 

	Trader(int traders[][])
	{
		no_of_traders = traders.length;
		coordinates = new int[traders.length][traders[0].length];
		for(int i=0;i<no_of_traders;i++)
		{
			for(int j=0;j<traders[i].length;j++)
			{
				coordinates[i][j] = traders[i][j];
			}
		}
		range =2;
		int r = Player.board_size / 3 ;
		if(range<r)
			range = r;
	}

	int getBestTrader(int location[],Floyd shortest)
	{
		int rank[] = new int[no_of_traders];
		for(int i=0;i<no_of_traders;i++)
		{
			Vector<Integer> path = shortest.getShortestPossiblePath(location[0], location[1], coordinates[i][0], coordinates[i][1]);
			if(path != null)
				rank[i]=path.size()+1;
			else
				rank[i]=50;
			boolean visited[] =new boolean[no_of_traders];
			rank[i] = rank[i] - getClusterScore(i,visited,shortest);
		}
		int min = 0;
		for(int i=1;i<no_of_traders;i++)
		{
			if(rank[min] > rank[i])
				min=i;
		}
		return min;
	}
	
	/*
	 * 	int getBestTrader(int location[],Floyd shortest, OtherPlayers opponents,int turns)
	{
		int rank[] = new int[no_of_traders];
		int t[] = new int[no_of_traders];
		for(int i=0;i<no_of_traders;i++)
		{
			Vector<Integer> path = shortest.getShortestPossiblePath(location[0], location[1], coordinates[i][0], coordinates[i][1]);
			if(path != null)
				rank[i]=path.size()+1;
			else
				rank[i]=50;
			boolean visited[] =new boolean[no_of_traders];
			int score =getClusterScore(i,visited,shortest);
			Print.printStatement("SCORE : "+score);
			rank[i] = rank[i] - score;
			t[i] = i;
		}
		int min = 0;
		for(int i=1;i<no_of_traders;i++)
		{
			for(int j=i+1;j<no_of_traders;j++)
			{
				if(rank[i] > rank[j])
				{
					int x = rank[j];
					rank[j]=rank[i];
					rank[i]=rank[j];
					int y = t[j];
					t[j]=t[i];
					t[i]=t[j];
				}
			}
		}
		boolean f =true;
		if(turns>3)
		{
			Print.printStatement("DONE1234567890");
			for(int i =0;i<no_of_traders;i++)
			{
				Print.printStatement("TRADER NUMBER : "+t[i]+"   "+rank[i]);
				f = opponents.checkPlayerMovement(t[i], shortest, coordinates);
				//			Print.printStatement("DONE");
				if(!f)
				{
					min = t[i];
					System.out.println("TRADER : "+min);
					return min;
				}
			}
		}
		System.out.println("TRADER : "+t[min]);
		return t[min];
	}
	*/

	int getClusterScore(int i, boolean visited[],Floyd shortest)
	{
		visited[i]=true;
		LinkedList<Integer> near = getNearByTraders(i,visited,shortest);
		if(near.size() == 0)
			return 1;
		int r[] = new int[no_of_traders];
		for(int j=0;j<near.size();j++)
		{
			r[(int)near.get(j)] = getClusterScore(near.get(j),visited,shortest)+1;
		}
		int min = 0;
		for(int f=1;f<no_of_traders;f++)
		{
			if(r[min] > r[f])
				min=f;
		}
		return r[min];
	}

	LinkedList<Integer> getNearByTraders(int i,boolean[] visited, Floyd shortest)
	{
		LinkedList<Integer> near = new LinkedList<Integer>();
		for(int x=0;x<no_of_traders;x++)
		{
			if(!visited[x])
			{
				int size = shortest.getShortestPossiblePath(coordinates[i][0], coordinates[i][1], coordinates[x][0], coordinates[x][1]).size()+1;
				if(size<= range)
					near.add(x);
			}
		}
		return near;
	}
}