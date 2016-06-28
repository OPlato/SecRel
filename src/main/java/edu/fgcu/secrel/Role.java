/**
 * TODO
 */
package edu.fgcu.secrel;

import java.util.Set;

/**
 * TODO
 *
 * @author lngibson
 *
 */
public class Role {
    
    /**
     * TODO
     */
    private final int id;

    /**
     * TODO
     *
     * @param id
     */
    public Role( int id ) {
        super();
        this.id = id;
    }
    
    /**
     * TODO
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * TODO
     *
     * @return
     */
    public String getName() {
        return System.getRoleName( id );
    }

    /**
     * TODO
     *
     * @return
     */
    public Set< Role > getRoles() {
        throw new RuntimeException( "not implemented" );
    }

}