/**
 * <p>
 * This file defines the Services class. The Services class contains methods to
 * manipulate the data in the SecRelSystem concerning services.
 * </p>
 */
package edu.fgcu.secrel;

/**
 * This class contains methods for registering, removing, and querying
 * information about services in the ScRelSystem.
 *
 * @author lngibson
 *
 */
public class Services {
	
	/**
	 * Finds the service with the specified service ID if one exists.
	 *
	 * @param serviceId the id of the service
	 * @return the corresponding Service
	 */
	public static Service findService(Integer serviceId) {
		if (SecRelSystem.serviceIds.containsKey(serviceId))
			return SecRelSystem.serviceIds.get(serviceId);
		throw new IllegalArgumentException("User with that id does not exist");
	}
	
	/**
	 * Finds the service with the specified service name if one exists.
	 *
	 * @param serviceName the name of the service
	 * @return the corresponding Service
	 */
	public static Service findService(String serviceName) {
		if (SecRelSystem.serviceNames.containsKey(serviceName))
			return SecRelSystem.serviceIds.get(SecRelSystem.serviceNames.get(serviceName));
		throw new IllegalArgumentException("Service with that name does not exist");
	}
	
	/**
	 * Returns whether the system has a service with the specified id.
	 *
	 * @param serviceId the id of the service
	 * @return true if there is such a service
	 */
	public static boolean hasService(Integer serviceId) {
		return SecRelSystem.serviceIds.containsKey(serviceId);
	}
	
	/**
	 * Returns whether the service has been registered with the system.
	 *
	 * @param service the service
	 * @return true if the service has been registered
	 */
	public static boolean hasService(Service service) {
		return SecRelSystem.serviceIds.containsValue(service);
	}
	
	/**
	 * Returns whether the system has a service with the specified name.
	 *
	 * @param serviceName the name of the service
	 * @return true if there is such a service
	 */
	public static boolean hasService(String serviceName) {
		return SecRelSystem.serviceNames.containsKey(serviceName);
	}
	
	/**
	 * Registers the Service with the system.
	 *
	 * @param service the Service to be registered
	 */
	public static void registerService(Service service) {
		// check if the service is null
		if (service == null)
		    // throw exception
		    throw new NullPointerException("Service cannot be null.");
		// check if service is already registered
		if (SecRelSystem.serviceIds.containsValue(service))
		    // exit method
		    throw new IllegalArgumentException(
		            String.format("A Service with the name \"%s\" already exists. Service(%d,\"%s\")",
		                    service.getName(), SecRelSystem.roleNames.get(service.getName()), service.getName()));
		// check if a service with that name already exists
		if (SecRelSystem.serviceNames.containsKey(service.getName()))
		    // throw exception
		    throw new IllegalArgumentException(
		            String.format("A Service with the name \"%s\" already exists.", service.getName()));
		// compute new serviceId
		Integer id = SecRelSystem.serviceIds.isEmpty() ? 0 : SecRelSystem.serviceIds.lastKey() + 1;
		try {
			// attempt to set id
			service.setId(id);
		}
		catch (IllegalStateException e) {
			// id is already set retrieve id
			id = service.getId();
			// check if id is already in use
			if (SecRelSystem.serviceIds.containsKey(id))
			    // throw exception
			    throw new IllegalStateException("Service id is already in use.");
		}
		// register service
		SecRelSystem.serviceIds.put(id, service);
		SecRelSystem.serviceNames.put(service.getName(), id);
	}
	
	/**
	 * Removes the specified service from the system.
	 *
	 * @param serviceId the id of the service
	 */
	public static void removeService(Integer serviceId) {
		// check if a serviceId is null
		if (serviceId == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		// check if service exists
		if (!SecRelSystem.serviceIds.containsKey(serviceId))
			throw new IllegalArgumentException("Service does not exist.");
		// clear service roles
		// SecRelSystem.clearServiceRoles(serviceId); // not implemented
		Service service = SecRelSystem.serviceIds.get(serviceId);
		// remove service from system
		SecRelSystem.serviceNames.remove(service.getName());
		SecRelSystem.serviceIds.remove(serviceId);
	}
	
	/**
	 * Removes the specified service from the system.
	 *
	 * @param service the service instance
	 */
	public static void removeService(Service service) {
		// check if a service is null
		if (service == null)
		    // throw exception
		    throw new NullPointerException("Service cannot be null.");
		removeService(service.getId());
		// check if service is registered
		if (!SecRelSystem.serviceNames.containsValue(service))
			throw new IllegalArgumentException("Service is not registered.");
		// clear service roles
		// SecRelSystem.clearServiceRoles(serviceId); // not implemented
		// remove service from system
		SecRelSystem.serviceIds.remove(service.getId());
		SecRelSystem.serviceNames.remove(service.getName());
	}
	
	/**
	 * Removes the specified service from the system.
	 *
	 * @param serviceName the name of the service
	 */
	public static void removeService(String serviceName) {
		// check if a serviceName is null
		if (serviceName == null)
		    // throw exception
		    throw new NullPointerException("Service name cannot be null.");
		// check if service exists
		if (!SecRelSystem.serviceNames.containsKey(serviceName))
			throw new IllegalArgumentException("Service does not exist.");
		// clear service roles
		// SecRelSystem.clearServiceRoles(serviceId); // not implemented
		Service service = SecRelSystem.serviceIds.get(SecRelSystem.serviceNames.get(serviceName));
		// remove service from system
		SecRelSystem.serviceIds.remove(service.getId());
		SecRelSystem.serviceNames.remove(serviceName);
	}
	
	/**
	 * Prevents instantiation of the Services class.
	 */
	private Services() {
	}

}