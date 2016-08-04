/**
 * This file defines the Right class. The Right class is simply the
 * encapsulation of a role id, a service id and an access type.
 */
// Note: accessType, checkRights
package edu.fgcu.secrel;

/**
 * The Right class is the encapsulation of a role and service id as well as an
 * access type for use by the SecRelSystem to represent role-service
 * authorizations.
 *
 * @author lngibson
 *
 */
public final class Right {
	
	/**
	 * The id of the authorized role.
	 */
	public final int roleId;
	
	/**
	 * The id of the service.
	 */
	public final int serviceId;
	
	/**
	 * I do not know the purpose of this field.
	 */
	private int accessType;
	
	/**
	 * Constructs a Right given the role and service.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 */
	protected Right(int roleId, int serviceId) {
		super();
		this.roleId = roleId;
		this.serviceId = serviceId;
	}
	
	/**
	 * Constructs a Right given the role, service and accesType.
	 *
	 * @param roleId the id of the role
	 * @param serviceId the id of the service
	 * @param accessType the access type
	 */
	protected Right(int roleId, int serviceId, int accessType) {
		super();
		this.roleId = roleId;
		this.serviceId = serviceId;
		this.accessType = accessType;
	}
	
	/**
	 * I do not know the purpose of this method
	 */
	@SuppressWarnings("static-method")
	public void checkRights() {
		throw new RuntimeException("not implemented");
	}
	
	/**
	 * Getter for accessType.
	 * 
	 * @return the accessType
	 */
	public int getAccessType() {
		return accessType;
	}
	
	/**
	 * Returns the Role instance corresponding to the authorized role.
	 *
	 * @return the role
	 */
	public Role getRole() {
		return Roles.findRole(roleId);
	}
	
	/**
	 * Getter for roleId.
	 * 
	 * @return the roleId
	 */
	public int getRoleId() {
		return roleId;
	}
	
	/**
	 * Returns the Service.
	 *
	 * @return the service
	 */
	public Service getService() {
		return Services.findService(serviceId);
	}
	
	/**
	 * Getter for serviceId.
	 *
	 * @return the serviceId
	 */
	public int getServiceId() {
		return serviceId;
	}
	
	/**
	 * Setter accessType.
	 *
	 * @param accessType the accessType to set
	 */
	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}
	
}