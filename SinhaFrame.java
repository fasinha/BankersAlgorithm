
public class SinhaFrame
{
	int id;
	boolean isEmpty;
	int lastUse;
	int recentUse;
	SinhaProcess p;
	SinhaPage page;
	
	public SinhaFrame()
	{
		isEmpty = true;
		lastUse = -1;
		recentUse = -1;
		//page = -1;
	}
	
	public boolean setInitialProcess(SinhaProcess p)
	{
		isEmpty = false;
		this.p = p;
		this.page = p.getCurrPage();
		return true; 
	}
	
	public SinhaProcess getProcess()
	{
		return p;
	}
}


