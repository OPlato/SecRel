/**
 * <p>
 * This file defines the SecRelSystem and Entity types.
 * <p>
 * <p>
 * The SecRelSystem class is a database for user, role and service information.
 * The User and Role classes defer requested actions and queries to the
 * SecRelSystem. The only class that contains any significant logic outside the
 * SecRelSystem class is the Service abstract class.
 * </p>
 * <p>
 * The Entity interface is used as a super type for the User, Role and Service
 * types. The need for this is little but may become useful if further
 * extensions include classes treated similarly to the Service class or if the
 * Service class proves to be too abstract and more granularity is needed.
 * </p>
 */
package edu.fgcu.secrel;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import edu.fgcu.secrel.Service.ReferenceMonitor;

/**
 * Required by verifyCompositeMap for extensibility.
 *
 * @author lngibson
 *
 */
interface Entity {

	/**
	 * Returns the id of this entity.
	 *
	 * @return this entity's id
	 */
	Integer getId();

	/**
	 * Returns the name of this entity.
	 *
	 * @return this entity's name
	 */
	String getName();
}

/**
 * <p>
 * The SecRelSystem class is the principal component of the SecRel pattern. It
 * provides a means of manipulating users, roles and services and there
 * interactions.
 * </p>
 * <p>
 * <!-- TODO: give instructions -->
 * </p>
 *
 * @author lngibson
 *
 */
public class SecRelSystem {

	/**
	 * Maps user IDs to user names.
	 */
	protected static final NavigableMap<Integer, String> userIds = new TreeMap<>();

	/**
	 * Maps role IDs to role names.
	 */
	protected static final NavigableMap<Integer, String> roleIds = new TreeMap<>();

	/**
	 * Maps user names to user IDs.
	 */
	protected static final NavigableMap<String, Integer> userNames = new TreeMap<>();

	/**
	 * Maps role names to role IDs.
	 */
	protected static final NavigableMap<String, Integer> roleNames = new TreeMap<>();

	/**
	 * <p>
	 * Maps users to roles.
	 * </p>
	 * <p>
	 * Each long value is the concatenated integer IDs of a user and a role. The
	 * 32 most significant bytes belong to the user and the 32 least significant
	 * belong to the role. Because the set is sorted, users assigned to multiple
	 * roles will have their mappings long values stored consecutively, allowing
	 * for simple retrieval of all a users roles using
	 * <code>NavigableSet.subSet()</code>.
	 * </p>
	 */
	protected static final NavigableSet<Long> memberForwardMap = new TreeSet<>();

	/**
	 * <p>
	 * Maps roles to users.
	 * </p>
	 * <p>
	 * Each long value is the concatenated integer IDs of a role and a user. The
	 * 32 most significant bytes belong to the role and the 32 least significant
	 * belong to the user. Because the set is sorted, roles assigned to multiple
	 * users will have their mappings long values stored consecutively, allowing
	 * for simple retrieval of all a roles users using
	 * <code>NavigableSet.subSet()</code>.
	 * </p>
	 */
	protected static final NavigableSet<Long> memberBackwardMap = new TreeSet<>();

	/**
	 * Maps service IDs to services.
	 */
	protected static final NavigableMap<Integer, Service> serviceIds = new TreeMap<>();

	/**
	 * Maps service names to service IDs.
	 */
	protected static final NavigableMap<String, Integer> serviceNames = new TreeMap<>();

	/**
	 * <p>
	 * Maps roles to services.
	 * </p>
	 * <p>
	 * Each long value is the concatenated integer IDs of a role and a service.
	 * The 32 most significant bytes belong to the role and the 32 least
	 * significant belong to the service. Because the map is sorted, roles
	 * assigned to multiple services will have their mappings long values stored
	 * consecutively, allowing for simple retrieval of all a roles services
	 * using <code>NavigableSet.subMap()</code>.
	 * </p>
	 */
	protected static final NavigableMap<Long, Right> serviceForwardMap = new TreeMap<>();

	/**
	 * <p>
	 * Maps services to roles.
	 * </p>
	 * <p>
	 * Each long value is the concatenated integer IDs of a service and a role.
	 * The 32 most significant bytes belong to the service and the 32 least
	 * significant belong to the role. Because the map is sorted, services
	 * assigned to multiple roles will have their mappings long values stored
	 * consecutively, allowing for simple retrieval of all a services roles
	 * using <code>NavigableSet.subMap()</code>.
	 * </p>
	 */
	protected static final NavigableMap<Long, Right> serviceBackwardMap = new TreeMap<>();

	/**
	 * The number of threads in the serviceThreadPool.
	 */
	private static final int NUM_SERVICE_THREADS = 5;

	/**
	 * The thread pool used to execute ReferenceMonitor instances.
	 */
	protected static ExecutorService monitorThreadPool = Executors.newCachedThreadPool();

	/**
	 * The thread pool used to execute Services.
	 */
	protected static ExecutorService serviceThreadPool = Executors.newFixedThreadPool(NUM_SERVICE_THREADS);

	/**
	 * Clears all assignments for the specified role.
	 *
	 * @param roleId the id of the role
	 */
	protected static void clearRoleMembers(Integer roleId) {
		for (Integer userId : SecRelSystem.getMemberIds(roleId)) {
			long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
			long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
			SecRelSystem.memberForwardMap.remove(forwardRow);
			SecRelSystem.memberBackwardMap.remove(backwardRow);
		}
	}

	/**
	 * Clears all assignments for the specified user.
	 *
	 * @param userId the id of the user
	 */
	protected static void clearUserRoles(Integer userId) {
		for (Integer roleId : SecRelSystem.getRoleIds(userId)) {
			long forwardRow = userId.longValue() << 32 | roleId;
			long backwardRow = roleId.longValue() << 32 | userId;
			SecRelSystem.memberForwardMap.remove(forwardRow);
			SecRelSystem.memberBackwardMap.remove(backwardRow);
		}
	}

	/**
	 * Returns the roles authorized to invoke the service with the specified ID.
	 *
	 * @param serviceId the id of the service
	 * @return the ids of the roles
	 */
	protected static Integer[] getAuthorizedIds(Integer serviceId) {
		Map<Long, Right> entries = SecRelSystem.serviceBackwardMap.subMap(serviceId.longValue() << 32,
		        serviceId.longValue() + 1 << 32);
		Integer[] ids = new Integer[entries.size()];
		int i = 0;
		for (Iterator<Long> it = entries.keySet().iterator(); it.hasNext(); i++)
			ids[i] = (int) (it.next() & 0xffffffffl);
		return ids;
	}

	/**
	 * Returns the users assigned to the role with the specified ID.
	 *
	 * @param roleId the id of the role
	 * @return the ids of the users
	 */
	protected static Integer[] getMemberIds(Integer roleId) {
		Set<Long> entries = SecRelSystem.memberBackwardMap.subSet(roleId.longValue() << 32,
		        roleId.longValue() + 1 << 32);
		Integer[] ids = new Integer[entries.size()];
		int i = 0;
		for (Iterator<Long> it = entries.iterator(); it.hasNext(); i++)
			ids[i] = (int) (it.next() & 0xffffffffl);
		return ids;
	}

	/**
	 * Returns the roles to which the user with the specified ID is assigned.
	 *
	 * @param userId the id of the user
	 * @return the ids of the roles
	 */
	protected static Integer[] getRoleIds(Integer userId) {
		Set<Long> entries = SecRelSystem.memberForwardMap.subSet(userId.longValue() << 32,
		        userId.longValue() + 1 << 32);
		Integer[] ids = new Integer[entries.size()];
		int i = 0;
		for (Iterator<Long> it = entries.iterator(); it.hasNext(); i++)
			ids[i] = (int) (it.next() & 0xffffffffl);
		return ids;
	}

	/**
	 * Returns the name of the role with the specified ID.
	 *
	 * @param id the id of the role
	 * @return the name of the role
	 */
	protected static String getRoleName(Integer id) {
		return SecRelSystem.roleIds.get(id);
	}

	/**
	 * Returns the services the role with the specified ID is authorized to
	 * invoke.
	 *
	 * @param roleId the id of the role
	 * @return the ids of the services
	 */
	protected static Integer[] getServiceIds(Integer roleId) {
		Map<Long, Right> entries = SecRelSystem.serviceForwardMap.subMap(roleId.longValue() << 32,
		        roleId.longValue() + 1 << 32);
		Integer[] ids = new Integer[entries.size()];
		int i = 0;
		for (Iterator<Long> it = entries.keySet().iterator(); it.hasNext(); i++)
			ids[i] = (int) (it.next() & 0xffffffffl);
		return ids;
	}

	/**
	 * Returns the name of the user with the specified ID.
	 *
	 * @param id the id of the user
	 * @return the name of the user
	 */
	protected static String getUserName(Integer id) {
		return SecRelSystem.userIds.get(id);
	}

	/**
	 * Returns the mapping row for use in memberBackwardRow. It consists of a
	 * role id and a user id stored in a long in that order.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @return the mapping row
	 */
	protected static Long memberBackwardRow(Integer userId, Integer roleId) {
		return roleId.longValue() << 32 | userId;
	}

	/**
	 * Returns the mapping row for use in memberForwardRow. It consists of a
	 * user id and a role id stored in a long in that order.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @return the mapping row
	 */
	protected static Long memberForwardRow(Integer userId, Integer roleId) {
		return userId.longValue() << 32 | roleId;
	}

	/**
	 * Returns the mapping row for use in serviceBackwardRow. It consists of a
	 * service id and a role id stored in a long in that order.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @return the mapping row
	 */
	protected static Long serviceBackwardRow(Integer roleId, Integer serviceId) {
		return serviceId.longValue() << 32 | roleId;
	}

	/**
	 * Returns the mapping row for use in serviceForwardRow. It consists of a
	 * role id and a service id stored in a long in that order.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @return the mapping row
	 */
	protected static Long serviceForwardRow(Integer roleId, Integer serviceId) {
		return roleId.longValue() << 32 | serviceId;
	}

	/**
	 * Assigns the specified user to the specified role.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @return true if successful, false otherwise
	 */
	public static boolean assignRole(Integer userId, Integer roleId) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User with that id does not exist.");
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		if (SecRelSystem.memberForwardMap.contains(forwardRow) || SecRelSystem.memberBackwardMap.contains(backwardRow))
			throw new IllegalStateException("User is already assigned to that Role: " + forwardRow + "( " + userId
			        + ", " + roleId + " ), " + backwardRow + "( " + roleId + ", " + userId + " )");
		SecRelSystem.memberForwardMap.add(forwardRow);
		SecRelSystem.memberBackwardMap.add(backwardRow);
		return true;
	}

	/**
	 * Assigns the specified user to the specified role.
	 *
	 * @param userName the name of the user
	 * @param roleName the name of the role
	 * @return true if successful, false otherwise
	 */
	public static boolean assignRole(String userName, String roleName) {
		// check if a userName is null
		if (userName == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		if (!SecRelSystem.userNames.containsKey(userName))
			throw new IllegalArgumentException("User with that name does not exist.");
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role with that name does not exist.");
		Integer userId = SecRelSystem.userNames.get(userName);
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		if (SecRelSystem.memberForwardMap.contains(forwardRow) || SecRelSystem.memberBackwardMap.contains(backwardRow))
			throw new IllegalStateException("User is already assigned to that Role: " + forwardRow + "( "
			        + SecRelSystem.userNames.get(userName) + ", " + SecRelSystem.roleNames.get(roleName) + " ), "
			        + backwardRow + "( " + SecRelSystem.roleNames.get(roleName) + ", "
			        + SecRelSystem.userNames.get(userName) + " )");
		SecRelSystem.memberForwardMap.add(forwardRow);
		SecRelSystem.memberBackwardMap.add(backwardRow);
		return true;
	}

	/**
	 * Assigns the specified user to the specified role.
	 *
	 * @param user the User instance
	 * @param role the Role instance
	 * @return true if successful, false otherwise
	 */
	public static boolean assignRole(User user, Role role) {
		// check if a user is null
		if (user == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		if (!SecRelSystem.userIds.containsKey(user.getId()))
			throw new IllegalArgumentException("User does not exist.");
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(role.getId()))
			throw new IllegalArgumentException("Role does not exist.");
		Integer userId = user.getId();
		Integer roleId = role.getId();
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		if (SecRelSystem.memberForwardMap.contains(forwardRow) || SecRelSystem.memberBackwardMap.contains(backwardRow))
			throw new IllegalStateException("User is already assigned to that Role: " + forwardRow + "( " + user.getId()
			        + ", " + role.getId() + " ), " + backwardRow + "( " + role.getId() + ", " + user.getId() + " )");
		SecRelSystem.memberForwardMap.add(forwardRow);
		SecRelSystem.memberBackwardMap.add(backwardRow);
		return true;
	}

	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @param accessType <!-- TODO -->
	 * @return true if successful, false otherwise
	 */
	public static boolean authorizeRole(Integer roleId, Integer serviceId, int accessType) {
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		// check if a serviceId is null
		if (serviceId == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		if (!SecRelSystem.serviceIds.containsKey(serviceId))
			throw new IllegalArgumentException("Service with that id does not exist.");
		Right right = new Right(roleId, serviceId, accessType);
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		if (SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        || SecRelSystem.serviceBackwardMap.containsKey(backwardRow))
			throw new IllegalStateException(
			        "Role is already authorized to invoke that Service: " + forwardRow + ", " + backwardRow);
		SecRelSystem.serviceForwardMap.put(forwardRow, right);
		SecRelSystem.serviceBackwardMap.put(backwardRow, right);
		return true;
	}

	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param role the Role instance
	 * @param service the Service instance
	 * @param accessType <!-- TODO -->
	 * @return true if successful, false otherwise
	 */
	public static boolean authorizeRole(Role role, Service service, int accessType) {
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
		Right right = new Right(role.getId(), service.getId(), accessType);
		Integer roleId = role.getId();
		Integer serviceId = service.getId();
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		if (SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        || SecRelSystem.serviceBackwardMap.containsKey(backwardRow))
			throw new IllegalStateException(
			        "Role is already authorized to invoke that Service: " + forwardRow + ", " + backwardRow);
		SecRelSystem.serviceForwardMap.put(forwardRow, right);
		SecRelSystem.serviceBackwardMap.put(backwardRow, right);
		return true;
	}

	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param roleName the name of the role
	 * @param serviceName the name of the service
	 * @param accessType <!-- TODO -->
	 * @return true if successful, false otherwise
	 */
	public static boolean authorizeRole(String roleName, String serviceName, int accessType) {
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
		Service service = SecRelSystem.serviceIds.get(SecRelSystem.serviceNames.get(serviceName));
		Right right = new Right(SecRelSystem.roleNames.get(roleName), service.getId(), accessType);
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		Integer serviceId = SecRelSystem.serviceNames.get(serviceName);
		long forwardRow = SecRelSystem.serviceForwardRow(roleId, serviceId);
		long backwardRow = SecRelSystem.serviceBackwardRow(roleId, serviceId);
		if (SecRelSystem.serviceForwardMap.containsKey(forwardRow)
		        || SecRelSystem.serviceBackwardMap.containsKey(backwardRow))
			throw new IllegalStateException(
			        "Role is already authorized to invoke that Service: " + forwardRow + ", " + backwardRow);
		SecRelSystem.serviceForwardMap.put(forwardRow, right);
		SecRelSystem.serviceBackwardMap.put(backwardRow, right);
		return true;
	}

	/**
	 * Creates a new role with the specified role name.
	 *
	 * @param name the name of the new role
	 * @return a Role instance representing the new role
	 */
	public static Role createRole(String name) {
		// check if a name is null
		if (name == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		// check if a role with that name already exists
		if (SecRelSystem.roleNames.containsKey(name))
		    // throw exception
		    throw new IllegalArgumentException(
		            String.format("A Role with the name \"%s\" already exists. Role(%d,\"%s\")", name,
		                    SecRelSystem.roleNames.get(name), name));
		// compute new roleId
		Integer id = SecRelSystem.roleIds.isEmpty() ? 0 : SecRelSystem.roleIds.lastKey() + 1;
		SecRelSystem.roleIds.put(id, name);
		SecRelSystem.roleNames.put(name, id);
		return new Role(id);
	}

	/**
	 * Creates a new user with the specified user name.
	 *
	 * @param name the name of the new user
	 * @return a User instance representing the new user
	 */
	public static User createUser(String name) {
		// check if a name is null
		if (name == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		// check if a user with that name already exists
		if (SecRelSystem.userNames.containsKey(name))
		    // throw exception
		    throw new IllegalArgumentException(
		            String.format("A User with the name \"%s\" already exists. User(%d,\"%s\")", name,
		                    SecRelSystem.userNames.get(name), name));
		// compute new userId
		Integer id = SecRelSystem.userIds.isEmpty() ? 0 : SecRelSystem.userIds.lastKey() + 1;
		SecRelSystem.userIds.put(id, name);
		SecRelSystem.userNames.put(name, id);
		return new User(id);
	}

	/**
	 * Finds the role with the specified role ID if one exists.
	 *
	 * @param roleId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static Role findRole(Integer roleId) {
		if (SecRelSystem.roleIds.containsKey(roleId))
			return new Role(roleId);
		throw new IllegalArgumentException("Role with that id does not exist");
	}

	/**
	 * Finds the role with the specified role name if one exists.
	 *
	 * @param roleName <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static Role findRole(String roleName) {
		if (SecRelSystem.roleNames.containsKey(roleName))
			return new Role(SecRelSystem.roleNames.get(roleName));
		throw new IllegalArgumentException("Role with that name does not exist");
	}

	/**
	 * Finds the service with the specified service ID if one exists.
	 *
	 * @param serviceId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static Service findService(Integer serviceId) {
		// <!-- TODO -->
		throw new RuntimeException("not implemented");
	}

	/**
	 * Finds the service with the specified service name if one exists.
	 *
	 * @param serviceName <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static Service findService(String serviceName) {
		// <!-- TODO -->
		throw new RuntimeException("not implemented");
	}

	/**
	 * Finds the user with the specified user ID if one exists.
	 *
	 * @param userId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static User findUser(Integer userId) {
		if (SecRelSystem.userIds.containsKey(userId))
			return new User(userId);
		throw new IllegalArgumentException("User with that id does not exist");
	}

	/**
	 * Finds the user with the specified user name if one exists.
	 *
	 * @param userName <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static User findUser(String userName) {
		if (SecRelSystem.userNames.containsKey(userName))
			return new User(SecRelSystem.userNames.get(userName));
		throw new IllegalArgumentException("User with that name does not exist");
	}

	/**
	 * Returns the roles authorized to invoke the service with the specified ID.
	 *
	 * @param serviceId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static NavigableSet<Role> getAuthorizedRoles(Integer serviceId) {
		Integer[] ids = SecRelSystem.getAuthorizedIds(serviceId);
		List<Integer> roles = Arrays.asList(ids);
		return roles.stream().map(id -> new Role(id)).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the services the role with the specified ID is authorized to
	 * invoke.
	 *
	 * @param roleId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static NavigableSet<Service> getAuthorizedServices(Integer roleId) {
		Integer[] ids = SecRelSystem.getServiceIds(roleId);
		List<Integer> services = Arrays.asList(ids);
		return services.stream().map(id -> SecRelSystem.serviceIds.get(id))
		        .collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the users assigned to the role with the specified ID.
	 *
	 * @param roleId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static NavigableSet<User> getMembers(Integer roleId) {
		Integer[] ids = SecRelSystem.getMemberIds(roleId);
		List<Integer> users = Arrays.asList(ids);
		return users.stream().map(id -> new User(id)).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the rights of the role with the specified ID.
	 *
	 * @param roleId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static Set<Right> getRoleRights(Integer roleId) {
		Integer[] ids = SecRelSystem.getServiceIds(roleId);
		List<Integer> services = Arrays.asList(ids);
		return services.stream()
		        .map(id -> SecRelSystem.serviceForwardMap.get(SecRelSystem.serviceForwardRow(roleId, id)))
		        .collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * Returns the roles to which the user with the specified ID is assigned.
	 *
	 * @param userId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static NavigableSet<Role> getRoles(Integer userId) {
		Integer[] ids = SecRelSystem.getRoleIds(userId);
		List<Integer> roles = Arrays.asList(ids);
		return roles.stream().map(id -> new Role(id)).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the rights of the service with the specified ID.
	 *
	 * @param serviceId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static Set<Right> getServiceRights(Integer serviceId) {
		Integer[] ids = SecRelSystem.getAuthorizedIds(serviceId);
		List<Integer> roles = Arrays.asList(ids);
		return roles.stream()
		        .map(id -> SecRelSystem.serviceForwardMap.get(SecRelSystem.serviceForwardRow(id, serviceId)))
		        .collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param roleId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasRole(Integer roleId) {
		return SecRelSystem.roleIds.containsKey(roleId);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param role <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasRole(Role role) {
		return SecRelSystem.hasRole(role.getId());
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param roleName <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasRole(String roleName) {
		return SecRelSystem.roleNames.containsKey(roleName);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param serviceId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasService(Integer serviceId) {
		return SecRelSystem.serviceIds.containsKey(serviceId);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param service <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasService(Service service) {
		return SecRelSystem.serviceIds.containsValue(service);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param serviceName <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasService(String serviceName) {
		return SecRelSystem.serviceNames.containsKey(serviceName);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasUser(Integer userId) {
		return SecRelSystem.userIds.containsKey(userId);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userName <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasUser(String userName) {
		return SecRelSystem.userNames.containsKey(userName);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param user <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean hasUser(User user) {
		return SecRelSystem.hasUser(user.getId());
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param roleId <!-- TODO -->
	 * @param serviceId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean isAuthorizedFor(Integer roleId, Integer serviceId) {
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		// check if a serviceId is null
		if (serviceId == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		if (!SecRelSystem.serviceIds.containsKey(serviceId))
			throw new IllegalArgumentException("Service with that id does not exist.");
		return SecRelSystem.serviceForwardMap.containsKey(roleId.longValue() << 32 | serviceId);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param role <!-- TODO -->
	 * @param service <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean isAuthorizedFor(Role role, Service service) {
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
		return SecRelSystem.serviceForwardMap.containsKey((long) role.getId() << 32 | service.getId());
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param roleName <!-- TODO -->
	 * @param serviceName <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean isAuthorizedFor(String roleName, String serviceName) {
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
		return SecRelSystem.serviceForwardMap.containsKey(SecRelSystem.roleNames.get(roleName).longValue() << 32
		        | SecRelSystem.serviceIds.get(SecRelSystem.serviceNames.get(serviceName)).getId());
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userId <!-- TODO -->
	 * @param roleId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean isMemberOf(Integer userId, Integer roleId) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User with that id does not exist.");
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		return SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userName <!-- TODO -->
	 * @param roleName <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean isMemberOf(String userName, String roleName) {
		// check if a userName is null
		if (userName == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		if (!SecRelSystem.userNames.containsKey(userName))
			throw new IllegalArgumentException("User with that name does not exist.");
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role with that name does not exist.");
		Integer userId = SecRelSystem.userNames.get(userName);
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		return SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param user <!-- TODO -->
	 * @param role <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean isMemberOf(User user, Role role) {
		// check if a user is null
		if (user == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		if (!SecRelSystem.userIds.containsKey(user.getId()))
			throw new IllegalArgumentException("User does not exist.");
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(role.getId()))
			throw new IllegalArgumentException("Role does not exist.");
		Integer userId = user.getId();
		Integer roleId = role.getId();
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		return SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userId <!-- TODO -->
	 * @param serviceId <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean makeRequest(Integer userId, Integer serviceId) {
		/* <!-- TODO --> */ throw new RuntimeException("not implemented");
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userId <!-- TODO -->
	 * @param serviceId <!-- TODO -->
	 * @param data <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static Service.Handle makeRequest(Integer userId, Integer serviceId, InputStream data) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User with that id does not exist.");
		// check if a serviceId is null
		if (serviceId == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		if (!SecRelSystem.serviceIds.containsKey(serviceId))
			throw new IllegalArgumentException("Service with that id does not exist.");
		Service service = SecRelSystem.serviceIds.get(serviceId);
		ReferenceMonitor monitor = service.monitor(userId);
		if (monitor.checkRights())
			return service.invokeService(data);
		return null;
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userId <!-- TODO -->
	 * @param serviceId <!-- TODO -->
	 * @param data <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean makeRequest(Integer userId, Integer serviceId, String data) {
		/* <!-- TODO --> */ throw new RuntimeException("not implemented");
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param service <!-- TODO -->
	 * @return <!-- TODO -->
	 */
	public static boolean registerService(Service service) {
		// check if a service is null
		if (service == null)
		    // throw exception
		    throw new NullPointerException("Service cannot be null.");
		// check if service is already registered
		if (SecRelSystem.serviceIds.containsValue(service))
		    // exit method
		    return false;
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
		SecRelSystem.serviceIds.put(id, service);
		SecRelSystem.serviceNames.put(service.getName(), id);
		return true;
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param roleId <!-- TODO -->
	 */
	public static void removeRole(Integer roleId) {
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role does not exist.");
		SecRelSystem.clearRoleMembers(roleId);
		SecRelSystem.roleNames.remove(SecRelSystem.roleIds.get(roleId));
		SecRelSystem.roleIds.remove(roleId);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param role <!-- TODO -->
	 */
	public static void removeRole(Role role) {
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role cannot be null.");
		SecRelSystem.removeRole(role.getId());
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param roleName <!-- TODO -->
	 */
	public static void removeRole(String roleName) {
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role does not exist.");
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		SecRelSystem.clearRoleMembers(roleId);
		SecRelSystem.roleIds.remove(roleId);
		SecRelSystem.roleNames.remove(roleName);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param serviceId <!-- TODO -->
	 */
	public static void removeService(Integer serviceId) {
		// check if a serviceId is null
		if (serviceId == null)
		    // throw exception
		    throw new NullPointerException("Service id cannot be null.");
		if (!SecRelSystem.serviceIds.containsKey(serviceId))
			throw new IllegalArgumentException("Service does not exist.");
		Service service = SecRelSystem.serviceIds.get(serviceId);
		SecRelSystem.serviceNames.remove(service.getName());
		SecRelSystem.serviceIds.remove(serviceId);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param service <!-- TODO -->
	 */
	public static void removeService(Service service) {
		// check if a service is null
		if (service == null)
		    // throw exception
		    throw new NullPointerException("Service cannot be null.");
		SecRelSystem.removeService(service.getId());
		// check if service is registered
		if (!SecRelSystem.serviceNames.containsValue(service))
			throw new IllegalArgumentException("Service is not registered.");
		SecRelSystem.serviceIds.remove(service.getId());
		SecRelSystem.serviceNames.remove(service.getName());
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param serviceName <!-- TODO -->
	 */
	public static void removeService(String serviceName) {
		// check if a serviceName is null
		if (serviceName == null)
		    // throw exception
		    throw new NullPointerException("Service name cannot be null.");
		if (!SecRelSystem.serviceNames.containsKey(serviceName))
			throw new IllegalArgumentException("Service does not exist.");
		Service service = SecRelSystem.serviceIds.get(SecRelSystem.serviceNames.get(serviceName));
		SecRelSystem.serviceIds.remove(service.getId());
		SecRelSystem.serviceNames.remove(serviceName);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userId <!-- TODO -->
	 */
	public static void removeUser(Integer userId) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User does not exist.");
		SecRelSystem.clearUserRoles(userId);
		SecRelSystem.userNames.remove(SecRelSystem.userIds.get(userId));
		SecRelSystem.userIds.remove(userId);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param userName <!-- TODO -->
	 */
	public static void removeUser(String userName) {
		// check if a userName is null
		if (userName == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		if (!SecRelSystem.userNames.containsKey(userName))
			throw new IllegalArgumentException("User does not exist.");
		Integer userId = SecRelSystem.userNames.get(userName);
		SecRelSystem.clearUserRoles(userId);
		SecRelSystem.userIds.remove(userId);
		SecRelSystem.userNames.remove(userName);
	}

	/**
	 * <!-- TODO -->
	 *
	 * @param user <!-- TODO -->
	 */
	public static void removeUser(User user) {
		// check if a user is null
		if (user == null)
		    // throw exception
		    throw new NullPointerException("User cannot be null.");
		SecRelSystem.removeUser(user.getId());
	}

	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @return true if successful, false otherwise
	 */
	public static boolean unassignRole(Integer userId, Integer roleId) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User with that id does not exist.");
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		long forwardRow = userId.longValue() << 32 | roleId;
		long backwardRow = roleId.longValue() << 32 | userId;
		if (!(SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow)))
			throw new IllegalArgumentException("User was not assigned to that Role");
		return SecRelSystem.memberForwardMap.remove(forwardRow) && SecRelSystem.memberBackwardMap.remove(backwardRow);
	}

	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param userName the name of the user
	 * @param roleName the name of the role
	 * @return true if successful, false otherwise
	 */
	public static boolean unassignRole(String userName, String roleName) {
		// check if a userName is null
		if (userName == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		if (!SecRelSystem.userNames.containsKey(userName))
			throw new IllegalArgumentException("User with that name does not exist.");
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role with that name does not exist.");
		long forwardRow = SecRelSystem.userNames.get(userName).longValue() << 32 | SecRelSystem.roleNames.get(roleName);
		long backwardRow = SecRelSystem.roleNames.get(roleName).longValue() << 32
		        | SecRelSystem.userNames.get(userName);
		if (!(SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow)))
			throw new IllegalArgumentException("User was not assigned to that Role");
		return SecRelSystem.memberForwardMap.remove(forwardRow) && SecRelSystem.memberBackwardMap.remove(backwardRow);
	}

	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param user the User instance
	 * @param role the Role instance
	 * @return true if successful, false otherwise
	 */
	public static boolean unassignRole(User user, Role role) {
		// check if a user is null
		if (user == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		if (!SecRelSystem.userIds.containsKey(user.getId()))
			throw new IllegalArgumentException("User does not exist.");
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		if (!SecRelSystem.roleIds.containsKey(role.getId()))
			throw new IllegalArgumentException("Role does not exist.");
		long forwardRow = (long) user.getId() << 32 | role.getId();
		long backwardRow = (long) role.getId() << 32 | user.getId();
		if (!(SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow)))
			throw new IllegalArgumentException("User was not assigned to that Role");
		return SecRelSystem.memberForwardMap.remove(forwardRow) && SecRelSystem.memberBackwardMap.remove(backwardRow);
	}

	/**
	 * <!-- TODO -->
	 */
	private SecRelSystem() {
	}

}