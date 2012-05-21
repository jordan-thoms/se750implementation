package fluid;

public class SplashThread extends Thread {
	private static PDXSplash sp;
	
	public SplashThread(){
		sp = new PDXSplash();
	}
	
	public void run(){
		sp.setVisible(true);
	}
	
	public void close(){
		sp.dispose();
	}

}
