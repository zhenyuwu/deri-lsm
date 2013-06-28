import java.util.*;

public class EventExample {
	// this is our custom event
	// we should probably always extent EventObject for
	// such objects
	class DepositBoxChangedEvent extends EventObject {
		public DepositBoxChangedEvent(Object source) {
			super(source);
		}
	}

	// our listener interface
	// we extend EventListener because it makes the Java gods happy
	interface DepositBoxChangedListener extends EventListener {
		void depositBoxChanged(DepositBoxChangedEvent e);
	}
	
	// An interface implemented by the objects our listener wants to listent to
	interface DepositBoxEventGenertor {
		void addDepositBoxChangedListener(DepositBoxChangedListener listener);
	}
	
	
	class DepositBox implements DepositBoxEventGenertor  {
		private String name = null;
		private double amount = 0;
		private ArrayList<DepositBoxChangedListener> depositBoxChangedListeners = 
			new ArrayList<DepositBoxChangedListener>();
		
		public DepositBox(String name) {
			this.name = name;
		}
		
		public String getName() { return this.name; }
		public double getAmount() { return this.amount; }
		public void setAmount(double value) { 
			if (value!=this.amount) {
				this.amount = value;
				FireDepositBoxChangedEvent(new DepositBoxChangedEvent(this));
			}
		}
		
		private void FireDepositBoxChangedEvent(DepositBoxChangedEvent e) {
			int j = depositBoxChangedListeners.size();
			if (j==0) { return; }
			for(int i=0; i<j; i++) {
				depositBoxChangedListeners.get(i).depositBoxChanged(e);
			}
		}
			
		public void addDepositBoxChangedListener(DepositBoxChangedListener listener) {
			depositBoxChangedListeners.add(listener);
		}
	}
	
	
	class Vault implements DepositBoxChangedListener {
		private ArrayList<DepositBox> depositBoxes = new ArrayList<DepositBox>();
		public Vault() { }
		public void addBox(DepositBox box) {
//			this.depositBoxes.add(box);
			box.addDepositBoxChangedListener(this);
		}
		
//		public DepositBox [] getBoxes() { 
//			DepositBox [] a = new DepositBox[this.depositBoxes.size()];
//			this.depositBoxes.toArray(a);
//			return a;
//		}
		
		public void depositBoxChanged(DepositBoxChangedEvent e) {
			DepositBox box = (DepositBox)e.getSource();
			System.out.println("BoxChanged: " + box.name + " = " + box.getAmount());
		}
	}
	
	public void runExample() {
		Vault vault = new Vault();
		DepositBox b1 = new DepositBox("b1");
		b1.setAmount(100);
		vault.addBox(b1);
		DepositBox b2 = new DepositBox("b2");
		vault.addBox(b2);
		b2.setAmount(100);
		b1.setAmount(300);
		b2.setAmount(100);
	}

	public static  void main(String s[] ){
		new EventExample().runExample();
	}
}

