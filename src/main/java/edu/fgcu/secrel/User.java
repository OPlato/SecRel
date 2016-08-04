/**
 * This file defines the User class. The User class is an encapsulation of a
 * user id along with delegate functions to SecRel User and Member functions.
 */
package edu.fgcu.secrel;

import java.util.Set;

/**
 * The User class provides a means of querying the System about a specific user.
 * User instances do not contain any information other than the ID of the
 * corresponding user in the System. The ID is used as a key to make queries to
 * the System.
 *
 * @author lngibson
 *
 */
public class User implements Entity, Comparable<User> {
	
	/**
	 * The id of this User.
	 */
	private final int id;
	
	/**
	 * Constructs a User with the specified ID.
	 *
	 * @param id the ID of this User
	 */
	public User(int id) {
		super();
		this.id = id;
	}
	
	@Override
	public int compareTo(User u) {
		return id - u.id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof User)
			return ((User) o).id == id;
		return false;
	}
	
	/**
	 * Returns the ID of this User.
	 *
	 * @return the id
	 */
	@Override
	public Integer getId() {
		return id;
	}
	
	/**
	 * Returns the name of this User.
	 *
	 * @return the name of this User
	 */
	@Override
	public String getName() {
		return SecRelSystem.getUserName(id);
	}
	
	/**
	 * Returns the roles to which this User is assigned.
	 *
	 * @return the roles to which this User is assigned
	 */
	public Set<Role> getRoles() {
		throw new RuntimeException("not implemented");
	}
	
	@Override
	public String toString() {
		return String.format("%s:User", getName());
	}
	
}