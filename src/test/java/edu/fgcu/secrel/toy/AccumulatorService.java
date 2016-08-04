/**
 * This file defines the AccumulatorService class. AccululatorService is a toy
 * implementation of the Service class.
 */
package edu.fgcu.secrel.toy;

import java.io.*;
import java.util.*;

import edu.fgcu.secrel.ReliableSoftwareService;

/**
 * The AccumulatorService class is a Service that calculates statistics on a
 * number series passed through its input stream. The caller can specify either
 * in the argument vector or as the named parameter "type" the type of numbers
 * that will be sent. The type can be one of "Integer", "Long", "Float" or
 * "Double". If both the argument map and vector are non-empty and contain valid
 * values, the named parameter takes precedence. If no valid parameter exists,
 * the type defaults to "Integer". After each number is received, the current
 * statistics will be sent through the output stream.
 *
 * @author lngibson
 *
 */
public class AccumulatorService extends ReliableSoftwareService {

	@SuppressWarnings("javadoc")
	private Number max(String type, Number a, Number b) {
		if (type.equalsIgnoreCase("Integer"))
			return b.intValue() > a.intValue() ? b : a;
		if (type.equalsIgnoreCase("Long"))
			return b.longValue() > a.longValue() ? b : a;
		if (type.equalsIgnoreCase("Float"))
			return b.floatValue() > a.floatValue() ? b : a;
		if (type.equalsIgnoreCase("Double"))
			return b.doubleValue() > a.doubleValue() ? b : a;
		return null;
	}

	@SuppressWarnings("javadoc")
	private Number mea(String type, Number sum, int num) {
		if (type.equalsIgnoreCase("Integer"))
			return sum.floatValue() / num;
		if (type.equalsIgnoreCase("Long"))
			return sum.doubleValue() / num;
		if (type.equalsIgnoreCase("Float"))
			return sum.floatValue() / num;
		if (type.equalsIgnoreCase("Double"))
			return sum.doubleValue() / num;
		return null;
	}

	@SuppressWarnings("javadoc")
	private Number med(String type, Number a, Number b) {
		if (type.equalsIgnoreCase("Integer"))
			return b.intValue() + a.intValue() / 2;
		if (type.equalsIgnoreCase("Long"))
			return b.longValue() + a.longValue() / 2;
		if (type.equalsIgnoreCase("Float"))
			return b.floatValue() + a.floatValue() / 2;
		if (type.equalsIgnoreCase("Double"))
			return b.doubleValue() + a.doubleValue() / 2;
		return null;
	}

	@SuppressWarnings("javadoc")
	private Number min(String type, Number a, Number b) {
		if (type.equalsIgnoreCase("Integer"))
			return b.intValue() < a.intValue() ? b : a;
		if (type.equalsIgnoreCase("Long"))
			return b.longValue() < a.longValue() ? b : a;
		if (type.equalsIgnoreCase("Float"))
			return b.floatValue() < a.floatValue() ? b : a;
		if (type.equalsIgnoreCase("Double"))
			return b.doubleValue() < a.doubleValue() ? b : a;
		return null;
	}
	
	@SuppressWarnings("javadoc")
	private Number sum(String type, Number a, Number b) {
		if (type.equalsIgnoreCase("Integer"))
			return b.intValue() + a.intValue();
		if (type.equalsIgnoreCase("Long"))
			return b.longValue() + a.longValue();
		if (type.equalsIgnoreCase("Float"))
			return b.floatValue() + a.floatValue();
		if (type.equalsIgnoreCase("Double"))
			return b.doubleValue() + a.doubleValue();
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see edu.fgcu.secrel.Service#invokeServiceInner(edu.fgcu.secrel.Service.
	 * Handle, java.lang.String[], java.util.Map)
	 */
	@Override
	protected void invokeServiceInner(Handle handle, String[] argv, Map<String, String> argm) {
		String type = null;
		if (!argm.isEmpty() && argm.containsKey("type")) {
			String mv = argm.get("type");
			if (mv.equalsIgnoreCase("Integer"))
				type = "Integer";
			else if (mv.equalsIgnoreCase("Long"))
				type = "Long";
			else if (mv.equalsIgnoreCase("Float"))
				type = "Float";
			else if (mv.equalsIgnoreCase("Double"))
				type = "Double";
		}
		if (type == null)
			for (int i = 0; i < argv.length && type == null; i++) {
				String mv = argv[i];
				if (mv.equalsIgnoreCase("Integer"))
					type = "Integer";
				else if (mv.equalsIgnoreCase("Long"))
					type = "Long";
				else if (mv.equalsIgnoreCase("Float"))
					type = "Float";
				else if (mv.equalsIgnoreCase("Double"))
					type = "Double";
			}
		if (type == null)
			type = "Integer";
		List<Number> sequence = new LinkedList<>();
		Number minimum = null, maximum = null, median, sum = null, mean
		// , mode
		// , variance
		;
		DataInputStream din = new DataInputStream(inputStream(handle));
		PrintWriter pwout = new PrintWriter(outputStream(handle));
		while (true) {
			String msg = null;
			while (msg == null)
				try {
					msg = din.readUTF();
				}
				catch (IOException e) {
					// do nothing
				}
			if (msg.equalsIgnoreCase("exit"))
				break;
			Number cur = null;
			if (type.equalsIgnoreCase("Integer"))
				try {
					cur = Integer.parseInt(msg);
				}
				catch (NumberFormatException e) {
					// do nothing
				}
			else if (type.equalsIgnoreCase("Long"))
				try {
					cur = Long.parseLong(msg);
				}
				catch (NumberFormatException e) {
					// do nothing
				}
			else if (type.equalsIgnoreCase("Float"))
				try {
					cur = Float.parseFloat(msg);
				}
				catch (NumberFormatException e) {
					// do nothing
				}
			else if (type.equalsIgnoreCase("Double"))
				try {
					cur = Double.parseDouble(msg);
				}
				catch (NumberFormatException e) {
					// do nothing
				}
			if (cur == null)
				continue;
			if (sequence.isEmpty())
				minimum = maximum = sum = cur;
			else {
				minimum = min(type, minimum, cur);
				maximum = max(type, maximum, cur);
				sum = sum(type, sum, cur);
			}
			median = med(type, minimum, maximum);
			mean = mea(type, sum, sequence.size());
			pwout.printf("Statistics:\n\tsize: %d\n\tminimum: %s\n\tmaximum: %s\n\tmedian: %s\n\tmean: %s",
			        sequence.size(), minimum.toString(), maximum.toString(), median.toString(), mean.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.fgcu.secrel.Service#getName()
	 */
	@Override
	public String getName() {
		return "edu.fgcu.secrel.toy.Accumulator";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.fgcu.secrel.Service#processRequest()
	 */
	@Override
	public void processRequest() {
		// not implemented
	}

}