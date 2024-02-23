import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;
public class Vase {
	public static Lock room;
	public static int guests = 20;
	public static ConcurrentLinkedQueue<Integer> queue;
	
	public static void main(String[] args) throws InterruptedException {
		room = new ReentrantLock();
		queue = new ConcurrentLinkedQueue<Integer>();
		Thread threads[] = new Thread[guests];
		for(int i = 0; i < guests; i++) {
			threads[i] = new Thread((new Runnable() {
				//passthough needed vars to function
				int i;
				public void run() {
					thread(i);
				}
			     public Runnable pass(int i) {
			           this.i = i;
			           return this;
			     }
			    
			}).pass(i));
			threads[i].start();
		}
		for(int i = 0; i < guests; i++) {
			threads[i].join();
		}
		System.out.println("Queue is finished");
	}
	
	public static void thread(int i) {
		Random rand = new Random();
		queue.add(i);
		while(!queue.isEmpty() && queue.contains(i)) {
			if(queue.peek() == i) {
				room.lock();
				try {
					queue.poll();
					System.out.println("Removed from queue " + i);
					if(rand.nextInt(100) == 1) {
						queue.add(i);
					}
				}finally {
					room.unlock();
				}
			}
		}
	}
}