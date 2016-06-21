/**
 * This file defines the Service type. The SecRel pattern defines the Service
 * type as a class but it is defined as an interface here to more easily
 * facilitate differing implementations. The Services class is also defined in
 * this file. The Services class is not included in the SecRel Pattern but is
 * included here to facilitate simple creation and retrieval of the secure
 * Service instances.
 */
package edu.fgcu.secrel;

/**
 * This interface represents a reliable service to be protected by the SecRel's
 * role-based security system.
 *
 * @author lngibson
 *
 */
public interface Service {

    /**
     * @author lngibson
     *
     */
    public class ReferenceMonitor {

        /**
         *
         */
        private int id;

        /**
         *
         */
        public void authorizeUser() {
            throw new RuntimeException( "not implemented" );
        }
        
        /**
         * @param user
         */
        public void checkRights( User user ) {
            throw new RuntimeException( "not implemented" );
        }

    }

    /**
     * This class contains the mechanisms for instantiating and retrieving
     * Service instances.
     *
     * @author lngibson
     *
     */
    public static final class Services {

        /**
         * This method returns the Service represented by the specified
         * identifier if one exists.
         *
         * @param id
         * @return
         */
        public Service find( long id ) {
            throw new RuntimeException( "not implemented" );
        }

    }

    /**
     * Returns the unique identifier of this service. The SecRel pattern
     * specifies a private id instance variable. This method seeks to satisfy
     * that requirement as interfaces cannot specify instance variables.
     *
     * @return
     */
    long getId();

    /**
     * Returns the unique human readable identifier of this service. This method
     * is not included in the SecRel pattern.
     *
     * @return
     */
    String getName();

    /**
     * Returns a Rights instance representing the Rights this Service instance
     * contains and the Roles for which those Rights apply.
     *
     * @return
     */
    Rights getRights();

    /**
     * Performs the action specified by this Service instance.
     */
    void invokeService();

    /**
     * Creates a new ReferenceMonitor for this Service.
     *
     * @return
     */
    ReferenceMonitor monitor();

    /**
     * Determines whether the specified User is authorized to invoke this
     * Service.
     *
     */
    void processRequest( User user );

    /*
     * (non-Javadoc) I do not know the purpose of this method.
     *
     * @return
     */
    default int serviceLevel() {
        return -1;
    }

    /**
     * This method sets the identifier of this Service instance. This method is
     * called by Services in order to assign unique identifiers to distinct
     * Service instances. An identifier can only be assigned once. If this
     * method is called twice on an instance, implementations must throw an
     * IllegalStateException. This method is not included in the SecRel pattern.
     *
     * @param id
     */
    void setId( long id );

}