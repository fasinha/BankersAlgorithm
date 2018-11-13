
public class Action 
{
	String activity; 
	int a; 
	int b;
	int c;
	
	public Action(String activity, int a, int b, int c)
	{
		this.activity = activity;
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public String getActivity()
	{
		return this.activity;
	}
	
	public int getA()
	{
		return this.a;
	}
	
	public int getB()
	{
		return this.b;
	}
	
	public int getC()
	{
		return this.c;
	}
	
	public String toString()
	{
		String str = activity + " " + a + " " + b + " " + c;
		return str;
	}
}
