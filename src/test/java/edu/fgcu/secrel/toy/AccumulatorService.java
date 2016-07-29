/**
 * TODO
 */
package edu.fgcu.secrel.toy;

import java.io.InputStream;

import edu.fgcu.secrel.*;

/**
 * TODO
 *
 * @author lngibson
 *         
 */
public class AccumulatorService extends ReliableSoftwareService {

	/**
	 * TODO
	 *
	 * @see edu.fgcu.secrel.Service#invokeServiceInner(edu.fgcu.secrel.Service.Handle,
	 *      java.io.InputStream)
	 */
	@Override
	protected void invokeServiceInner(Handle handle, InputStream stream) {
		// TODO Auto-generated method stub

	}

	/**
	 * TODO
	 *
	 * @see edu.fgcu.secrel.Service#getName()
	 */
	@Override
	public String getName() {
		return "edu.fgcu.secrel.toy.Accumulator";
	}

	/**
	 * TODO
	 *
	 * @see edu.fgcu.secrel.Service#processRequest(edu.fgcu.secrel.User)
	 */
	@Override
	public void processRequest(User user) {
		// TODO Auto-generated method stub

	}

}
