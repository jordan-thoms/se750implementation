package angil.test;

import java.util.ArrayList;
import java.util.Random;

public class TestRandom {

	static int counter5 = 0;
	static int counter3 = 0;
	static int counter2 = 0;

	public static void main(String[] args) {
		ArrayList<Double> probabilities = new ArrayList<Double>();
		probabilities.add(0.5);
		probabilities.add(0.3);
		probabilities.add(0.2);

		TestRandom tr = new TestRandom();
		for (int i = 0; i < 1000000; i++) {
			Random rd = new Random();
			double r = rd.nextDouble();
			for (int j = 0; j < probabilities.size(); j++) 
			{
				r -= probabilities.get(j);
				if (r < 0 && probabilities.get(j) == 0.5) {
					counter5++;
					break;
				}
				if (r < 0 && probabilities.get(j) == 0.3) {
					counter3++;
					break;
				}
				if (r < 0 && probabilities.get(j) == 0.2) {
					counter2++;
					break;
				}
			}
		}
		// for (int i = 0; i < 10000; i++) {
		// tr.stochasticallySelectNextAction1(probabilities);
		// }
		tr.display();
	}

	public void stochasticallySelectNextAction1(ArrayList<Double> inputlist) {
		Random stochasticValue = new Random();
		double ranDouble = stochasticValue.nextDouble();
		for (int i = 0; i < inputlist.size(); i++) {
			double d = inputlist.get(i);
			ranDouble -= d;
			if (ranDouble < 0 && d == 0.5) {
				counter5++;
				break;
			}
			if (ranDouble < 0 && d == 0.3) {
				counter3++;
				break;
			}
			if (ranDouble < 0 && d == 0.3) {
				counter3++;
				break;
			}
		}
	}

	public void display() {
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
