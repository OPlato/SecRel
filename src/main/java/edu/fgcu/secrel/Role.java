/**
 * TODO
 */
package edu.fgcu.secrel;

import java.util.Set;

/**
 * The Role class provides a means of querying the System about a specific role.
 * Role instances do not contain any information other than the ID of the
 * corresponding role in the System. The ID is used as a key to make queries to
 * the System.
 *
 * @author lngibson
 *        
 */
public class Role implements Entity, Comparable<Role> {
	
	/**
	 * The id of this Role.
	 */
	private final int id;
	
	/**
	 * Constructs a Role with the specified ID.
	 *
	 * @param id the ID of this Role
	 */
	public Role(int id) {
		super();
		this.id = id;
	}
	
	@Override
	public int compareTo(Role r) {
		return id - r.id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Role)
			return ((Role) o).id == id;
		return false;
	}
	
	/**
	 * Returns the ID of this Role.
	 *
	 * @return the id
	 */
	@Override
	public Integer getId() {
		return id;
	}
	
	/**
	 * Returns the name of this Role.
	 *
	 * @return the name of this Role
	 */
	@Override
	public String getName() {
		return SecRelSystem.getRoleName(id);
	}
	
	/**
	 * Returns the services this Role is authorized to invoke.
	 *
	 * @return the services this Role is authorized to invoke
	 */
	public Set<Service> getServices() {
		throw new RuntimeException("not implemented");
	}
	
	/**
	 * Returns the users assigned to this Role.
	 *
	 * @return the users assigned to this Role
	 */
	public Set<User> getUsers() {
		throw new RuntimeException("not implemented");
	}
	
	@Override
	public String toString() {
		return String.format("%s:Role", getName());
	}
	
}