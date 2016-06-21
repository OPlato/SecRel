/**
 *
 */
package edu.fgcu.secrel;

/**
 * @author lngibson
 *
 */
public final class Right {
    
    /**
     *
     */
    public final Role role;
    
    /**
     *
     */
    public final Service service;
    
    /**
     *
     */
    private int accessType;
    
    /**
     * @param role
     * @param service
     * @param accessType
     */
    public Right( Role role, Service service ) {
        super();
        this.role = role;
        this.service = service;
    }
    
    /**
     * @param role
     * @param service
     * @param accessType
     */
    public Right( Role role, Service service, int accessType ) {
        super();
        this.role = role;
        this.service = service;
        this.accessType = accessType;
    }
    
    /**
     *
     */
    public void checkRights() {
        throw new RuntimeException( "not implemented" );
    }
    
    /**
     * @return the accessType
     */
    public int getAccessType() {
        return accessType;
    }
    
    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }
    
    /**
     * @return the service
     */
    public Service getService() {
        return service;
    }
    
    /**
     * @param accessType
     *            the accessType to set
     */
    public void setAccessType( int accessType ) {
        this.accessType = accessType;
    }
    
}