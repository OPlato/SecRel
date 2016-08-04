/**
 * <p>
 * This file defines the Services class. The Services class contains methods to
 * manipulate the data in the SecRelSystem concerning role-service
 * authorization.
 * </p>
 */
// Note: authorizeRole(accessType)
package edu.fgcu.secrel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains methods for (un)authorizing roles to invoke services in
 * the ScRelSystem and testing role-service authorization.
 *
 * @author lngibson
 *
 */
public class Authorizations {

	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @param accessType I do not know the purpose of this parameter
	 */
	public static void authorizeRole(Integer roleId, Integer serviceId, int accessType) {
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		// check if a serviceId is null
		if (serviceId == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		// check if service exists
		if (!SecRelSystem.serviceIds.containsKey(serviceId))
			throw new IllegalArgumentException("Service with that id does not exist.");
		// create Right instance
		Right right = new Right(roleId, serviceId, accessType);
		// construct mapping rows
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		// check if rows exist
		if (SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        || SecRelSystem.serviceBackwardMap.containsKey(backwardRow))
			throw new IllegalStateException(
			        "Role is already authorized to invoke that Service: " + forwardRow + ", " + backwardRow);
		// add rows to system
		SecRelSystem.serviceForwardMap.put(forwardRow, right);
		SecRelSystem.serviceBackwardMap.put(backwardRow, right);
	}

	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param role the Role instance
	 * @param service the Service instance
	 * @param accessType I do not know the purpose of this parameter
	 */
	public static void authorizeRole(Role role, Service service, int accessType) {
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(role.getId()))
			throw new IllegalArgumentException("Role does not exist.");
		// check if a service is null
		if (service == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		if (!SecRelSystem.serviceIds.containsKey(service.getId()))
			throw new IllegalArgumentException("Service does not exist.");
		Integer roleId = role.getId();
		Integer serviceId = service.getId();
		// create Right instance
		Right right = new Right(roleId, serviceId, accessType);
		// construct mapping rows
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		// check if rows exist
		if (SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        || SecRelSystem.serviceBackwardMap.containsKey(backwardRow))
			throw new IllegalStateException(
			        "Role is already authorized to invoke that Service: " + forwardRow + ", " + backwardRow);
		// add rows to system
		SecRelSystem.serviceForwardMap.put(forwardRow, right);
		SecRelSystem.serviceBackwardMap.put(backwardRow, right);
	}

	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param roleName the name of the role
	 * @param serviceName the name of the service
	 * @param accessType I do not know the purpose of this parameterI do not
	 *            know the purpose of this parameter
	 */
	public static void authorizeRole(String roleName, String serviceName, int accessType) {
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role with that name does not exist.");
		// check if a serviceName is null
		if (serviceName == null)
		    // throw exception
		    throw new NullPointerException("Service name cannot be null.");
		if (!SecRelSystem.serviceNames.containsKey(serviceName))
			throw new IllegalArgumentException("Service with that name does not exist.");
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		Integer serviceId = SecRelSystem.serviceNames.get(serviceName);
		// create Right instance
		Right right = new Right(roleId, serviceId, accessType);
		// construct mapping rows
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		// check if rows exist
		if (SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        || SecRelSystem.serviceBackwardMap.containsKey(backwardRow))
			throw new IllegalStateException(
			        "Role is already authorized to invoke that Service: " + forwardRow + ", " + backwardRow);
		// add rows to system
		SecRelSystem.serviceForwardMap.put(forwardRow, right);
		SecRelSystem.serviceBackwardMap.put(backwardRow, right);
	}

	/**
	 * Returns the roles authorized to invoke the service with the specified ID.
	 *
	 * @param serviceId the id of the service
	 * @return the roles authorized to invoke the service
	 */
	public static NavigableSet<Role> getAuthorizedRoles(Integer serviceId) {
		// retrieve role ids
		Integer[] ids = SecRelSystem.getAuthorizedIds(serviceId);
		// convert to list
		List<Integer> roles = Arrays.asList(ids);
		// stream into Role set
		return roles.stream().map(id -> new Role(id)).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the services the role with the specified ID is authorized to
	 * invoke.
	 *
	 * @param roleId the id of the role
	 * @return the services the role is authorized to invoke
	 */
	public static NavigableSet<Service> getAuthorizedServices(Integer roleId) {
		// retrieve service ids
		Integer[] ids = SecRelSystem.getServiceIds(roleId);
		// convert to list
		List<Integer> services = Arrays.asList(ids);
		// stream into Service set
		return services.stream().map(id -> SecRelSystem.serviceIds.get(id))
		        .collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the rights of the role with the specified ID.
	 *
	 * @param roleId the id of the role
	 * @return the rights associated with this role
	 */
	public static Set<Right> getRoleRights(Integer roleId) {
		// retrieves the service ids
		Integer[] ids = SecRelSystem.getServiceIds(roleId);
		// convert to list
		List<Integer> services = Arrays.asList(ids);
		// stream into Right set
		return services.stream()
		        .map(id -> SecRelSystem.serviceForwardMap.get(SecRelSystem.serviceForwardRow(roleId, id)))
		        .collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * Returns the rights of the service with the specified ID.
	 *
	 * @param serviceId the id of the service
	 * @return the rights associated with this service
	 */
	public static Set<Right> getServiceRights(Integer serviceId) {
		// retrieve the role ids
		Integer[] ids = SecRelSystem.getAuthorizedIds(serviceId);
		// convert to list
		List<Integer> roles = Arrays.asList(ids);
		// stream into Right set
		return roles.stream()
		        .map(id -> SecRelSystem.serviceForwardMap.get(SecRelSystem.serviceForwardRow(id, serviceId)))
		        .collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * Returns whether the specified role is authorized to invoke the specified
	 * service.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @return true if the role is authorized to invoke the service
	 */
	public static boolean isAuthorizedFor(Integer roleId, Integer serviceId) {
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		// check if a serviceId is null
		if (serviceId == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		// check if service exists
		if (!SecRelSystem.serviceIds.containsKey(serviceId))
			throw new IllegalArgumentException("Service with that id does not exist.");
		// construct mapping rows
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		// check existence of mapping rows
		return SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        && SecRelSystem.serviceBackwardMap.containsKey(backwardRow);
	}

	/**
	 * Returns whether the specified role is authorized to invoke the specified
	 * service.
	 *
	 * @param role the role instance
	 * @param service the service
	 * @return true if the role is authorized to invoke the service
	 */
	public static boolean isAuthorizedFor(Role role, Service service) {
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(role.getId()))
			throw new IllegalArgumentException("Role does not exist.");
		// check if a service is null
		if (service == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		// check if service exists
		if (!SecRelSystem.serviceIds.containsKey(service.getId()))
			throw new IllegalArgumentException("Service does not exist.");
		// construct mapping rows
		Integer roleId = role.getId();
		Integer serviceId = service.getId();
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		// check existence of mapping rows
		return SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        && SecRelSystem.serviceBackwardMap.containsKey(backwardRow);
	}

	/**
	 * Returns whether the specified role is authorized to invoke the specified
	 * service.
	 *
	 * @param roleName the name of the role
	 * @param serviceName the name of the service
	 * @return true if the role is authorized to invoke the service
	 */
	public static boolean isAuthorizedFor(String roleName, String serviceName) {
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role with that name does not exist.");
		// check if a serviceName is null
		if (serviceName == null)
		    // throw exception
		    throw new NullPointerException("Service name cannot be null.");
		// check if service is registered
		if (!SecRelSystem.serviceNames.containsKey(serviceName))
			throw new IllegalArgumentException("Service with that name does not exist.");
		// construct mapping rows
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		Integer serviceId = SecRelSystem.serviceNames.get(serviceName);
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		// check existence of mapping rows
		return SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        && SecRelSystem.serviceBackwardMap.containsKey(backwardRow);
	}

	/**
	 * Prevents instantiation of the Authorizations class.
	 */
	private Authorizations() {
	}
	
}