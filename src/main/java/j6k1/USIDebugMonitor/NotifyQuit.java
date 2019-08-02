package j6k1.USIDebugMonitor;

public class NotifyQuit {
	private volatile boolean quit = false;

	public void invoke() {
		this.quit = true;
	}

	public boolean isQuit() {
		return this.quit;
	}
}
