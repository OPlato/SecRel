/**
 * <p>
 * This file defines the Services class. The Services class contains methods to
 * manipulate the data in the SecRelSystem concerning user-role membership.
 * </p>
 */
package edu.fgcu.secrel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains methods for (un)assigning users to roles in the
 * ScRelSystem and testing user-role membership.
 *
 * @author lngibson
 *
 */
public class Members {

	/**
	 * Assigns the specified user to the specified role.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 */
	public static void assignRole(Integer userId, Integer roleId) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		// check if user exists
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User with that id does not exist.");
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		// construct mapping rows
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check if rows exist
		if (SecRelSystem.memberForwardMap.contains(forwardRow) || SecRelSystem.memberBackwardMap.contains(backwardRow))
			throw new IllegalStateException("User is already assigned to that Role: " + forwardRow + "( " + userId
			        + ", " + roleId + " ), " + backwardRow + "( " + roleId + ", " + userId + " )");
		// add rows to system
		SecRelSystem.memberForwardMap.add(forwardRow);
		SecRelSystem.memberBackwardMap.add(backwardRow);
	}

	/**
	 * Assigns the specified user to the specified role.
	 *
	 * @param userName the name of the user
	 * @param roleName the name of the role
	 */
	public static void assignRole(String userName, String roleName) {
		// check if a userName is null
		if (userName == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		// check if user exists
		if (!SecRelSystem.userNames.containsKey(userName))
			throw new IllegalArgumentException("User with that name does not exist.");
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role with that name does not exist.");
		// construct mapping row
		Integer userId = SecRelSystem.userNames.get(userName);
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check if rows exist
		if (SecRelSystem.memberForwardMap.contains(forwardRow) || SecRelSystem.memberBackwardMap.contains(backwardRow))
			throw new IllegalStateException("User is already assigned to that Role: " + forwardRow + "( "
			        + SecRelSystem.userNames.get(userName) + ", " + SecRelSystem.roleNames.get(roleName) + " ), "
			        + backwardRow + "( " + SecRelSystem.roleNames.get(roleName) + ", "
			        + SecRelSystem.userNames.get(userName) + " )");
		// add rows to system
		SecRelSystem.memberForwardMap.add(forwardRow);
		SecRelSystem.memberBackwardMap.add(backwardRow);
	}

	/**
	 * Assigns the specified user to the specified role.
	 *
	 * @param user the User instance
	 * @param role the Role instance
	 */
	public static void assignRole(User user, Role role) {
		// check if a user is null
		if (user == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		// check if user exists
		if (!SecRelSystem.userIds.containsKey(user.getId()))
			throw new IllegalArgumentException("User does not exist.");
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(role.getId()))
			throw new IllegalArgumentException("Role does not exist.");
		// construct mapping rows
		Integer userId = user.getId();
		Integer roleId = role.getId();
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check if rows exist
		if (SecRelSystem.memberForwardMap.contains(forwardRow) || SecRelSystem.memberBackwardMap.contains(backwardRow))
			throw new IllegalStateException("User is already assigned to that Role: " + forwardRow + "( " + user.getId()
			        + ", " + role.getId() + " ), " + backwardRow + "( " + role.getId() + ", " + user.getId() + " )");
		// add rows to system
		SecRelSystem.memberForwardMap.add(forwardRow);
		SecRelSystem.memberBackwardMap.add(backwardRow);
	}

	/**
	 * Returns the users assigned to the role with the specified ID.
	 *
	 * @param roleId the id of the role
	 * @return the users assigned to this role
	 */
	public static NavigableSet<User> getMembers(Integer roleId) {
		// retrieves the user ids
		Integer[] ids = SecRelSystem.getMemberIds(roleId);
		// convert to list
		List<Integer> users = Arrays.asList(ids);
		// stream into User set
		return users.stream().map(id -> new User(id)).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the roles to which the user with the specified ID is assigned.
	 *
	 * @param userId the id of the user
	 * @return the roles to which the user is assigned
	 */
	public static NavigableSet<Role> getRoles(Integer userId) {
		// retrieve the role ids
		Integer[] ids = SecRelSystem.getRoleIds(userId);
		// convert to list
		List<Integer> roles = Arrays.asList(ids);
		// stream into Role set
		return roles.stream().map(id -> new Role(id)).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns whether the specified user is assigned to the specified role.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 * @return true if the user is assigned to the role
	 */
	public static boolean isMemberOf(Integer userId, Integer roleId) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		// check if user exists
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User with that id does not exist.");
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		// construct mapping rows
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check existence of mapping rows
		return SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow);
	}

	/**
	 * Returns whether the specified user is assigned to the specified role.
	 *
	 * @param userName the name of the user
	 * @param roleName the name of the role
	 * @return true if the user is assigned to the role
	 */
	public static boolean isMemberOf(String userName, String roleName) {
		// check if a userName is null
		if (userName == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		// check if user exists
		if (!SecRelSystem.userNames.containsKey(userName))
			throw new IllegalArgumentException("User with that name does not exist.");
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role with that name does not exist.");
		// construct mapping rows
		Integer userId = SecRelSystem.userNames.get(userName);
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check existence of mapping rows
		return SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow);
	}

	/**
	 * Returns whether the specified user is assigned to the specified role.
	 *
	 * @param user the user instance
	 * @param role the role instance
	 * @return true if the user is assigned to the role
	 */
	public static boolean isMemberOf(User user, Role role) {
		// check if a user is null
		if (user == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		// check if user exists
		if (!SecRelSystem.userIds.containsKey(user.getId()))
			throw new IllegalArgumentException("User does not exist.");
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(role.getId()))
			throw new IllegalArgumentException("Role does not exist.");
		// construct mapping rows
		Integer userId = user.getId();
		Integer roleId = role.getId();
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check existence of mapping rows
		return SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow);
	}

	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 */
	public static void unassignRole(Integer userId, Integer roleId) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		// check if user exists
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User with that id does not exist.");
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role with that id does not exist.");
		// construct mapping rows
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check existence of mapping rows
		if (!(SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow)))
			throw new IllegalArgumentException("User was not assigned to that Role");
		// remove rows from system
		SecRelSystem.memberForwardMap.remove(forwardRow);
		SecRelSystem.memberBackwardMap.remove(backwardRow);
	}

	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param userName the name of the user
	 * @param roleName the name of the role
	 */
	public static void unassignRole(String userName, String roleName) {
		// check if a userName is null
		if (userName == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		// check if user exists
		if (!SecRelSystem.userNames.containsKey(userName))
			throw new IllegalArgumentException("User with that name does not exist.");
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role with that name does not exist.");
		// construct mapping rows
		Integer userId = SecRelSystem.userNames.get(userName);
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check existence of mapping rows
		if (!(SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow)))
			throw new IllegalArgumentException("User was not assigned to that Role");
		// remove rows from system
		SecRelSystem.memberForwardMap.remove(forwardRow);
		SecRelSystem.memberBackwardMap.remove(backwardRow);
	}

	/**
	 * Unassigns the specified user from the specified role.
	 *
	 * @param user the User instance
	 * @param role the Role instance
	 */
	public static void unassignRole(User user, Role role) {
		// check if a user is null
		if (user == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		// check if user exists
		if (!SecRelSystem.userIds.containsKey(user.getId()))
			throw new IllegalArgumentException("User does not exist.");
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(role.getId()))
			throw new IllegalArgumentException("Role does not exist.");
		// construct mapping rows
		Integer userId = user.getId();
		Integer roleId = role.getId();
		long forwardRow = SecRelSystem.memberForwardRow(userId, roleId);
		long backwardRow = SecRelSystem.memberBackwardRow(userId, roleId);
		// check existence of mapping rows
		if (!(SecRelSystem.memberForwardMap.contains(forwardRow)
		        && SecRelSystem.memberBackwardMap.contains(backwardRow)))
			throw new IllegalArgumentException("User was not assigned to that Role");
		// remove rows from system
		SecRelSystem.memberForwardMap.remove(forwardRow);
		SecRelSystem.memberBackwardMap.remove(backwardRow);
	}

	/**
	 * Prevents instantiation of the Members class.
	 */
	private Members() {
	}
	
}