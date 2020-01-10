package org.ruanwei.demo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Recorder {
	private static Log log = LogFactory.getLog(Recorder.class);

	private static int seq = 0;
	private static final SortedMap<Integer, String> records = new TreeMap<>();
	private static final Map<String, String> holder = new HashMap<>();

	public static synchronized void record(String record, Class<?> clazz) {
		records.put(++seq, record);
		log.info("[record]" + seq + "==========[" + clazz + "]" + record);
	}

	public static void printRecords() {
		for (Integer key : records.keySet()) {
			log.info("[printRecords]" + key + "==========" + records.get(key));
		}
	}

	public static boolean exsits(String record) {
		return records.values().contains(record);
	}

	public static void put(String key, String value) {
		holder.put(key, value);
	}

	public static String get(String key) {
		return holder.get(key);
	}

}
