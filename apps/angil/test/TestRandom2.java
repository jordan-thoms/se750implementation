package angil.test;

import java.util.ArrayList;
import java.util.Random;

public class TestRandom2 {

	static int counter5 = 0;
	static int counter3 = 0;
	static int counter2 = 0;

	public static void main(String[] args) {
		ArrayList<Double> probabilities = new ArrayList<Double>();
		probabilities.add(0.5);
		probabilities.add(0.3);
		probabilities.add(0.2);

		TestRandom2 tr = new TestRandom2();
		for (int i = 0; i < 1000000; i++) {
			tr.stochasticallySelectNextAction1(probabilities);
		}
		// for (int i = 0; i < 10000; i++) {
		// tr.stochasticallySelectNextAction1(probabilities);
		// }
		TestRandom2.display();
	}

	public void stochasticallySelectNextAction1(ArrayList<Double> inputlist) {
		Random stochasticValue = new Random();
		double ranDouble = stochasticValue.nextDouble();
		for (double d : inputlist) {
			ranDouble -= d;
			if (ranDouble < 0) {
				if (d == 0.5)
					counter5++;
				if (d == 0.3)
					counter3++;
				if (d == 0.2)
					counter2++;
				break;
			}
		}
	}

	public static void display() {
		System.out.println("counter -> " + counter5);
		System.out.println("counter -> " + counter3);
		System.out.println("counter -> " + counter2);

	}

	public double average(ArrayList<Double> ll) {
		double out = 0.0;
		double total = 0.0;
		for (double d : ll) {
			total += d;
		}
		out = total / (ll.size());
		return out;
	}

}
