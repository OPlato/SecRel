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
// Note: authorizeRole(accessType)
package edu.fgcu.secrel;

import java.util.*;
import java.util.concurrent.*;

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
 * <h2>Operation</h2>
 * <p>
 * The user identities are setup with calls to createremoveUser,
 * create/removeRole and (un)assignRole. The services and rights are setup with
 * calls to register/removeService and authorizeRole. <!-- unauthorizeRole is
 * not implemented -->
 * </p>
 * <h2>Conventions</h2>
 * <p>
 * Methods that can fail report failures by throwing subclasses of
 * RuntimeException, specifically, NullPointerException,
 * IllegalArgumentException and IllegalStateException.
 * </p>
 * <h2>Unfinished Sections</h2>
 * <p>
 * The create/remove/assign/unassign mechanisms of the user/role system is
 * mirrored in the role/service system, however, some changes to the user/role
 * system may not have been applied to the role/service system. For example, I
 * am not sure that for all the uses of member(For|Back)wardRow in the user/role
 * system there are corresponding calls to service(For|Back)wardRow in the
 * role/service. Furthermore, do to the lack of a Service implementation, while
 * the user/role system is extensively tested, the role/service system is widely
 * untested.
 * </p>
 * <h3>Proposed</h3>
 * <p>
 * It was intended to have delegate methods in the Role and User classes to
 * create the illusion that they actually contained some logic.
 * </p>
 * <h2>Changes</h2>
 * <p>
 * Due to the growing size of this class, I moved all but three
 * methods(makeRequest) to separate classes, i.e. Users, Roles, Members,
 * Services and Authorizations. I debated removing the depreciated delegate
 * functions from this class.
 * </p>
 * <p>
 * A toy service, AccumulatorService, has been implemented. It still needs to be
 * tested.
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
	 * Clears all assignments for the specified role. This is called when a role
	 * is removed. If the mappings remain, if the role id is reused, the system
	 * will inadvertently assume membership of the removed role's user in the
	 * new role.
	 *
	 * @param roleId the id of the role
	 */
	protected static void clearRoleMembers(Integer roleId) {
		// iterate the user ids of the role members
		for (Integer userId : SecRelSystem.getMemberIds(roleId)) {
			// construct forward and backward mapping rows
			long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
			long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
			// remove mappings from system
			SecRelSystem.memberForwardMap.remove(forwardRow);
			SecRelSystem.memberBackwardMap.remove(backwardRow);
		}
	}
	
	/**
	 * Clears all assignments for the specified user. This is called when a user
	 * is removed. If the mappings remain, if the user id is reused, the system
	 * will inadvertently assume membership of the new user in the roles of the
	 * removed user.
	 *
	 * @param userId the id of the user
	 */
	protected static void clearUserRoles(Integer userId) {
		// iterate the role ids of the user roles
		for (Integer roleId : SecRelSystem.getRoleIds(userId)) {
			// construct forward and backward mapping rows
			long forwardRow = userId.longValue() << 32 | roleId;
			long backwardRow = roleId.longValue() << 32 | userId;
			// remove mappings from system
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
		// retrieve elements of backwards map corresponding to this service
		Map<Long, Right> entries = SecRelSystem.serviceBackwardMap.subMap(serviceId.longValue() << 32,
				serviceId.longValue() + 1 << 32);
		// initialize output array
		Integer[] ids = new Integer[entries.size()];
		// extract role ids from mapping rows
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
		// retrieve elements of backward map corresponding to this role
		Set<Long> entries = SecRelSystem.memberBackwardMap.subSet(roleId.longValue() << 32,
				roleId.longValue() + 1 << 32);
		// initialize output array
		Integer[] ids = new Integer[entries.size()];
		// extract user ids from mapping rows
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
		// retrieve elements of forward map corresponding to this user
		Set<Long> entries = SecRelSystem.memberForwardMap.subSet(userId.longValue() << 32,
				userId.longValue() + 1 << 32);
		// initialize output array
		Integer[] ids = new Integer[entries.size()];
		// extract role ids from mapping rows
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
		// retrieve elements of forward map corresponding to this role
		Map<Long, Right> entries = SecRelSystem.serviceForwardMap.subMap(roleId.longValue() << 32,
				roleId.longValue() + 1 << 32);
		// initialize output array
		Integer[] ids = new Integer[entries.size()];
		// extract service ids from mapping rows
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
	 * Returns the constructed mapping row for use in memberBackwardRow. It
	 * consists of a role id and a user id stored in a long in that order.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @return the mapping row
	 */
	protected static Long memberBackwardRow(Integer userId, Integer roleId) {
		return roleId.longValue() << 32 | userId;
	}
	
	/**
	 * Returns the constructed mapping row for use in memberForwardRow. It
	 * consists of a user id and a role id stored in a long in that order.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @return the mapping row
	 */
	protected static Long memberForwardRow(Integer userId, Integer roleId) {
		return userId.longValue() << 32 | roleId;
	}
	
	/**
	 * Returns the constructed mapping row for use in serviceBackwardRow. It
	 * consists of a service id and a role id stored in a long in that order.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @return the mapping row
	 */
	protected static Long serviceBackwardRow(Integer roleId, Integer serviceId) {
		return serviceId.longValue() << 32 | roleId;
	}
	
	/**
	 * Returns the constructed mapping row for use in serviceForwardRow. It
	 * consists of a role id and a service id stored in a long in that order.
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
	 * @deprecated Use {@link Members#assignRole(Integer,Integer)} instead
	 */
	@Deprecated
	public static void assignRole(Integer userId, Integer roleId) {
		Members.assignRole(userId, roleId);
	}
	
	/**
	 * Assigns the specified user to the specified role.
	 *
	 * @param userName the name of the user
	 * @param roleName the name of the role
	 * @deprecated Use {@link Members#assignRole(String,String)} instead
	 */
	@Deprecated
	public static void assignRole(String userName, String roleName) {
		Members.assignRole(userName, roleName);
	}
	
	/**
	 * Assigns the specified user to the specified role.
	 *
	 * @param user the User instance
	 * @param role the Role instance
	 * @deprecated Use {@link Members#assignRole(User,Role)} instead
	 */
	@Deprecated
	public static void assignRole(User user, Role role) {
		Members.assignRole(user, role);
	}
	
	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @param accessType I do not know the purpose of this parameter
	 * @deprecated Use {@link Authorizations#authorizeRole(Integer,Integer,int)}
	 *             instead
	 */
	@Deprecated
	public static void authorizeRole(Integer roleId, Integer serviceId, int accessType) {
		Authorizations.authorizeRole(roleId, serviceId, accessType);
	}
	
	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param role the Role instance
	 * @param service the Service instance
	 * @param accessType I do not know the purpose of this parameter
	 * @deprecated Use {@link Authorizations#authorizeRole(Role,Service,int)}
	 *             instead
	 */
	@Deprecated
	public static void authorizeRole(Role role, Service service, int accessType) {
		Authorizations.authorizeRole(role, service, accessType);
	}
	
	/**
	 * Authorizes the specified role to invoke the specified service.
	 *
	 * @param roleName the name of the role
	 * @param serviceName the name of the service
	 * @param accessType I do not know the purpose of this parameterI do not
	 *            know the purpose of this parameter
	 * @deprecated Use {@link Authorizations#authorizeRole(String,String,int)}
	 *             instead
	 */
	@Deprecated
	public static void authorizeRole(String roleName, String serviceName, int accessType) {
		Authorizations.authorizeRole(roleName, serviceName, accessType);
	}
	
	/**
	 * Creates a new role with the specified role name.
	 *
	 * @param name the name of the new role
	 * @return a Role instance representing the new role
	 * @deprecated Use {@link Roles#createRole(String)} instead
	 */
	@Deprecated
	public static Role createRole(String name) {
		return Roles.createRole(name);
	}
	
	/**
	 * Creates a new user with the specified user name.
	 *
	 * @param name the name of the new user
	 * @return a User instance representing the new user
	 * @deprecated Use {@link Users#createUser(String)} instead
	 */
	@Deprecated
	public static User createUser(String name) {
		return Users.createUser(name);
	}
	
	/**
	 * Finds the role with the specified role ID if one exists.
	 *
	 * @param roleId the id of the role
	 * @return the corresponding Role instance
	 * @deprecated Use {@link Roles#findRole(Integer)} instead
	 */
	@Deprecated
	public static Role findRole(Integer roleId) {
		return Roles.findRole(roleId);
	}
	
	/**
	 * Finds the role with the specified role name if one exists.
	 *
	 * @param roleName the name of the role
	 * @return the corresponding Role instance
	 * @deprecated Use {@link Roles#findRole(String)} instead
	 */
	@Deprecated
	public static Role findRole(String roleName) {
		return Roles.findRole(roleName);
	}
	
	/**
	 * Finds the service with the specified service ID if one exists.
	 *
	 * @param serviceId the id of the service
	 * @return the corresponding Service
	 * @deprecated Use {@link Services#findService(Integer)} instead
	 */
	@Deprecated
	public static Service findService(Integer serviceId) {
		return Services.findService(serviceId);
	}
	
	/**
	 * Finds the service with the specified service name if one exists.
	 *
	 * @param serviceName the name of the service
	 * @return the corresponding Service
	 * @deprecated Use {@link Services#findService(String)} instead
	 */
	@Deprecated
	public static Service findService(String serviceName) {
		return Services.findService(serviceName);
	}
	
	/**
	 * Finds the user with the specified user ID if one exists.
	 *
	 * @param userId the id of the user
	 * @return the corresponding User instance
	 * @deprecated Use {@link Users#findUser(Integer)} instead
	 */
	@Deprecated
	public static User findUser(Integer userId) {
		return Users.findUser(userId);
	}
	
	/**
	 * Finds the user with the specified user name if one exists.
	 *
	 * @param userName the name of the user
	 * @return the corresponding User instance
	 * @deprecated Use {@link Users#findUser(String)} instead
	 */
	@Deprecated
	public static User findUser(String userName) {
		return Users.findUser(userName);
	}
	
	/**
	 * Returns the roles authorized to invoke the service with the specified ID.
	 *
	 * @param serviceId the id of the service
	 * @return the roles authorized to invoke the service
	 * @deprecated Use {@link Authorizations#getAuthorizedRoles(Integer)}
	 *             instead
	 */
	@Deprecated
	public static NavigableSet<Role> getAuthorizedRoles(Integer serviceId) {
		return Authorizations.getAuthorizedRoles(serviceId);
	}
	
	/**
	 * Returns the services the role with the specified ID is authorized to
	 * invoke.
	 *
	 * @param roleId the id of the role
	 * @return the services the role is authorized to invoke
	 * @deprecated Use {@link Authorizations#getAuthorizedServices(Integer)}
	 *             instead
	 */
	@Deprecated
	public static NavigableSet<Service> getAuthorizedServices(Integer roleId) {
		return Authorizations.getAuthorizedServices(roleId);
	}
	
	/**
	 * Returns the users assigned to the role with the specified ID.
	 *
	 * @param roleId the id of the role
	 * @return the users assigned to this role
	 * @deprecated Use {@link Members#getMembers(Integer)} instead
	 */
	@Deprecated
	public static NavigableSet<User> getMembers(Integer roleId) {
		return Members.getMembers(roleId);
	}
	
	/**
	 * Returns the rights of the role with the specified ID.
	 *
	 * @param roleId the id of the role
	 * @return the rights associated with this role
	 * @deprecated Use {@link Authorizations#getRoleRights(Integer)} instead
	 */
	@Deprecated
	public static Set<Right> getRoleRights(Integer roleId) {
		return Authorizations.getRoleRights(roleId);
	}
	
	/**
	 * Returns the roles to which the user with the specified ID is assigned.
	 *
	 * @param userId the id of the user
	 * @return the roles to which the user is assigned
	 * @deprecated Use {@link Members#getRoles(Integer)} instead
	 */
	@Deprecated
	public static NavigableSet<Role> getRoles(Integer userId) {
		return Members.getRoles(userId);
	}
	
	/**
	 * Returns the rights of the service with the specified ID.
	 *
	 * @param serviceId the id of the service
	 * @return the rights associated with this service
	 * @deprecated Use {@link Authorizations#getServiceRights(Integer)} instead
	 */
	@Deprecated
	public static Set<Right> getServiceRights(Integer serviceId) {
		return Authorizations.getServiceRights(serviceId);
	}
	
	/**
	 * Returns whether the system has a role with the specified id.
	 *
	 * @param roleId the id of the role
	 * @return true if there is such a role
	 * @deprecated Use {@link Roles#hasRole(Integer)} instead
	 */
	@Deprecated
	public static boolean hasRole(Integer roleId) {
		return Roles.hasRole(roleId);
	}
	
	/**
	 * Returns whether the role instance corresponds to a role in the system.
	 *
	 * @param role the role instance
	 * @return true if there is such a role
	 * @deprecated Use {@link Roles#hasRole(Role)} instead
	 */
	@Deprecated
	public static boolean hasRole(Role role) {
		return Roles.hasRole(role);
	}
	
	/**
	 * Returns whether the system has a role with the specified name.
	 *
	 * @param roleName the name of the role
	 * @return true if there is such a role
	 * @deprecated Use {@link Roles#hasRole(String)} instead
	 */
	@Deprecated
	public static boolean hasRole(String roleName) {
		return Roles.hasRole(roleName);
	}
	
	/**
	 * Returns whether the system has a service with the specified id.
	 *
	 * @param serviceId the id of the service
	 * @return true if there is such a service
	 * @deprecated Use {@link Services#hasService(Integer)} instead
	 */
	@Deprecated
	public static boolean hasService(Integer serviceId) {
		return Services.hasService(serviceId);
	}
	
	/**
	 * Returns whether the service has been registered with the system.
	 *
	 * @param service the service
	 * @return true if the service has been registered
	 * @deprecated Use {@link Services#hasService(Service)} instead
	 */
	@Deprecated
	public static boolean hasService(Service service) {
		return Services.hasService(service);
	}
	
	/**
	 * Returns whether the system has a service with the specified name.
	 *
	 * @param serviceName the name of the service
	 * @return true if there is such a service
	 * @deprecated Use {@link Services#hasService(String)} instead
	 */
	@Deprecated
	public static boolean hasService(String serviceName) {
		return Services.hasService(serviceName);
	}
	
	/**
	 * Returns whether the system has a user with the specified id.
	 *
	 * @param userId the id of the user
	 * @return true if there is such a user
	 * @deprecated Use {@link Users#hasUser(Integer)} instead
	 */
	@Deprecated
	public static boolean hasUser(Integer userId) {
		return Users.hasUser(userId);
	}
	
	/**
	 * Returns whether the system has a user with the specified name.
	 *
	 * @param userName the name of the user
	 * @return true if there is such a user
	 * @deprecated Use {@link Users#hasUser(String)} instead
	 */
	@Deprecated
	public static boolean hasUser(String userName) {
		return Users.hasUser(userName);
	}
	
	/**
	 * Returns whether the user instance corresponds to a user in the system.
	 *
	 * @param user the user instance
	 * @return true if there is such a user
	 * @deprecated Use {@link Users#hasUser(User)} instead
	 */
	@Deprecated
	public static boolean hasUser(User user) {
		return Users.hasUser(user);
	}
	
	/**
	 * Returns whether the specified role is authorized to invoke the specified
	 * service.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @return true if the role is authorized to invoke the service
	 * @deprecated Use {@link Authorizations#isAuthorizedFor(Integer,Integer)}
	 *             instead
	 */
	@Deprecated
	public static boolean isAuthorizedFor(Integer roleId, Integer serviceId) {
		return Authorizations.isAuthorizedFor(roleId, serviceId);
	}
	
	/**
	 * Returns whether the specified role is authorized to invoke the specified
	 * service.
	 *
	 * @param role the role instance
	 * @param service the service
	 * @return true if the role is authorized to invoke the service
	 * @deprecated Use {@link Authorizations#isAuthorizedFor(Role,Service)}
	 *             instead
	 */
	@Deprecated
	public static boolean isAuthorizedFor(Role role, Service service) {
		return Authorizations.isAuthorizedFor(role, service);
	}
	
	/**
	 * Returns whether the specified role is authorized to invoke the specified
	 * service.
	 *
	 * @param roleName the name of the role
	 * @param serviceName the name of the service
	 * @return true if the role is authorized to invoke the service
	 * @deprecated Use {@link Authorizations#isAuthorizedFor(String,String)}
	 *             instead
	 */
	@Deprecated
	public static boolean isAuthorizedFor(String roleName, String serviceName) {
		return Authorizations.isAuthorizedFor(roleName, serviceName);
	}
	
	/**
	 * Returns whether the specified user is assigned to the specified role.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @return true if the user is assigned to the role
	 * @deprecated Use {@link Members#isMemberOf(Integer,Integer)} instead
	 */
	@Deprecated
	public static boolean isMemberOf(Integer userId, Integer roleId) {
		return Members.isMemberOf(userId, roleId);
	}
	
	/**
	 * Returns whether the specified user is assigned to the specified role.
	 *
	 * @param userName the name of the user
	 * @param roleName the name of the role
	 * @return true if the user is assigned to the role
	 * @deprecated Use {@link Members#isMemberOf(String,String)} instead
	 */
	@Deprecated
	public static boolean isMemberOf(String userName, String roleName) {
		return Members.isMemberOf(userName, roleName);
	}
	
	/**
	 * Returns whether the specified user is assigned to the specified role.
	 *
	 * @param user the user instance
	 * @param role the role instance
	 * @return true if the user is assigned to the role
	 * @deprecated Use {@link Members#isMemberOf(User,Role)} instead
	 */
	@Deprecated
	public static boolean isMemberOf(User user, Role role) {
		return Members.isMemberOf(user, role);
	}
	
	/**
	 * Attempts to invoke a service using the identity of the specified user.
	 * This function returns a Handle object which can be used to open a I/O
	 * connection to the executing thread of the service and to retrieve any
	 * results. This function calls makeRequest(Integer, Integer, InputStream)
	 * using an empty InputStream, that is, an InputStream that only returns
	 * EOF.
	 *
	 * @param userId userId the id of the user
	 * @param serviceId the id of the service
	 * @return the handle of the executing thread of the service if successful
	 */
	public static Service.Handle makeRequest(Integer userId, Integer serviceId) {
		return makeRequest(userId, serviceId, new String[0], new HashMap<String, String>());
	}
	
	/**
	 * Attempts to invoke a service using the identity of the specified user.
	 * This function returns a Handle object which can be used to open a I/O
	 * connection to the executing thread of the service and to retrieve any
	 * results. The input data is streamed into the service using the supplied
	 * InputStream.
	 *
	 * @param userId the id of the user
	 * @param serviceId the id of the service
	 * @param argv an array of parameters
	 * @param argm an map of parameters
	 * @return the handle of the executing thread of the service if successful
	 */
	public static Service.Handle makeRequest(Integer userId, Integer serviceId, String[] argv,
			Map<String, String> argm) {
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
			return service.invokeService(argv, argm);
		return null;
	}
	
	/**
	 * Registers the Service with the system.
	 *
	 * @param service the Service to be registered
	 * @deprecated Use {@link Services#registerService(Service)} instead
	 */
	@Deprecated
	public static void registerService(Service service) {
		Services.registerService(service);
	}
	
	/**
	 * Removes the specified role from the system.
	 *
	 * @param roleId the id of the role
	 * @deprecated Use {@link Roles#removeRole(Integer)} instead
	 */
	@Deprecated
	public static void removeRole(Integer roleId) {
		Roles.removeRole(roleId);
	}
	
	/**
	 * Removes the specified role from the system.
	 *
	 * @param role the role instance
	 * @deprecated Use {@link Roles#removeRole(Role)} instead
	 */
	@Deprecated
	public static void removeRole(Role role) {
		Roles.removeRole(role);
	}
	
	/**
	 * Removes the specified role from the system.
	 *
	 * @param roleName the name of the role
	 * @deprecated Use {@link Roles#removeRole(String)} instead
	 */
	@Deprecated
	public static void removeRole(String roleName) {
		Roles.removeRole(roleName);
	}
	
	/**
	 * Removes the specified service from the system.
	 *
	 * @param serviceId the id of the service
	 * @deprecated Use {@link Services#removeService(Integer)} instead
	 */
	@Deprecated
	public static void removeService(Integer serviceId) {
		Services.removeService(serviceId);
	}
	
	/**
	 * Removes the specified service from the system.
	 *
	 * @param service the service instance
	 * @deprecated Use {@link Services#removeService(Service)} instead
	 */
	@Deprecated
	public static void removeService(Service service) {
		Services.removeService(service);
	}
	
	/**
	 * Removes the specified service from the system.
	 *
	 * @param serviceName the name of the service
	 * @deprecated Use {@link Services#removeService(String)} instead
	 */
	@Deprecated
	public static void removeService(String serviceName) {
		Services.removeService(serviceName);
	}
	
	/**
	 * Removes the specified user from the system.
	 *
	 * @param userId the id of the user
	 * @deprecated Use {@link Users#removeUser(Integer)} instead
	 */
	@Deprecated
	public static void removeUser(Integer userId) {
		Users.removeUser(userId);
	}
	
	/**
	 * Removes the specified user from the system.
	 *
	 * @param userName the string of the user
	 * @deprecated Use {@link Users#removeUser(String)} instead
	 */
	@Deprecated
	public static void removeUser(String userName) {
		Users.removeUser(userName);
	}
	
	/**
	 * Removes the specified user from the system.
	 *
	 * @param user the user instance
	 * @deprecated Use {@link Users#removeUser(User)} instead
	 */
	@Deprecated
	public static void removeUser(User user) {
		Users.removeUser(user);
	}
	
	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @deprecated Use {@link Members#unassignRole(Integer,Integer)} instead
	 */
	@Deprecated
	public static void unassignRole(Integer userId, Integer roleId) {
		Members.unassignRole(userId, roleId);
	}
	
	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param userName the name of the user
	 * @param roleName the name of the role
	 * @deprecated Use {@link Members#unassignRole(String,String)} instead
	 */
	@Deprecated
	public static void unassignRole(String userName, String roleName) {
		Members.unassignRole(userName, roleName);
	}
	
	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param user the User instance
	 * @param role the Role instance
	 * @deprecated Use {@link Members#unassignRole(User,Role)} instead
	 */
	@Deprecated
	public static void unassignRole(User user, Role role) {
		Members.unassignRole(user, role);
	}
	
	/**
	 * Prevents instantiation of the SecRelSystem class.
	 */
	private SecRelSystem() {
	}
	
}