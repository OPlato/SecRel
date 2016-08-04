/**
 * This file defines the Service type and its inner classes, ReferenceMonitor
 * and Handle.
 */
package edu.fgcu.secrel;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * <p>
 * This class represents a reliable service to be protected by the SecRel's
 * role-based security system.
 * </p>
 *
 * @author lngibson
 *
 */
// Note: processRequest, serviceLevel
public abstract class Service implements Entity, Comparable<Service> {

	/**
	 * The Handle class is a link between callers of a Service and its executing
	 * thread. When a Service is executed, a unique Handle instance is passed to
	 * the caller that can be used to query the state of the execution, receive
	 * messages from the executing thread and wait for its completion.
	 *
	 * @author lngibson
	 *
	 */
	public class Handle {

		/**
		 * Signifies that the executing thread has not yet started. Service
		 * implementations must not set the state to this value as this is
		 * handled automatically.
		 */
		public static final int IDLE = 0;

		/**
		 * Signifies that the executing thread is preparing to start. Service
		 * implementations need not use this state and may start in the RUNNING
		 * state.
		 */
		public static final int STARTING = 1;

		/**
		 * Signifies that the executing thread is running its main section.
		 */
		public static final int RUNNING = 2;

		/**
		 * Signifies that the executing thread is blocked or waiting. Service
		 * implementations should set the state to WAITING before entering a
		 * synchronized block or explicitly waiting.
		 */
		public static final int WAITING = 3;

		/**
		 * Signifies that the executing thread is ready to send results. Service
		 * implementations that return results must set the state to FINALIZING
		 * so that the caller will know to read the results.
		 */
		public static final int FINALIZING = 4;

		/**
		 * Signifies that the executing thread has completed and that any
		 * results are now available. Service implementations must not set the
		 * state to this value as this is handled automatically.
		 */
		public static final int COMPLETED = 5;

		/**
		 * The state of the executing thread.
		 */
		private int state;

		/**
		 * The executing thread.
		 */
		private Thread thread;

		/**
		 * The stream to receive messages from the caller.
		 */
		private InputStream iStream;

		/**
		 * The stream to send messages to the caller.
		 */
		private OutputStream oStream;

		/**
		 * The results data.
		 */
		private InputStream resultStream;

		/**
		 * The size in bytes of the results data.
		 */
		private int resultSize;

		/**
		 * Returns the input stream for use by the executing thread.
		 *
		 * @return the input stream
		 */
		protected InputStream inputStream() {
			return iStream;
		}

		/**
		 * Returns the output stream for use by the executing thread.
		 *
		 * @return the output stream
		 */
		protected OutputStream outputStream() {
			return oStream;
		}

		/**
		 * Sets the result of the Service and the size in bytes of that result.
		 * If the size cannot be predetermined, a -1 must be passed for size. In
		 * this case, all result fetching methods other than streamResult will
		 * fail.
		 *
		 * @param result the result of the Service
		 * @param size the size of the result in bytes or -1 if it cannot be
		 *            determined
		 */
		protected void result(InputStream result, int size) {
			resultStream = result;
			resultSize = size;
		}

		/**
		 * Alerts the owner of this Handle of the state of the Service.
		 *
		 * @param newState the state of the Service
		 */
		protected void state(int newState) {
			synchronized (this) {
				if (newState < 0 || newState > Handle.COMPLETED)
					throw new IllegalArgumentException("Unknown state");
				state = newState;
				notifyAll();
				;
			}
		}

		/**
		 * The result of the Service's operation as a byte if applicable. If
		 * there is no result or if the result has a size greater than one byte,
		 * an IllegalStateChangeException will be thrown. If this method is
		 * called before the Service has ended, an IllegalStateException will be
		 * thrown. To wait for the Service to finish, use join().
		 *
		 * @return the result as a byte
		 * @throws IOException if reads from the result stream fail
		 */
		public int byteResult() throws IOException {
			Byte out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else if (resultSize == 0 || resultStream == null)
					message = "Service provided no results";
				else if (resultSize != 1)
					message = "Service results are larger than one byte";
				else {
					out = (byte) resultStream.read();
					resultStream.close();
				}
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}

		/**
		 * The result of the Service's operation as a byte array. If there is no
		 * result, an IllegalStateChangeException will be thrown. If this method
		 * is called before the Service has ended, an IllegalStateException will
		 * be thrown. To wait for the Service to finish, use join().
		 *
		 * @return the result as a byte
		 * @throws IOException if reads from the result stream fail
		 */
		public byte[] bytesResult() throws IOException {
			byte[] out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else if (resultSize == 0 || resultStream == null)
					message = "Service provided no results";
				else {
					ByteArrayOutputStream baout = new ByteArrayOutputStream();
					BufferedOutputStream bout = new BufferedOutputStream(baout);
					byte[] buffer = new byte[1024];
					int n = 0;
					while ((n = resultStream.read(buffer)) > 0)
						bout.write(buffer, 0, n);
					out = baout.toByteArray();
				}
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}

		/**
		 * Initializes input and output stream using supplied streams. This is
		 * meant to be used not by the caller but by an middle man. For example,
		 * if user invoke service over a tcp connection, as the SecRel system
		 * does not provide this mechanism, another application would relay the
		 * request to the system. Then, that application would call this method
		 * to connect the input and output stream from the caller to the
		 * executing thread using this method.
		 *
		 * @param in the input stream to connect to
		 * @param out the output stream to connect to
		 */
		public void connect(InputStream in, OutputStream out) {
			iStream = new BufferedInputStream(in);
			oStream = new BufferedOutputStream(out);
		}

		/**
		 * The result of the Service's operation as a double if applicable. If
		 * there is no result or if the result has a size not equal to 8 bytes,
		 * an IllegalStateChangeException will be thrown. If this method is
		 * called before the Service has ended, an IllegalStateException will be
		 * thrown. To wait for the Service to finish, use join().
		 *
		 * @return the result as a double
		 * @throws IOException if reads from the result stream fail
		 */
		public double doubleResult() throws IOException {
			Double out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else if (resultSize == 0 || resultStream == null)
					message = "Service provided no results";
				else if (resultSize != 8)
					message = "Service results are not 8 bytes";
				else {
					byte[] bytes = new byte[8];
					resultStream.read(bytes);
					out = ByteBuffer.wrap(bytes).getDouble();
					resultStream.close();
				}
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}

		/**
		 * The result of the Service's operation as a float if applicable. If
		 * there is no result or if the result has a size not equal to 4 bytes,
		 * an IllegalStateChangeException will be thrown. If this method is
		 * called before the Service has ended, an IllegalStateException will be
		 * thrown. To wait for the Service to finish, use join().
		 *
		 * @return the result as a float
		 * @throws IOException if reads from the result stream fail
		 */
		public float floatResult() throws IOException {
			Float out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else if (resultSize == 0 || resultStream == null)
					message = "Service provided no results";
				else if (resultSize != 4)
					message = "Service results are not 4 bytes";
				else {
					byte[] bytes = new byte[4];
					resultStream.read(bytes);
					out = ByteBuffer.wrap(bytes).getFloat();
					resultStream.close();
				}
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}

		/**
		 * The result of the Service's operation as an int if applicable. If
		 * there is no result or if the result has a size not equal to 4 bytes,
		 * an IllegalStateChangeException will be thrown. If this method is
		 * called before the Service has ended, an IllegalStateException will be
		 * thrown. To wait for the Service to finish, use join().
		 *
		 * @return the result as an int
		 * @throws IOException if reads from the result stream fail
		 */
		public int intResult() throws IOException {
			Integer out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else if (resultSize == 0 || resultStream == null)
					message = "Service provided no results";
				else if (resultSize != 4)
					message = "Service results are not 4 bytes";
				else {
					byte[] bytes = new byte[4];
					resultStream.read(bytes);
					out = ByteBuffer.wrap(bytes).getInt();
					resultStream.close();
				}
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}

		/**
		 * Waits for the Service to complete or timeout milliseconds.
		 *
		 * @param timeout the number of milliseconds to wait or -1 to wait
		 *            indefinitely
		 */
		public void join(long timeout) {
			synchronized (this) {
				if (state != Handle.COMPLETED)
					try {
						thread.join(timeout);
					}
				catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		}

		/**
		 * The result of the Service's operation as a long if applicable. If
		 * there is no result or if the result has a size not equal to 8 bytes,
		 * an IllegalStateChangeException will be thrown. If this method is
		 * called before the Service has ended, an IllegalStateException will be
		 * thrown. To wait for the Service to finish, use join().
		 *
		 * @return the result as a long
		 * @throws IOException if reads from the result stream fail
		 */
		public long longResult() throws IOException {
			Long out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else if (resultSize == 0 || resultStream == null)
					message = "Service provided no results";
				else if (resultSize != 8)
					message = "Service results are not 8 bytes";
				else {
					byte[] bytes = new byte[8];
					resultStream.read(bytes);
					out = ByteBuffer.wrap(bytes).getLong();
					resultStream.close();
				}
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}

		/**
		 * The result of the Service's operation as a short if applicable. If
		 * there is no result or if the result has a size not equal to 2 bytes,
		 * an IllegalStateChangeException will be thrown. If this method is
		 * called before the Service has ended, an IllegalStateException will be
		 * thrown. To wait for the Service to finish, use join().
		 *
		 * @return the result as a short
		 * @throws IOException if reads from the result stream fail
		 */
		public short shortResult() throws IOException {
			Short out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else if (resultSize == 0 || resultStream == null)
					message = "Service provided no results";
				else if (resultSize != 2)
					message = "Service results are not 2 bytes";
				else {
					byte[] bytes = new byte[2];
					resultStream.read(bytes);
					out = ByteBuffer.wrap(bytes).getShort();
					resultStream.close();
				}
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}

		/**
		 * Queries the state of the Service.
		 *
		 * @return the state of the Service
		 */
		public int state() {
			return state;
		}

		/**
		 * Waits for the Service state to change or timeout milliseconds.
		 *
		 * @param targetState the state to wait for or -1 to wait for any new
		 *            state
		 * @param timeout the number of milliseconds to wait or -1 to wait
		 *            indefinitely
		 * @return the new state of the Service if changed
		 */
		public int stateChange(int targetState, long timeout) {
			synchronized (this) {
				int prev = state;
				while (targetState == -1 ? state == prev : state != targetState)
					try {
						if (timeout != -1)
							wait(timeout);
						else
							wait();
					}
				catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
			return state;
		}

		/**
		 * The result of the Service's operation as an InputStream. If this
		 * method is called before the Service has ended, an
		 * IllegalStateException will be thrown. To wait for the Service to
		 * finish, use join().
		 *
		 * @return the result as an InputStream
		 */
		public InputStream streamResult() {
			InputStream out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else
					out = resultStream;
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}

		/**
		 * The result of the Service's operation as a String. If this method is
		 * called before the Service has ended, an IllegalStateException will be
		 * thrown. To wait for the Service to finish, use join().
		 *
		 * @return the result as a String
		 * @throws IOException if reads from the result stream fail
		 */
		public String stringResult() throws IOException {
			String out = null;
			String message = null;
			synchronized (this) {
				if (state != Handle.COMPLETED)
					message = "Service has not yet completed";
				else if (resultStream == null)
					message = "Service provided no results";
				else {
					@SuppressWarnings("resource")
					Scanner s = new Scanner(resultStream).useDelimiter("\\A");
					out = s.hasNext() ? s.next() : "";
					s.close();
					resultStream.close();
				}
			}
			if (out != null)
				return out;
			throw new IllegalStateException(message);
		}
	}

	/**
	 * This class is responsible for granting or denying access to a request to
	 * invoke a Service.
	 *
	 * @author lngibson
	 *
	 */
	public class ReferenceMonitor implements Runnable {

		/**
		 * Signifies that the monitor has not started.
		 */
		public static final int IDLE = 0;

		/**
		 * Signifies that the monitor is deliberating.
		 */
		public static final int PENDING = -1;

		/**
		 * Signifies that the monitor has granted access.
		 */
		public static final int AUTHORIZED = 1;

		/**
		 * Signifies that the monitor has denied access.
		 */
		public static final int UNAUTHORIZED = 2;

		/**
		 * The id of this ReferenceMonitor.
		 */
		private final int monitorId;

		/**
		 * The id of the requesting user.
		 */
		private final int userId;

		/**
		 * The thread executing this monitor.
		 */
		private Thread thread;

		/**
		 * The state of this monitor.
		 */
		private int state = ReferenceMonitor.IDLE;

		/**
		 * Whether the requesting user has been granted access.
		 */
		private boolean isAuthorized = false;

		/**
		 * Constructs a ReferenceMonitor for the specified user.
		 *
		 * @param id The id of this monitor
		 * @param userId the id of the requesting user
		 */
		private ReferenceMonitor(int id, int userId) {
			super();
			monitorId = id;
			this.userId = userId;
		}

		/**
		 * Grants access to the user.
		 */
		public void authorizeUser() {
			isAuthorized = true;
			state = ReferenceMonitor.AUTHORIZED;
			synchronized (this) {
				notify();
			}
		}

		/**
		 * Determines if the user can invoke the Service. Deliberation is done
		 * in a separate thread.
		 *
		 * @return whether the user has been granted access
		 */
		public boolean checkRights() {
			/**
			 * // Using another thread and then waiting for it to finish seems
			 * to me now to be pointless state = ReferenceMonitor.PENDING;
			 *
			 * SecRelSystem.monitorThreadPool.execute(this); while (state ==
			 * ReferenceMonitor.PENDING) try { synchronized (this) { wait(); } }
			 * catch (InterruptedException e) { // e.printStackTrace(); }
			 */
			run();
			return isAuthorized;
		}

		/**
		 * Returns the id of this monitor.
		 *
		 * @return the id
		 */
		public int getId() {
			return monitorId;
		}

		/**
		 * Returns the state of this monitor.
		 *
		 * @return the state
		 */
		public int getState() {
			return state;
		}

		/**
		 * Returns the thread executing this monitor.
		 *
		 * @return the thread
		 */
		public Thread getThread() {
			return thread;
		}

		/**
		 * Returns the id of the requesting user.
		 *
		 * @return the userId
		 */
		public int getUserId() {
			return userId;
		}

		/**
		 * Returns whether the requesting user has been granted access.
		 *
		 * @return the isAuthorized
		 */
		public boolean isAuthorized() {
			return isAuthorized;
		}

		@Override
		public void run() {
			Integer[] roleIds = SecRelSystem.getRoleIds(userId);
			for (int roleId : roleIds)
				if (Authorizations.isAuthorizedFor(roleId, id))
					authorizeUser();
			if (state == ReferenceMonitor.PENDING) {
				state = ReferenceMonitor.UNAUTHORIZED;
				synchronized (this) {
					notify();
				}
			}
		}

	}

	/**
	 * This class executes the Service in a separate thread.
	 *
	 * @author lngibson
	 *
	 */
	public class ServiceRunner implements Runnable {

		/**
		 * The connection to the caller of the Service.
		 */
		private Handle handle;

		/**
		 * The parameters array.
		 */
		private String[] argumentVector;

		/**
		 * The paramenters map.
		 */
		private Map<String, String> argumentMap;

		/**
		 * Construct a ServiceRunner for the owning Service.
		 *
		 * @param handle the connection to the caller of the Service
		 * @param argv an array of parameters
		 * @param argm an map of parameters
		 */
		public ServiceRunner(Handle handle, String[] argv, Map<String, String> argm) {
			super();
			this.handle = handle;
			argumentVector = argv;
			argumentMap = argm;
		}

		@Override
		public void run() {
			handle.state(Handle.RUNNING);
			invokeServiceInner(handle, argumentVector, argumentMap);
			handle.state(Handle.COMPLETED);
		}
	}

	/**
	 * The collection of monitors currently running.
	 */
	private static final NavigableMap<Integer, ReferenceMonitor> monitors = new TreeMap<>();

	/**
	 * Retrieves stream from <code>Handle.inputStream()</code>.
	 *
	 * @param handle the Handle of this Service
	 * @return the input stream
	 */
	protected static InputStream inputStream(Handle handle) {
		return handle.inputStream();
	}

	/**
	 * Retrieves stream from <code>Handle.outputStream()</code>.
	 *
	 * @param handle the Handle of this Service
	 * @return the output stream
	 */
	protected static OutputStream outputStream(Handle handle) {
		return handle.outputStream();
	}

	/**
	 * Delegates result and size to <code>Handle.result()</code>.
	 *
	 * @param handle the Handle of this Service
	 * @param result the result of the Service
	 * @param size the size of the result in bytes or -1 if it cannot be
	 *            determined
	 */
	protected static void result(Handle handle, InputStream result, int size) {
		handle.result(result, size);
	}

	/**
	 * Delegates state to <code>Handle.state()</code>.
	 *
	 * @param handle the Handle of this Service
	 * @param state the state of the Service
	 */
	protected static void state(Handle handle, int state) {
		handle.state(state);
	}

	/**
	 * Whether id has been set.
	 */
	private boolean idSet = false;

	/**
	 * The id of the Service. The id is set by the SecRelSystem during
	 * registration.
	 */
	private int id;

	/**
	 * Performs the action specified by this Service instance. Any results will
	 * be sent to any listeners registered to the Handle supplied.
	 *
	 * @param handle a Handle instance to communicate with this Service thread
	 * @param argv an array of parameters
	 * @param argm an map of parameters
	 */
	protected abstract void invokeServiceInner(Handle handle, String[] argv, Map<String, String> argm);

	/**
	 * Sets the id of this Service. This method is to be called by the
	 * SecRelSystem during the registration of this Service and should only be
	 * called once. Any additional calls will trigger an IllegalStateException.
	 *
	 * @param id the new id of this Service
	 */
	protected void setId(int id) {
		if (idSet)
			throw new IllegalStateException("Service Id has already been set");
		this.id = id;
	}

	@Override
	public int compareTo(Service s) {
		return id - s.id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Service)
			return ((Service) o).id == id;
		return false;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public abstract String getName();

	/**
	 * Returns the rights associated with this service.
	 *
	 * @return this services rights
	 */
	public Collection<Right> getRights() {
		return Authorizations.getServiceRights(id);
	}

	/**
	 * Performs the action specified by this Service instance. This function is
	 * a wrapper for the abstract method
	 * <code>invokeServiceInner( Handle handle, InputStream stream )</code>.
	 * This function creates a new Handle instance to pass to the
	 * invokeServiceInner and returns the created Handle.
	 *
	 * @param argv an array of parameters
	 * @param argm an map of parameters
	 * @return a Handle instance to communicate with the Service thread
	 */
	public Handle invokeService(String[] argv, Map<String, String> argm) {
		Handle handle = new Handle();
		SecRelSystem.serviceThreadPool.execute(new ServiceRunner(handle, argv, argm));
		handle.state = Handle.IDLE;
		handle.thread.start();
		return handle;
	}

	/**
	 * Creates a new ReferenceMonitor for this Service.
	 *
	 * @param userId the id of the user requesting this Service
	 *
	 * @return the monitor authorizing the user's request
	 */
	public ReferenceMonitor monitor(int userId) {
		int monitorId = Service.monitors.isEmpty() ? 0 : Service.monitors.lastKey() + 1;
		return new ReferenceMonitor(monitorId, userId);
	}

	/**
	 * I do not know the purpose of this method. I had planned to use this to
	 * process parameters to the service invocation // so I made it abstract
	 *
	 */
	public abstract void processRequest();

	/**
	 * I do not know the purpose of this method.
	 *
	 * @return I do not now
	 */
	public int serviceLevel() {
		throw new RuntimeException("not implemented");
	}

}