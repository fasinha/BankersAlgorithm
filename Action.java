
/*
 * every instance of this class represents an activity for a particular task 
 */
public class Action 
{
	String activity; //initiate, request, release, terminate, compute
	int a; //task number
	int b; //resource number or blank
	int c; //depending on what the activity is, the meaning of c will change
	
	public Action(String activity, int a, int b, int c)
	{
		this.activity = activity;
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/* 
	 * returns the activity name
	 */
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
