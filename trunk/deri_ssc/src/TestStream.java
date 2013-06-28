
public class TestStream extends Thread{
	
	public void run(){
		System.out.print("helo");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestStream t = new TestStream();
		t.start();
		System.out.println(t.isAlive());
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(t.isAlive());
	}
}
