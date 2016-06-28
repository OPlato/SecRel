/**
 * TODO
 */
package edu.fgcu.secrel;

import java.util.Set;

/**
 * @author lngibson
 *
 */
public class User {

    /**
     * TODO
     */
    private final int id;
    
    /**
     * TODO
     *
     * @param id
     */
    public User( int id ) {
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
     * @return
     */
    public String getName() {
        return System.getUserName( id );
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