/**
 * <p>
 * This file defines the Roles class. The Roles class contains methods to
 * manipulate the data in the SecRelSystem concerning roles.
 * </p>
 */
package edu.fgcu.secrel;

/**
 * This class contains methods for creating, removing, and querying information
 * about roles in the ScRelSystem.
 *
 * @author lngibson
 *
 */
public class Roles {
	
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
		// add role to system
		SecRelSystem.roleIds.put(id, name);
		SecRelSystem.roleNames.put(name, id);
		return new Role(id);
	}
	
	/**
	 * Finds the role with the specified role ID if one exists.
	 *
	 * @param roleId the id of the role
	 * @return the corresponding Role instance
	 */
	public static Role findRole(Integer roleId) {
		if (SecRelSystem.roleIds.containsKey(roleId))
			return new Role(roleId);
		throw new IllegalArgumentException("Role with that id does not exist");
	}
	
	/**
	 * Finds the role with the specified role name if one exists.
	 *
	 * @param roleName the name of the role
	 * @return the corresponding Role instance
	 */
	public static Role findRole(String roleName) {
		if (SecRelSystem.roleNames.containsKey(roleName))
			return new Role(SecRelSystem.roleNames.get(roleName));
		throw new IllegalArgumentException("Role with that name does not exist");
	}
	
	/**
	 * Returns whether the system has a role with the specified id.
	 *
	 * @param roleId the id of the role
	 * @return true if there is such a role
	 */
	public static boolean hasRole(Integer roleId) {
		return SecRelSystem.roleIds.containsKey(roleId);
	}
	
	/**
	 * Returns whether the role instance corresponds to a role in the system.
	 *
	 * @param role the role instance
	 * @return true if there is such a role
	 */
	public static boolean hasRole(Role role) {
		return hasRole(role.getId());
	}
	
	/**
	 * Returns whether the system has a role with the specified name.
	 *
	 * @param roleName the name of the role
	 * @return true if there is such a role
	 */
	public static boolean hasRole(String roleName) {
		return SecRelSystem.roleNames.containsKey(roleName);
	}
	
	/**
	 * Removes the specified role from the system.
	 *
	 * @param roleId the id of the role
	 */
	public static void removeRole(Integer roleId) {
		// check if a roleId is null
		if (roleId == null)
		    // throw exception
		    throw new NullPointerException("Role id cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleIds.containsKey(roleId))
			throw new IllegalArgumentException("Role does not exist.");
		// clear role member assignments
		SecRelSystem.clearRoleMembers(roleId);
		// remove role from system
		SecRelSystem.roleNames.remove(SecRelSystem.roleIds.get(roleId));
		SecRelSystem.roleIds.remove(roleId);
	}
	
	/**
	 * Removes the specified role from the system.
	 *
	 * @param role the role instance
	 */
	public static void removeRole(Role role) {
		// check if a role is null
		if (role == null)
		    // throw exception
		    throw new NullPointerException("Role cannot be null.");
		// delegate to id version
		removeRole(role.getId());
	}
	
	/**
	 * Removes the specified role from the system.
	 *
	 * @param roleName the name of the role
	 */
	public static void removeRole(String roleName) {
		// check if a roleName is null
		if (roleName == null)
		    // throw exception
		    throw new NullPointerException("Role name cannot be null.");
		// check if role exists
		if (!SecRelSystem.roleNames.containsKey(roleName))
			throw new IllegalArgumentException("Role does not exist.");
		Integer roleId = SecRelSystem.roleNames.get(roleName);
		// clear role member assignments
		SecRelSystem.clearRoleMembers(roleId);
		// remove role from system
		SecRelSystem.roleIds.remove(roleId);
		SecRelSystem.roleNames.remove(roleName);
	}
	
	/**
	 * Prevents instantiation of the Roles class.
	 */
	private Roles() {
	}

}