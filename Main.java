import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException
	{
		File input = new File(args[0]);
		Scanner scan = new Scanner(input);
		String first = scan.nextLine();
		String[] split = first.split(" ");
		int numtasks = Integer.parseInt(split[0]);
		int numresources = Integer.parseInt(split[1]);
		
		int[] resourcelist = new int[numresources+1];
		
		ArrayList<Task> tasklist = new ArrayList<Task>();
		ArrayList<Task> bankerlist = new ArrayList<Task>();
		
		for (int i = 0; i < numtasks; i++)
		{
			Task t = new Task(i+1, numresources);
			Task btask = new Task(i+1, numresources);
			tasklist.add(t);
			bankerlist.add(btask);
			
		}
		
		resourcelist[0] = 0;
		for (int i = 1; i < resourcelist.length; i++)
		{
			resourcelist[i] = Integer.parseInt(split[i+1]);
			
		}
		
		while (scan.hasNext())
		{
			ArrayList<Action> instructionlist = new ArrayList<Action>();
			String act = scan.next(); //activity 
			int a = Integer.parseInt(scan.next()); //tasknumber
			int b = Integer.parseInt(scan.next()); 
			int c = Integer.parseInt(scan.next());
			Action action = new Action(act, a, b, c); 
			
			Task t = tasklist.get(a-1);
			Task other = bankerlist.get(a-1);
			t.getList().add(action);
			other.getList().add(action);
		}
		
		
		
		
		int[] bankerresourcelist = new int[numresources+1];
		for (int i = 1; i < numresources+1; i++)
		{
			bankerresourcelist[i] = resourcelist[i];
		}
		
		//----------------------------------------------------------------
		//RUN OPTIMISTIC
		
		
		//create an instance of the optimistic resource manager 
		Optimistic o = new Optimistic(numtasks, numresources, resourcelist);
		o.run(tasklist); //run the manager 
		
		//Print the output
		System.out.println("\tFIFO");
		int total = 0;
		int wait = 0; 
		for (Task t : tasklist)
		{
			System.out.print("Task " + t.getID() + "      ");
			if (t.aborted == true)
			{
				System.out.print("aborted");
				System.out.println();
			}
			else {
				total += Integer.parseInt(t.results[0]);
				wait += Integer.parseInt(t.results[1]);
				t.printResults();
				System.out.println();
			}
		}
		//System.out.println("total= " + total);
		//String percent = Math.round((((double) wait / (double) total ) * 100)) + "%";
		int percent = (int) Math.round((double) wait/total * 100);
		//System.out.print("percent is " + percent);
		String p2 = percent + "%";
		System.out.print("Total       " + total + "   " + wait + "   " + p2);
		//System.out.printf("Total       " + total+"  "+wait+"   %.0f",((double)wait/ (double)total) * 100) ;
		//System.out.print("%");
		
		System.out.println();
		System.out.println();
		
		//--------------------------------------------------------------------------
		//RUN BANKER 
		
		BankerNew b = new BankerNew(numtasks, numresources, bankerresourcelist);
		b.run(bankerlist); //run the manager 
		
		//Print the output
		System.out.println("\tBanker");
		int bankertotal = 0;
		int bankerwait = 0; 
		for (Task t : bankerlist)
		{
			System.out.print("Task " + t.getID() + "      ");
			if (t.aborted == true)
			{
				System.out.print("aborted");
				System.out.println();
			}
			else {
				bankertotal += Integer.parseInt(t.results[0]);
				bankerwait += Integer.parseInt(t.results[1]);
				t.printResults();
				System.out.println();
			}
		}
		//System.out.println("total= " + total);
		int bankerpercent = (int) Math.round((double) bankerwait/bankertotal * 100);
		String bp = bankerpercent + "%";
		//String bankerpercent = Math.round((((double) bankerwait / (double) bankertotal ) * 100)) + "%";
		System.out.print("Total       " + bankertotal + "   " + bankerwait + "   " + bp);
		//System.out.printf("Total       " + bankertotal+"  "+bankerwait+"   %.0f",((double)bankerwait/ (double)bankertotal) * 100) ;
		//System.out.print("%");
	}
}
