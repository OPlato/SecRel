/**
 * This file defines the Service type. TODO
 */
package edu.fgcu.secrel;

import java.io.InputStream;
import java.util.NavigableMap;
import java.util.TreeMap;

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
    public class ReferenceMonitor implements Runnable {

        /**
         * TODO
         */
        public static final int IDLE = 0;

        /**
         * TODO
         */
        public static final int PENDING = -1;

        /**
         * TODO
         */
        public static final int AUTHORIZED = 1;

        /**
         * TODO
         */
        public static final int UNAUTHORIZED = 2;

        /**
         * TODO
         */
        private final int id;

        /**
         * TODO
         */
        private final int userId;

        /**
         * TODO
         */
        private Thread thread;

        /**
         * TODO
         */
        private int state = ReferenceMonitor.IDLE;

        /**
         * TODO
         */
        private boolean isAuthorized = false;

        /**
         * TODO
         *
         * @param id
         *            TODO
         * @param userId
         *            TODO
         */
        private ReferenceMonitor( int id, int userId ) {
            super();
            this.id = id;
            this.userId = userId;
        }

        /**
         * TODO
         */
        public void authorizeUser() {
            isAuthorized = true;
            state = ReferenceMonitor.AUTHORIZED;
            synchronized( this ) {
                notify();
            }
        }

        /**
         * @return TODO
         */
        public boolean checkRights() {
            thread = new Thread( this );
            thread.start();
            state = ReferenceMonitor.PENDING;
            while( state == ReferenceMonitor.PENDING )
                try {
                    synchronized( this ) {
                        wait();
                    }
                } catch( InterruptedException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            return isAuthorized;
        }

        /**
         * TODO
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * TODO
         * @return the state
         */
        public int getState() {
            return state;
        }

        /**
         * TODO
         * @return the thread
         */
        public Thread getThread() {
            return thread;
        }

        /**
         * TODO
         * @return the userId
         */
        public int getUserId() {
            return userId;
        }

        /**
         * TODO
         * @return the isAuthorized
         */
        public boolean isAuthorized() {
            return isAuthorized;
        }

        /**
         * TODO
         *
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            int[] roleIds = System.getRoleIds( userId );
            for( int roleId : roleIds )
                if( System.isAuthorizedFor( roleId, Service.this.id ) )
                    authorizeUser();
            if( state == ReferenceMonitor.PENDING ) {
                state = ReferenceMonitor.UNAUTHORIZED;
                synchronized( this ) {
                    notify();
                }
            }
        }

    }

    /**
     * TODO
     */
    private static final NavigableMap< Integer, ReferenceMonitor > monitors = new TreeMap< >();

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
     *
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
     * @param userId
     *            TODO
     *
     * @return TODO
     */
    public ReferenceMonitor monitor( int userId ) {
        int id = Service.monitors.isEmpty() ? 0 : Service.monitors.lastKey() + 1;
        return new ReferenceMonitor( id, userId );
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
     *
     * @param id
     *            TODO
     */
    protected void setId( int id ) {
        if( idSet )
            throw new IllegalStateException( "Service Id has already been set" );
        this.id = id;
    }

}