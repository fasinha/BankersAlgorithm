import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SinhaFrameTable 
{
	SinhaPage[] ft;
	int[] frameindices;
	ArrayList<SinhaPage> pagelist;
	
	private int largestfreeframe; //index of largest free frame in ft
	
	
	public SinhaFrameTable(int fsize)
	{
		this.ft = new SinhaPage[fsize];
		this.frameindices = new int[fsize];
		this.largestfreeframe = fsize-1;
		
	}
	
	public int findProcess(SinhaProcess p)
	{
		for (SinhaPage f : ft)
		{
			if (f.getPageProcess().getID() == p.getID() && f.getPageProcess().currentpage == p.currentpage)
			{
				return 1;
			}
		}
		return -1;
	}
	
	public void setLargestFreeFrame()
	{
		for (int i = ft.length-1; i >= 0; i--)
		{
			if (ft[i] == null)
			{
				this.largestfreeframe = i;
				//return i;
			}
		}
		this.largestfreeframe = -1;
		//return -1;
	}
	
	public int getLargestFreeFrame()
	{
		return this.largestfreeframe;
	}
	
	public int lru_replace()
	{
		return 0;
	}
	
	public int lifo_replace()
	{
		return 0;
	}
	
	public int random_replace()
	{
		Scanner s = new Scanner("random-numbers.txt");
		int random = s.nextInt();
		return random % ft.length; //returns the index of the randomly picked frame
	}
}
