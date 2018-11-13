import java.util.*;

public class Banker 
{
	int numoftasks;
	int numresources;
	int[] resourcelist;
	
	ArrayList<Task> active;
	ArrayList<Task> blocked; 
	ArrayList<Task> ok; 
	
	int[] justReleased;
	
	public Banker(int numoftasks, int numresources, int[] resourcelist)
	{
		this.numoftasks = numoftasks;
		this.numresources = numresources;
		this.resourcelist = resourcelist;
		active = new ArrayList<Task>();
		blocked = new ArrayList<Task>();
		ok = new ArrayList<Task>();
		justReleased = new int[numresources+1];
	}
	
	public void run(ArrayList<Task> tasklist)
	{
		int currentcycle = 0;
		//Task current; 
		
		for (Task t : tasklist)
		{
			active.add(t);
		}
		
		while (active.size() > 0)
		{
			for (int i = 0; i < active.size(); i++)
			{
				Task current = active.get(i);
				int index = current.getIndex(); 
				//System.out.println(index);
				Action act = current.getList().get(index);
				if (current.getComputeTime() == 0)
				{
					if ( act.getActivity().equals("initiate"))
					{
						System.out.println("initiated task " + current.getID());
						int r = act.getB(); //resource type 
						
						if(act.getC() > resourcelist[r])
						{
							current.abort(currentcycle);
						}
						else {
							current.resourcesNeed[r] = act.getC(); //set resources need to amount needed
							current.currentindex +=1; 
							ok.add(current);
						}
						
					}
					else if ( act.getActivity().equals("request"))
					{
						System.out.println("request task " + current.getID());
						request(current, act);
					}
					else if ( act.getActivity().equals("release"))
					{
						release(current, act);
					}
					else if ( act.getActivity().equals("terminate"))
					{
						current.terminate(currentcycle);
						System.out.println("terminating curr cycle " + currentcycle);
						current.results[0] = currentcycle + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) (((double) current.waiting / (double) (currentcycle) ) * 100) + "%";
					} 
					else if ( act.getActivity().equals("compute"))
					{
						current.setComputeTime(act.getB());
						current.currentindex += 1;
						//current.incIndex();
						//act = current.getList().get(index);
						if (current.isDone() && current.getComputeTime() == 0)
						{
							current.terminate(currentcycle);
							System.out.println("terminating computing curr cycle " + currentcycle);
							current.results[0] = currentcycle + "";
							current.results[1] = current.waiting + "";
							current.results[2] = (int) (((double) current.waiting / (double) currentcycle ) * 100) + "%";
						}
					}
				}
				else {
					//the task is currently computing
					//System.out.println("hola");
					//current.setComputeTime(current.getComputeTime()-1);
					current.compute -=1;
					if (current.getComputeTime() == 0 && current.isDone())
					{
						current.terminate(currentcycle);
						current.finish = currentcycle;
						current.terminated = true;
						current.results[0] = currentcycle + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) (((double) current.waiting / (double) current.finish ) * 100) + "%";
					}
				}
		
			}
		}
	}

	public void request(Task current, Action act) 
	{
		int resource = act.getB();
		
		int amtrequested = act.getC();
		
		int available = resourcelist[resource];
		System.out.println("amt requested by task " + current.getID() + " " + amtrequested);
		
		
	}
	
}
