/**
 *
 */
package edu.fgcu.secrel;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lngibson
 *
 */
public final class Rights implements RightsRef {
    
    /**
     *
     */
    private RightsRef parent;
    
    /**
     *
     */
    private Set< Role > roles;
    
    /**
     *
     */
    private Set< Service > services;
    
    /**
     * @param parent
     * @param roles
     */
    Rights( RightsRef parent, Role[] roles ) {
        this( parent, roles, null );
    }
    
    /**
     * @param parent
     * @param roles
     * @param services
     */
    Rights( RightsRef parent, Role[] roles, Service[] services ) {
        super();
        this.parent = parent;
        if( roles == null )
            this.roles = null;
        else {
            this.roles = new HashSet< >();
            for( Role role : roles )
                this.roles.add( role );
        }
        if( services == null )
            this.services = null;
        else {
            this.services = new HashSet< >();
            for( Service service : services )
                this.services.add( service );
        }
    }
    
    /**
     * @param parent
     * @param services
     */
    Rights( RightsRef parent, Service[] services ) {
        this( parent, null, services );
    }
    
    @Override
    public void authorize( Role role, Service service ) {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Role > getRoles() {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Role > getRolesFor( long serviceId ) {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Role > getRolesFor( Service service ) {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Service > getServices() {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Service > getServicesFor( long roleId ) {
        throw new RuntimeException( "not implemented" );
    }

    @Override
    public Set< Service > getServicesFor( Role role ) {
        throw new RuntimeException( "not implemented" );
    }
    
}

/**
 * @author lngibson
 *
 */
interface RightsRef {
    
    void authorize( Role role, Service service );
    
    /**
     * @return
     */
    Set< Role > getRoles();
    
    /**
     * @param serviceId
     * @return
     */
    Set< Role > getRolesFor( long serviceId );
    
    /**
     * @param service
     * @return
     */
    Set< Role > getRolesFor( Service service );
    
    /**
     * @return
     */
    Set< Service > getServices();
    
    /**
     * @param roleId
     * @return
     */
    Set< Service > getServicesFor( long roleId );
    
    /**
     * @param role
     * @return
     */
    Set< Service > getServicesFor( Role role );
    
}

final class RightsTable implements RightsRef {
    
    /* (non-Javadoc)
     * @see edu.fgcu.secrel.RightsRef#authorize(edu.fgcu.secrel.Role, edu.fgcu.secrel.Service)
     */
    @Override
    public void authorize( Role role, Service service ) {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Role > getRoles() {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Role > getRolesFor( long serviceId ) {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Role > getRolesFor( Service service ) {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Service > getServices() {
        throw new RuntimeException( "not implemented" );
    }
    
    @Override
    public Set< Service > getServicesFor( long roleId ) {
        throw new RuntimeException( "not implemented" );
    }

    @Override
    public Set< Service > getServicesFor( Role role ) {
        throw new RuntimeException( "not implemented" );
    }
    
}