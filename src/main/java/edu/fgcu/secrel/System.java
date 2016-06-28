/**
 * TODO
 */
package edu.fgcu.secrel;

import java.io.InputStream;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * TODO
 *
 * @author lngibson
 *
 */
public class System {

    /**
     * TODO
     */
    private static final NavigableMap< Integer, String > userIds = new TreeMap< >();

    /**
     * TODO
     */
    private static final NavigableMap< Integer, String > roleIds = new TreeMap< >();

    /**
     * TODO
     */
    private static final NavigableMap< String, Integer > userNames = new TreeMap< >();

    /**
     * TODO
     */
    private static final NavigableMap< String, Integer > roleNames = new TreeMap< >();

    /**
     * TODO
     */
    private static final NavigableSet< Long > memberForwardMap = new TreeSet< >();

    /**
     * TODO
     */
    private static final NavigableSet< Long > memberBackwardMap = new TreeSet< >();

    /**
     * TODO
     */
    private static final NavigableMap< Integer, Service > serviceIds = new TreeMap< >();

    /**
     * TODO
     */
    private static final NavigableMap< String, Service > serviceNames = new TreeMap< >();

    /**
     * TODO
     */
    private static final NavigableMap< Long, Right > serviceForwardMap = new TreeMap< >();

    /**
     * TODO
     */
    private static final NavigableMap< Long, Right > serviceBackwardMap = new TreeMap< >();

    /**
     * TODO
     *
     * @param userId
     *            the id of the user
     * @param roleId
     *            the id of the role
     * @return true if successful, false otherwise
     */
    public static boolean assignRole( Integer userId, Integer roleId ) {
        // check if a userId is null
        if( userId == null )
            // throw exception
            throw new NullPointerException( "User id cannot be null." );
        if( !System.userIds.containsKey( userId ) )
            throw new IllegalArgumentException( "User with that id does not exist." );
        // check if a roleId is null
        if( roleId == null )
            // throw exception
            throw new NullPointerException( "Role id cannot be null." );
        if( !System.roleIds.containsKey( roleId ) )
            throw new IllegalArgumentException( "Role with that id does not exist." );
        System.memberForwardMap.add( userId.longValue() << 32 | roleId );
        System.memberBackwardMap.add( roleId.longValue() << 32 | userId );
        return true;
    }

    /**
     * TODO
     *
     * @param userName
     *            the name of the user
     * @param roleName
     *            the name of the role
     * @return true if successful, false otherwise
     */
    public static boolean assignRole( String userName, String roleName ) {
        // check if a userName is null
        if( userName == null )
            // throw exception
            throw new NullPointerException( "User name cannot be null." );
        if( !System.userNames.containsKey( userName ) )
            throw new IllegalArgumentException( "User with that name does not exist." );
        // check if a roleName is null
        if( roleName == null )
            // throw exception
            throw new NullPointerException( "Role name cannot be null." );
        if( !System.roleNames.containsKey( roleName ) )
            throw new IllegalArgumentException( "Role with that name does not exist." );
        System.memberForwardMap
        .add( System.userNames.get( userName ).longValue() << 32 | System.roleNames.get( roleName ) );
        System.memberBackwardMap
        .add( System.roleNames.get( roleName ).longValue() << 32 | System.userNames.get( userName ) );
        return true;
    }

    /**
     * TODO
     *
     * @param user
     *            the User instance
     * @param role
     *            the Role instance
     * @return true if successful, false otherwise
     */
    public static boolean assignRole( User user, Role role ) {
        // check if a user is null
        if( user == null )
            // throw exception
            throw new NullPointerException( "User id cannot be null." );
        if( !System.userIds.containsKey( user.getId() ) )
            throw new IllegalArgumentException( "User does not exist." );
        // check if a role is null
        if( role == null )
            // throw exception
            throw new NullPointerException( "Role id cannot be null." );
        if( !System.roleIds.containsKey( role.getId() ) )
            throw new IllegalArgumentException( "Role does not exist." );
        System.memberForwardMap.add( ( long ) user.getId() << 32 | role.getId() );
        System.memberBackwardMap.add( ( long ) role.getId() << 32 | user.getId() );
        return true;
    }

    /**
     * TODO
     *
     * @param roleId
     *            the id of the role
     * @param serviceId
     *            the id of the service
     * @param accessType
     *            TODO
     * @return true if successful, false otherwise
     */
    public static boolean authorizeRole( Integer roleId, Integer serviceId, int accessType ) {
        // check if a roleId is null
        if( roleId == null )
            // throw exception
            throw new NullPointerException( "Role id cannot be null." );
        if( !System.roleIds.containsKey( roleId ) )
            throw new IllegalArgumentException( "Role with that id does not exist." );
        // check if a serviceId is null
        if( serviceId == null )
            // throw exception
            throw new NullPointerException( "Service id cannot be null." );
        if( !System.serviceIds.containsKey( serviceId ) )
            throw new IllegalArgumentException( "Service with that id does not exist." );
        Right right = new Right( roleId, serviceId, accessType );
        System.serviceForwardMap.put( roleId.longValue() << 32 | serviceId, right );
        System.serviceBackwardMap.put( serviceId.longValue() << 32 | roleId, right );
        return true;
    }

    /**
     * TODO
     *
     * @param role
     *            the Role instance
     * @param service
     *            the Service instance
     * @param accessType
     *            TODO
     * @return true if successful, false otherwise
     */
    public static boolean authorizeRole( Role role, Service service, int accessType ) {
        // check if a role is null
        if( role == null )
            // throw exception
            throw new NullPointerException( "Role id cannot be null." );
        if( !System.roleIds.containsKey( role.getId() ) )
            throw new IllegalArgumentException( "Role does not exist." );
        // check if a service is null
        if( service == null )
            // throw exception
            throw new NullPointerException( "Service id cannot be null." );
        if( !System.serviceIds.containsKey( service.getId() ) )
            throw new IllegalArgumentException( "Service does not exist." );
        Right right = new Right( role.getId(), service.getId(), accessType );
        System.serviceForwardMap.put( ( long ) role.getId() << 32 | service.getId(), right );
        System.serviceBackwardMap.put( ( long ) service.getId() << 32 | role.getId(), right );
        return true;
    }

    /**
     * TODO
     *
     * @param roleName
     *            the name of the role
     * @param serviceName
     *            the name of the service
     * @param accessType
     *            TODO
     * @return true if successful, false otherwise
     */
    public static boolean authorizeRole( String roleName, String serviceName, int accessType ) {
        // check if a roleName is null
        if( roleName == null )
            // throw exception
            throw new NullPointerException( "Role name cannot be null." );
        if( !System.roleNames.containsKey( roleName ) )
            throw new IllegalArgumentException( "Role with that name does not exist." );
        // check if a serviceName is null
        if( serviceName == null )
            // throw exception
            throw new NullPointerException( "Service name cannot be null." );
        if( !System.serviceNames.containsKey( serviceName ) )
            throw new IllegalArgumentException( "Service with that name does not exist." );
        Service service = System.serviceNames.get( serviceName );
        Right right = new Right( System.roleNames.get( roleName ), service.getId(), accessType );
        System.serviceForwardMap.put( System.roleNames.get( roleName ).longValue() << 32 | service.getId(), right );
        System.serviceBackwardMap.put( ( long ) service.getId() << 32 | System.roleNames.get( roleName ), right );
        return true;
    }

    /**
     * TODO
     *
     * @param name
     *            the name of the new role
     * @return a Role instance representing the new role
     */
    public static Role createRole( String name ) {
        // check if a name is null
        if( name == null )
            // throw exception
            throw new NullPointerException( "Role name cannot be null." );
        // check if a role with that name already exists
        if( System.roleNames.containsKey( name ) )
            // throw exception
            throw new IllegalArgumentException(
                    String.format( "A Role with the name \"%s\" already exists. Role(%d,\"%s\")", name,
                            System.roleNames.get( name ), name ) );
        // compute new roleId
        Integer id = System.roleIds.isEmpty() ? 0 : System.roleIds.lastKey() + 1;
        System.roleIds.put( id, name );
        System.roleNames.put( name, id );
        return new Role( id );
    }

    /**
     * TODO
     *
     * @param name
     *            the name of the new user
     * @return a User instance representing the new user
     */
    public static User createUser( String name ) {
        // check if a name is null
        if( name == null )
            // throw exception
            throw new NullPointerException( "User name cannot be null." );
        // check if a user with that name already exists
        if( System.userNames.containsKey( name ) )
            // throw exception
            throw new IllegalArgumentException(
                    String.format( "A User with the name \"%s\" already exists. User(%d,\"%s\")", name,
                            System.userNames.get( name ), name ) );
        // compute new userId
        Integer id = System.userIds.isEmpty() ? 0 : System.userIds.lastKey() + 1;
        System.userIds.put( id, name );
        System.userNames.put( name, id );
        return new User( id );
    }

    /**
     * TODO
     * 
     * @param roleId
     *            TODO
     * @return TODO
     */
    public static Role findRole(Integer roleId) {
        // TODO
        throw new RuntimeException("not implemented");
    }

    /**
     * TODO
     * @param roleName TODO
     * @return TODO
     */
    public static Role findRole(String roleName) {
        // TODO
        throw new RuntimeException("not implemented");
    }

    /**
     * TODO
     * 
     * @param serviceId
     *            TODO
     * @return TODO
     */
    public static Service findService(Integer serviceId) {
        // TODO
        throw new RuntimeException("not implemented");
    }

    /**
     * TODO
     * @param serviceName TODO
     * @return TODO
     */
    public static Service findService(String serviceName) {
        // TODO
        throw new RuntimeException("not implemented");
    }

    /**
     * TODO
     * 
     * @param userId
     *            TODO
     * @return TODO
     */
    public static User findUser(Integer userId) {
        // TODO
        throw new RuntimeException("not implemented");
    }

    /**
     * TODO
     * @param userName TODO
     * @return TODO
     */
    public static User findUser(String userName) {
        // TODO
        throw new RuntimeException("not implemented");
    }

    /**
     * TODO
     *
     * @param roleId
     *            TODO
     * @return TODO
     */
    protected static int[] getMembersIds( Integer roleId ) {
        Set< Long > entries = System.memberBackwardMap.subSet( roleId.longValue() << 32, roleId.longValue() + 1 << 32 );
        int[] ids = new int[entries.size()];
        int i = 0;
        for( Iterator< Long > it = entries.iterator(); it.hasNext(); i++ )
            ids[i] = ( int ) ( it.next() & 0xffffffffl );
        return ids;
    }

    /**
     * TODO
     *
     * @param userId
     *            TODO
     * @return TODO
     */
    protected static int[] getRoleIds( Integer userId ) {
        Set< Long > entries = System.memberForwardMap.subSet( userId.longValue() << 32, userId.longValue() + 1 << 32 );
        int[] ids = new int[entries.size()];
        int i = 0;
        for( Iterator< Long > it = entries.iterator(); it.hasNext(); i++ )
            ids[i] = ( int ) ( it.next() & 0xffffffffl );
        return ids;
    }

    /**
     * TODO
     *
     * @param id TODO
     * @return TODO
     */
    protected static String getRoleName( Integer id ) {
        return System.roleIds.get( id );
    }

    /**
     * TODO
     *
     * @param id TODO
     * @return TODO
     */
    protected static String getUserName( Integer id ) {
        return System.userIds.get( id );
    }

    /**
     * TODO
     *
     * @param roleId TODO
     * @return TODO
     */
    public static boolean hasRole( Integer roleId ) {
        return System.roleIds.containsKey( roleId );
    }

    /**
     * TODO
     *
     * @param role TODO
     * @return TODO
     */
    public static boolean hasRole( Role role ) {
        return System.hasRole( role.getId() );
    }

    /**
     * TODO
     *
     * @param roleName TODO
     * @return TODO
     */
    public static boolean hasRole( String roleName ) {
        return System.roleNames.containsKey( roleName );
    }

    /**
     * TODO
     *
     * @param serviceId TODO
     * @return TODO
     */
    public static boolean hasService( Integer serviceId ) {
        return System.serviceIds.containsKey( serviceId );
    }

    /**
     * TODO
     *
     * @param service TODO
     * @return TODO
     */
    public static boolean hasService( Service service ) {
        return System.serviceIds.containsValue( service );
    }

    /**
     * TODO
     *
     * @param serviceName TODO
     * @return TODO
     */
    public static boolean hasService( String serviceName ) {
        return System.serviceNames.containsKey( serviceName );
    }

    /**
     * TODO
     *
     * @param userId
     *            TODO
     * @return TODO
     */
    public static boolean hasUser( Integer userId ) {
        return System.userIds.containsKey( userId );
    }

    /**
     * TODO
     *
     * @param userName
     *            TODO
     * @return TODO
     */
    public static boolean hasUser( String userName ) {
        return System.userNames.containsKey( userName );
    }

    /**
     * TODO
     *
     * @param user TODO
     * @return TODO
     */
    public static boolean hasUser( User user ) {
        return System.hasUser( user.getId() );
    }

    /**
     * TODO
     *
     * @param userId TODO
     * @param roleId TODO
     * @return TODO
     */
    public static boolean isMemberOf( Integer userId, Integer roleId ) {
        // check if a userId is null
        if( userId == null )
            // throw exception
            throw new NullPointerException( "User id cannot be null." );
        if( !System.userIds.containsKey( userId ) )
            throw new IllegalArgumentException( "User with that id does not exist." );
        // check if a roleId is null
        if( roleId == null )
            // throw exception
            throw new NullPointerException( "Role id cannot be null." );
        if( !System.roleIds.containsKey( roleId ) )
            throw new IllegalArgumentException( "Role with that id does not exist." );
        return System.memberForwardMap.contains( userId.longValue() << 32 | roleId );
    }

    /**
     * TODO
     *
     * @param userName TODO
     * @param roleName TODO
     * @return TODO
     */
    public static boolean isMemberOf( String userName, String roleName ) {
        // check if a userName is null
        if( userName == null )
            // throw exception
            throw new NullPointerException( "User name cannot be null." );
        if( !System.userNames.containsKey( userName ) )
            throw new IllegalArgumentException( "User with that name does not exist." );
        // check if a roleName is null
        if( roleName == null )
            // throw exception
            throw new NullPointerException( "Role name cannot be null." );
        if( !System.roleNames.containsKey( roleName ) )
            throw new IllegalArgumentException( "Role with that name does not exist." );
        return System.memberForwardMap
                .contains( System.userNames.get( userName ).longValue() << 32 | System.roleNames.get( roleName ) );
    }

    /**
     * TODO
     *
     * @param user TODO
     * @param role TODO
     * @return TODO
     */
    public static boolean isMemberOf( User user, Role role ) {
        // check if a user is null
        if( user == null )
            // throw exception
            throw new NullPointerException( "User id cannot be null." );
        if( !System.userIds.containsKey( user.getId() ) )
            throw new IllegalArgumentException( "User does not exist." );
        // check if a role is null
        if( role == null )
            // throw exception
            throw new NullPointerException( "Role id cannot be null." );
        if( !System.roleIds.containsKey( role.getId() ) )
            throw new IllegalArgumentException( "Role does not exist." );
        return System.memberForwardMap.contains( ( long ) user.getId() << 32 | role.getId() );
    }

    /**
     * TODO
     *
     * @param userId TODO
     * @param serviceId TODO
     * @return TODO
     */
    public static boolean makeRequest( Integer userId, Integer serviceId ) {
        /* TODO */ throw new RuntimeException( "not implemented" );
    }

    /**
     * TODO
     *
     * @param userId TODO
     * @param serviceId TODO
     * @param data TODO
     * @return TODO
     */
    public static boolean makeRequest( Integer userId, Integer serviceId, InputStream data ) {
        // check if a userId is null
        if( userId == null )
            // throw exception
            throw new NullPointerException( "User id cannot be null." );
        if( !System.userIds.containsKey( userId ) )
            throw new IllegalArgumentException( "User with that id does not exist." );
        int[] roleIds = System.getRoleIds(userId);
        for( @SuppressWarnings( "unused" ) int roleId : roleIds )
            /* TODO */ throw new RuntimeException( "not implemented" );
        return false;
    }

    /**
     * TODO
     *
     * @param userId TODO
     * @param serviceId TODO
     * @param data TODO
     * @return TODO
     */
    public static boolean makeRequest( Integer userId, Integer serviceId, String data ) {
        /* TODO */ throw new RuntimeException( "not implemented" );
    }

    /**
     * TODO
     *
     * @param service TODO
     * @return TODO
     */
    public static boolean registerService( ReliableHardwareService service ) {
        /* TODO */ throw new RuntimeException( "not implemented" );
    }

    /**
     * TODO
     *
     * @param service TODO
     * @return TODO
     */
    public static boolean registerService( ReliableSoftwareService service ) {
        /* TODO */ throw new RuntimeException( "not implemented" );
    }

    /**
     * TODO
     * @param service TODO
     * @return TODO
     */
    public static boolean registerService( Service service ) {
        // check if a service is null
        if( service == null )
            // throw exception
            throw new NullPointerException( "Service cannot be null." );
        // check if service is already registered
        if( System.serviceIds.containsValue( service ))
            // exit method
            return false;
        // check if a service with that name already exists
        if( System.serviceNames.containsKey( service.getName() ) )
            // throw exception
            throw new IllegalArgumentException(
                    String.format( "A Service with the name \"%s\" already exists", service.getName() ) );
        // compute new serviceId
        Integer id = System.serviceIds.isEmpty() ? 0 : System.serviceIds.lastKey() + 1;
        try {
            service.setId(id);
        } catch( IllegalStateException e ) {
            id = service.getId();
        }
        System.serviceIds.put( id, service );
        System.serviceNames.put( service.getName(), service );
        return true;
    }

    /**
     * TODO
     *
     * @param roleId
     *            TODO
     */
    public static void removeRole( Integer roleId ) {
        // check if a roleId is null
        if( roleId == null )
            // throw exception
            throw new NullPointerException( "Role id cannot be null." );
        if( !System.roleIds.containsKey( roleId ) )
            throw new IllegalArgumentException( "Role does not exist." );
        System.roleNames.remove( System.roleIds.get( roleId ) );
        System.roleIds.remove( roleId );
    }

    /**
     * TODO
     *
     * @param role
     *            TODO
     */
    public static void removeRole( Role role ) {
        // check if a role is null
        if( role == null )
            // throw exception
            throw new NullPointerException( "Role cannot be null." );
        System.removeRole( role.getId() );
    }

    /**
     * TODO
     *
     * @param roleName
     *            TODO
     */
    public static void removeRole( String roleName ) {
        // check if a roleName is null
        if( roleName == null )
            // throw exception
            throw new NullPointerException( "Role name cannot be null." );
        if( !System.roleNames.containsKey( roleName ) )
            throw new IllegalArgumentException( "Role does not exist." );
        System.roleIds.remove( System.roleNames.get( roleName ) );
        System.roleNames.remove( roleName );
    }

    /**
     * TODO
     *
     * @param serviceId
     *            TODO
     */
    public static void removeService( Integer serviceId ) {
        // check if a serviceId is null
        if( serviceId == null )
            // throw exception
            throw new NullPointerException( "Service id cannot be null." );
        if( !System.serviceIds.containsKey( serviceId ) )
            throw new IllegalArgumentException( "Service does not exist." );
        Service service = System.serviceIds.get( serviceId );
        System.serviceNames.remove( service.getName() );
        System.serviceIds.remove( serviceId );
    }

    /**
     * TODO
     *
     * @param service
     *            TODO
     */
    public static void removeService( Service service ) {
        // check if a service is null
        if( service == null )
            // throw exception
            throw new NullPointerException( "Service cannot be null." );
        System.removeService( service.getId() );
        // check if service is registered
        if( !System.serviceNames.containsValue( service ) )
            throw new IllegalArgumentException( "Service is not registered." );
        System.serviceIds.remove( service.getId() );
        System.serviceNames.remove( service.getName() );
    }

    /**
     * TODO
     *
     * @param serviceName
     *            TODO
     */
    public static void removeService( String serviceName ) {
        // check if a serviceName is null
        if( serviceName == null )
            // throw exception
            throw new NullPointerException( "Service name cannot be null." );
        if( !System.serviceNames.containsKey( serviceName ) )
            throw new IllegalArgumentException( "Service does not exist." );
        Service service = System.serviceNames.get( serviceName );
        System.serviceIds.remove( service.getId() );
        System.serviceNames.remove( serviceName );
    }

    /**
     * TODO
     *
     * @param userId
     *            TODO
     */
    public static void removeUser( Integer userId ) {
        // check if a userId is null
        if( userId == null )
            // throw exception
            throw new NullPointerException( "User id cannot be null." );
        if( !System.userIds.containsKey( userId ) )
            throw new IllegalArgumentException( "User does not exist." );
        System.userNames.remove( System.userIds.get( userId ) );
        System.userIds.remove( userId );
    }

    /**
     * TODO
     *
     * @param userName
     *            TODO
     */
    public static void removeUser( String userName ) {
        // check if a userName is null
        if( userName == null )
            // throw exception
            throw new NullPointerException( "User name cannot be null." );
        if( !System.userNames.containsKey( userName ) )
            throw new IllegalArgumentException( "User does not exist." );
        System.userIds.remove( System.userNames.get( userName ) );
        System.userNames.remove( userName );
    }

    /**
     * TODO
     *
     * @param user
     *            TODO
     */
    public static void removeUser( User user ) {
        // check if a user is null
        if( user == null )
            // throw exception
            throw new NullPointerException( "User cannot be null." );
        System.removeUser( user.getId() );
    }

    /**
     * TODO
     */
    private System() {
    }

}