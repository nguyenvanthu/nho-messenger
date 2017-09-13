package com.nho.server.statics;

import java.util.ArrayList;
import java.util.List;

public class Counter {
	private static int threshold = 500;

	public static int getThreshold() {
		return threshold;
	}

	public static void setThreshold(int value) {
		threshold = value;
	}

	private static int interaction = 1;
	private static List<Integer> counters = new ArrayList<>();

	private static void addCounter() {
		counters.add(Counter.getInteraction());
	}

	private static void clearCounters() {
		counters = new ArrayList<>();
	}

	private static int getAverageCounter() {
		int sum = 0;
		for (Integer value : counters) {
			sum += value;
		}
		return sum / 6;
	}

	private static boolean isUpdateThreshold() {
		if (counters.size() == 6) {
			return true;
		}
		return false;
	}

	public static void updateThreshold() {
		Counter.addCounter();
		if (isUpdateThreshold()) {
			Counter.setThreshold(getAverageCounter());
			Counter.clearCounters();
		}
	}

	public static void incrementInteraction() {
		interaction += 1;
	}

	public static int getInteraction() {
		return interaction;
	}

	public static void resetInteraction() {
		interaction = 1;
	}
}
