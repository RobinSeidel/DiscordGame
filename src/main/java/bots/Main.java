package bots;

public class Main {

	public static void main(String[] args) {
		new Thread(() -> TestingBot.main(null)).start();
		new Thread(() -> Snake.main(null)).start();
		new Thread(() -> CuteBot.main(null)).start();
	}

}
