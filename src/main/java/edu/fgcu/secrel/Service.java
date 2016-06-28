/**
 * This file defines the Service type. TODO
 */
package edu.fgcu.secrel;

import java.io.InputStream;

/**
 * This interface represents a reliable service to be protected by the SecRel's
 * role-based security system.
 *
 * @author lngibson
 *
 */
public abstract class Service {
    
    /**
     * TODO
     *
     * @author lngibson
     *
     */
    public class ReferenceMonitor {
        
        /**
         * TODO
         */
        @SuppressWarnings( "unused" )
        private int id;
        
        /**
         * TODO
         */
        public void authorizeUser() {
            throw new RuntimeException( "not implemented" );
        }

        /**
         * @param user
         *            TODO
         */
        public void checkRights( User user ) {
            throw new RuntimeException( "not implemented" );
        }
        
    }
    
    /**
     * TODO
     */
    private boolean idSet = false;
    
    /**
     * TODO
     */
    private int id;
    
    /**
     * TODO
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    /**
     * TODO
     *
     * @return TODO
     */
    public abstract String getName();
    
    /**
     * Returns a Rights instance representing the Rights this Service instance
     * contains and the Roles for which those Rights apply.
     *
     * @return TODO
     */
    public Rights getRights() {
        throw new RuntimeException( "not implemented" );
    }
    
    /**
     * Performs the action specified by this Service instance.
     *
     * @param stream
     *            TODO
     */
    public abstract void invokeService( InputStream stream );
    
    /**
     * Creates a new ReferenceMonitor for this Service.
     *
     * @return TODO
     */
    public ReferenceMonitor monitor() {
        throw new RuntimeException( "not implemented" );
    }
    
    /**
     * TODO
     *
     * @param user
     *            TODO
     *
     */
    public abstract void processRequest( User user );
    
    /**
     * TODO
     *
     * @return TODO
     */
    // I do not know the purpose of this method.
    public int serviceLevel() {
        throw new RuntimeException( "not implemented" );
    }
    
    /**
     * TODO
     * @param id TODO
     */
    protected void setId(int id) {
        if( idSet )
            throw new IllegalStateException("Service Id has already been set");
        this.id = id;
    }
    
}