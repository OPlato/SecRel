/**
 * <p>
 * This file defines the Users class. The Users class contains methods to
 * manipulate the data in the SecRelSystem concerning users.
 * </p>
 */
package edu.fgcu.secrel;

/**
 * This class contains methods for creating, removing, and querying information
 * about users in the ScRelSystem.
 *
 * @author lngibson
 *
 */
public class Users {

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
		// add user to system
		SecRelSystem.userIds.put(id, name);
		SecRelSystem.userNames.put(name, id);
		return new User(id);
	}

	/**
	 * Finds the user with the specified user ID if one exists.
	 *
	 * @param userId the id of the user
	 * @return the corresponding User instance
	 */
	public static User findUser(Integer userId) {
		if (SecRelSystem.userIds.containsKey(userId))
			return new User(userId);
		throw new IllegalArgumentException("User with that id does not exist");
	}
	
	/**
	 * Finds the user with the specified user name if one exists.
	 *
	 * @param userName the name of the user
	 * @return the corresponding User instance
	 */
	public static User findUser(String userName) {
		if (SecRelSystem.userNames.containsKey(userName))
			return new User(SecRelSystem.userNames.get(userName));
		throw new IllegalArgumentException("User with that name does not exist");
	}
	
	/**
	 * Returns whether the system has a user with the specified id.
	 *
	 * @param userId the id of the user
	 * @return true if there is such a user
	 */
	public static boolean hasUser(Integer userId) {
		return SecRelSystem.userIds.containsKey(userId);
	}

	/**
	 * Returns whether the system has a user with the specified name.
	 *
	 * @param userName the name of the user
	 * @return true if there is such a user
	 */
	public static boolean hasUser(String userName) {
		return SecRelSystem.userNames.containsKey(userName);
	}

	/**
	 * Returns whether the user instance corresponds to a user in the system.
	 *
	 * @param user the user instance
	 * @return true if there is such a user
	 */
	public static boolean hasUser(User user) {
		return hasUser(user.getId());
	}

	/**
	 * Removes the specified user from the system.
	 *
	 * @param userId the id of the user
	 */
	public static void removeUser(Integer userId) {
		// check if a userId is null
		if (userId == null)
		    // throw exception
		    throw new NullPointerException("User id cannot be null.");
		// check if user exists
		if (!SecRelSystem.userIds.containsKey(userId))
			throw new IllegalArgumentException("User does not exist.");
		// clear user role assignments
		SecRelSystem.clearUserRoles(userId);
		// remove role from system
		SecRelSystem.userNames.remove(SecRelSystem.userIds.get(userId));
		SecRelSystem.userIds.remove(userId);
	}

	/**
	 * Removes the specified user from the system.
	 *
	 * @param userName the string of the user
	 */
	public static void removeUser(String userName) {
		// check if a userName is null
		if (userName == null)
		    // throw exception
		    throw new NullPointerException("User name cannot be null.");
		// check if user exists
		if (!SecRelSystem.userNames.containsKey(userName))
			throw new IllegalArgumentException("User does not exist.");
		Integer userId = SecRelSystem.userNames.get(userName);
		// clear user role assignments
		SecRelSystem.clearUserRoles(userId);
		// remove role from system
		SecRelSystem.userIds.remove(userId);
		SecRelSystem.userNames.remove(userName);
	}

	/**
	 * Removes the specified user from the system.
	 *
	 * @param user the user instance
	 */
	public static void removeUser(User user) {
		// check if a user is null
		if (user == null)
		    // throw exception
		    throw new NullPointerException("User cannot be null.");
		// delegate to id version
		removeUser(user.getId());
	}

	/**
	 * Prevents instantiation of the Users class.
	 */
	private Users() {
	}
	
}