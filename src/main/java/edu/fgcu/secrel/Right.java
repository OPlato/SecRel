/**
 * TODO
 */
package edu.fgcu.secrel;

/**
 * TODO
 * 
 * @author lngibson
 *        
 */
public final class Right {

    /**
     * TODO
     */
    public final int roleId;

    /**
     * TODO
     */
    public final int serviceId;

    /**
     * TODO
     */
    private int accessType;

    /**
     * TODO
     * 
     * @param roleId
     *            TODO
     * @param serviceId
     *            TODO
     */
    protected Right( int roleId, int serviceId ) {
        super();
        this.roleId = roleId;
        this.serviceId = serviceId;
    }

    /**
     * TODO
     * 
     * @param roleId
     *            TODO
     * @param serviceId
     *            TODO
     * @param accessType
     *            TODO
     */
    protected Right( int roleId, int serviceId, int accessType ) {
        super();
        this.roleId = roleId;
        this.serviceId = serviceId;
        this.accessType = accessType;
    }

    /**
     * TODO
     */
    public void checkRights() {
        throw new RuntimeException( "not implemented" );
    }

    /**
     * TODO
     * 
     * @return the accessType
     */
    public int getAccessType() {
        return accessType;
    }

    /**
     *  TODO
     * @return the role
     */
    public Role getRole() {
        return System.findRole(roleId);
    }

    /**
     * TODO
     * 
     * @return the role
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     *  TODO
     * @return the service
     */
    public Service getService() {
        return System.findService(serviceId);
    }

    /**
     *  TODO
     * @return the service
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * TODO
     * 
     * @param accessType
     *            the accessType to set
     */
    public void setAccessType( int accessType ) {
        this.accessType = accessType;
    }

}