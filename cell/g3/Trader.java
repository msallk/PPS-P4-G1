package cell.g3;

public class Trader
{
	Player player;
	
	public Trader(Player p)
	{
		player = p;
	}
	
	//TODO check that we're not trading away marbles we dont have (ie illegal trade)
	// If there is a winningTrade, return true and set player-level request and give arrays accordingly.
	// Else return false.
	public Boolean winningTrade(double[] rate, int[] request, int[] give)
	{
		double giveVal = 0;
		double requestVal = 0;

		for (int i = 0; i < rate.length; i++) {
			if (player.savedSack[i] > player.quadCount) {
				give[i] = player.savedSack[i] - player.quadCount;
				request[i] = 0;
			} else {
				give[i] = 0;
				request[i] = player.quadCount - player.savedSack[i];
			}

			giveVal += give[i] * rate[i];
			requestVal += request[i] * rate[i];
		}

		if (giveVal >= requestVal && giveVal > 0) {
//			System.out.println("DEBUG Winning Trade " + giveVal + " " + requestVal);
			return true;
		}

		return false;
	}

	// DEPRECATED
	// If some marbles are below threshold, we need to save the player even if it is a lossy trade.
	//    return true and set player-level request and give arrays accordingly.
	// Else return false.
	public Boolean savingTrade(double[] rate, int[] request, int[] give)
	{
		Boolean save = false;
		double giveVal = 0;
		double requestVal = 0;
		int i;

		for (i = 0; i < rate.length; i++) {
			if (player.savedSack[i] <= player.minNextThreshold[i]) {
				save = true;
				break;
			}
		}

		if (save) {
			for (i = 0; i < rate.length; i++) {
				if (player.savedSack[i] <= player.minNextThreshold[i]) {
					request[i] = player.maxNextThreshold[i] - player.savedSack[i];
					requestVal += request[i] * rate[i];
					give[i] = 0;
				} else {
					request[i] = 0;
					give[i] = 0;
				}
			}

			for (i = 0; i < rate.length; i++) {
				if (player.savedSack[i] > player.maxNextThreshold[i]) {
					give[i] = (int)((requestVal - giveVal) / rate[i]) + 1;

					if (give[i] > player.savedSack[i] - player.maxNextThreshold[i])
						give[i] = player.savedSack[i] - player.maxNextThreshold[i];

					giveVal += give[i] * rate[i];
				}
			}

			if (giveVal >= requestVal && giveVal > 0) {
//				System.out.println("DEBUG Saving Trade " + giveVal + " " + requestVal);
				return true;
			}
		}

		return false;
	}

	private Boolean needToSave(int[] sack)
	{
		for (int i = 0; i < sack.length; i++) {
			if (sack[i] < player.minNextThreshold[i]) //was max
				return true;
		}
		return false;
	}

	public void greedyTrade(double[] rate, int[] request, int[] give)
	{
		int minColor = player.minIndex(rate);
		double giveVal = 0;
		double requestVal = 0;
		int colors[] = new int[rate.length];
		int changedSack[] = new int[rate.length];
		int giveColor, giveCount, reqColor, reqCount;
		int i;

		for (i = 0; i < rate.length; i++) {
			changedSack[i] = player.savedSack[i];
			request[i] = 0;
			give[i] = 0;
		}

		while (needToSave(changedSack)) {
			colors = sortedColors(changedSack, rate);
			giveColor = colors[0];

			for (i = rate.length-1; i > 0; i--) {
				reqColor = colors[i];

				if ((changedSack[reqColor] < player.minNextThreshold[reqColor]) &&
					(changedSack[giveColor] >= player.minNextThreshold[giveColor] + 1) &&
					(requestVal + rate[reqColor] <= giveVal + 
						((changedSack[giveColor]-player.minNextThreshold[giveColor]-1)*rate[giveColor]))) {
					request[reqColor]++;
					changedSack[reqColor]++;
					requestVal += rate[reqColor];
					giveCount = (int)((requestVal - giveVal) / rate[giveColor]) + 1;
					give[giveColor] += giveCount;
					changedSack[giveColor] -= giveCount;
					giveVal += giveCount * rate[giveColor]; 
				}
			}	
		}

//		System.out.print("DEBUG Init ");
		for (i = 0; i < rate.length; i++)
//			System.out.print(player.savedSack[i] + " ");
//		System.out.println();
//		System.out.print("DEBUG Give ");
		for (i = 0; i < rate.length; i++)
//			System.out.print(give[i] + " ");
//		System.out.println();
//		System.out.print("DEBUG Receive ");
		for (i = 0; i < rate.length; i++)
//			System.out.print(request[i] + " ");
//		System.out.println();
//		System.out.print("DEBUG Net ");
		for (i = 0; i < rate.length; i++)
//			System.out.print(changedSack[i] + " ");
//		System.out.println();

		for (i = 0; i < rate.length; i ++) {
			if (i == minColor || changedSack[i] <= player.minNextThreshold[i]) {}
			else {
				giveCount = changedSack[i] - player.minNextThreshold[i];
				give[i] += giveCount;
				reqCount = (int)(giveCount * rate[i] / rate[minColor]);
				request[minColor] += reqCount;
				giveVal += giveCount * rate[i];
				requestVal += reqCount * rate[minColor];
			}
		}

//		System.out.println("DEBUG Greedy Trade " + giveVal + " " + requestVal);
	}

	private double maxValue(double[] a)
	{
		double maxValue = a[0];
		for (int i = 0; i < a.length; i++)
		{
			if(a[i] > maxValue)
				maxValue = a[i];
		}
		return maxValue;
	}

	// Sort the colors from least valued to most valued.
	// Least valued colors can be given away during a trade.
	private int[] sortedColors(int[] sack, double[] rate)
	{
		int[] colors = new int [sack.length];
		double[] value = new double [sack.length];
		double replace;
		int i;

		for (i = 0; i < sack.length; i++)
			value[i] = (player.minNextThreshold[i] - sack[i]) * rate[i];	// VALUE FUNCTION.

		replace = maxValue(value) + 1;

		for (i = 0; i < sack.length; i++)
		{
			colors[i] = player.minIndex(value);
			value[colors[i]] = replace;
		}

		return colors;
	}
}