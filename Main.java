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
		//tasklist.add(null);
		for (int i = 0; i < numtasks; i++)
		{
			Task t = new Task(i+1, numresources);
			tasklist.add(t);
			//System.out.println( "task id" + tasklist.get(i).getID() );
		}
		//System.out.println(tasklist.size());
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
			//System.out.println(a);
			Task t = tasklist.get(a-1);
			t.getList().add(action);
		}
		
		/*
		for (Task t : tasklist)
		{
			System.out.println("Task " + t.getID() + " --" + t.getList().toString());
		} */
		
		Optimistic o = new Optimistic(numtasks, numresources, resourcelist);
		o.run(tasklist);
	}
}
