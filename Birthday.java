import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.util.Random;
public class Birthday {
	public static Lock maze;
	public static int guests = 20;
	public static void main(String[]args) throws InterruptedException {
		maze = new ReentrantLock();
		AtomicBoolean loop = new AtomicBoolean(true);
		AtomicBoolean cupcake = new AtomicBoolean(true);
		AtomicBoolean[] run = new AtomicBoolean[guests];
		for(int i = 0; i < guests; i++) {
			run[i] = new AtomicBoolean(false);
		}
		System.out.println("Minoutaur Birthday");
		
		//Initializes and run threads
		Thread threads[] = new Thread[guests];
		for(int i = 0; i < guests; i++) {
			threads[i] = new Thread((new Runnable() {
				//passthough needed vars to function
				int i;
				AtomicBoolean[] run;
				AtomicBoolean loop;
				AtomicBoolean cupcake;
				public void run() {
					thread(i, run, loop, cupcake);
				}
			     public Runnable pass(int i, AtomicBoolean[] run, AtomicBoolean loop,AtomicBoolean cupcake) {
			           this.i = i;
			           this.run = run;
			           this.loop = loop;
			           this.cupcake = cupcake;
			           return this;
			     }
			    
			}).pass(i,run, loop, cupcake));
			threads[i].start();
		}
		
		//find rand nunmber
		rand(run, loop);

		//Waits for threads to close
		for(int i = 0; i < guests; i++) {
			threads[i].join();
		}
		System.out.println("All Guests Ate");
	}
	public static void rand(AtomicBoolean[] run, AtomicBoolean loop) {

		Random rand = new Random();
		//random loop
		if(loop.get()) {
			int random = rand.nextInt(guests);
			run[random].set(true);
		}
	}
	public static void thread(int i, AtomicBoolean[] run, AtomicBoolean loop, AtomicBoolean cupcake) {

		int counter = 0;
		boolean eat = false;

		while(loop.get()) {
			if(run[i].get()) {
				maze.lock();
				try {
					//if counter maxes out end the random loop
					if(i == 0 && counter >= guests) {
						loop.set(false);
					}
					
					//if this is the leader guest place new cupcake and increment counter
					if(i == 0 && !cupcake.get()) {
						counter++;
						cupcake.set(true);
					}
					
					//if cupcake is not eaten and have not eaten eat it
					if(cupcake.get() && !eat) {
						eat = true;
						cupcake.set(false);
						System.out.println("Guest " + i + " Ate");
					}
					
					
					run[i].set(false);
					rand(run, loop);
				}finally {
					maze.unlock();
				}
			}
		}
	}
}