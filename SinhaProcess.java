import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
public class SinhaProcess 
{
	private int id;
	private double A; 
	private double B; 
	private double C; 
	int reference; 
	int size; 
	int numOfPages; 
	int residence;
	int evicted;
	SinhaPage currentpage; 
	int currentpagenum;
	int faults;
	
	ArrayList<SinhaPage> pagetable;
	private double y;
	
	public SinhaProcess(int id, double a, double b, double c, int size, int numOfPages)
	{
		this.id = id;
		this.A = a;
		this.B = b;
		this.C = c;
		this.size = size;
		this.numOfPages = numOfPages;
		this.reference = (111*id + this.size) % this.size;
		this.residence =0;
		this.evicted = 0;
		this.currentpagenum = this.reference / this.size;
		this.pagetable = new ArrayList<SinhaPage>();
		//this.currentpage = this.reference / this.size;
	}
	
	public void fillPageTable()
	{
		for (int i = 0; i < numOfPages; i++)
		{
			SinhaPage newpage = new SinhaPage(i, this);
			this.pagetable.add(newpage);
		}
	}
	
	public SinhaPage getCurrPage()
	{
		this.currentpagenum = this.reference / this.size;
		this.currentpage = this.pagetable.get(currentpagenum);
		return this.currentpage;
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public double getA()
	{
		return this.A;
	}
	
	public double getB()
	{
		return this.B;
	}
	
	public double getC()
	{
		return this.C;
	}
	
	public int getRef()
	{
		return this.reference;
	}
	
	public void nextWordToRef() throws FileNotFoundException
	{
		File f = new File("random-numbers.txt");
		Scanner scan1 = new Scanner(f);
		String rstr = scan1.next();
		//System.out.println(rstr);
		int r = Integer.parseInt(rstr);
		y = r / (Integer.MAX_VALUE + 1d);
		if (y < this.A)
		{
			this.reference = (this.reference + 1 + this.size) % this.size;
		}
		else if (y < this.A + this.B)
		{
			this.reference = (this.reference - 5 + this.size) % this.size;
		}
		else if (y < this.A + this.B + this.C)
		{
			this.reference = (this.reference + 4 + this.size) % this.size;
		}
		else 
		{
			int new_random = scan1.nextInt();
			this.reference = new_random % this.size;
		}
		
		this.currentpagenum = this.reference / this.size;
		this.currentpage = this.pagetable.get(currentpagenum);
	}
}
