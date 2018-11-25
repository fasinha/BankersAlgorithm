
public class SinhaPage 
{
	private int num;
	private int frame;
	private SinhaProcess p;
	private int lastref;
	
	public SinhaPage(int num, SinhaProcess p)
	{
		this.num = num;
		this.p = p;
	}
	
	public void setFrame(int f)
	{
		this.frame = f;
	}
	
	public SinhaProcess getPageProcess()
	{
		return this.p;
	}
	
	public int getFrameFromPage()
	{
		return this.frame;
	}
	
	public int getLastRef()
	{
		return this.lastref;
	}
	
	public void setLastRef(int ref)
	{
		this.lastref = ref;
	}
}
