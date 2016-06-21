/**
 *
 */
package edu.fgcu.secrel;


/**
 * @author lngibson
 *
 */
public interface Role {

    /**
     * This class contains the mechanisms for instantiating and retrieving Role
     * instances.
     *
     * @author lngibson
     *
     */
    public static final class Roles {

        /**
         * This method returns the Role represented by the specified identifier
         * if one exists.
         *
         * @param id
         * @return
         */
        public Role find( long id ) {
            throw new RuntimeException( "not implemented" );
        }

    }

    /**
     * Returns the unique identifier of this role. The SecRel pattern specifies
     * a private id instance variable. This method seeks to satisfy that
     * requirement as interfaces cannot specify instance variables.
     *
     * @return
     */
    long getId();

    /**
     * Returns the unique human readable identifier of this role. The SecRel
     * pattern specifies a private id instance variable. This method seeks to
     * satisfy that requirement as interfaces cannot specify instance variables.
     *
     * @return
     */
    String getName();

    /**
     * This method sets the identifier of this Role instance. This method is
     * called by Roles in order to assign unique identifiers to distinct Role
     * instances. An identifier can only be assigned once. If this method is
     * called twice on an instance, implementations must throw an
     * IllegalStateException. This method is not included in the SecRel pattern.
     *
     * @param id
     */
    void setId( long id );

}