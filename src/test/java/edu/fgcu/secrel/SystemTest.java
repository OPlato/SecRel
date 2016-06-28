/**
 * TODO
 */
package edu.fgcu.secrel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO
 *
 * @author lngibson
 *
 */
public class SystemTest {
    
    /**
     * TODO
     */
    private static Role Administrator;
    
    /*
     * TODO
     */
    // private static Role Teacher;
    
    /*
     * TODO
     */
    // private static Role Assistant;
    
    /**
     * TODO
     */
    private static Role CoolGuy;
    
    /**
     * TODO
     */
    private static User administrator;
    
    /*
     * TODO
     */
    // private static User ibuckley;
    
    /**
     * TODO
     */
    private static User lngibson;
    
    /**
     * A role meant to be removed in the remove role tests.
     */
    private static Role FakeRole;
    
    /**
     * A user meant to be removed in the remove user tests.
     */
    private static User fakeUser;
    
    /**
     * TODO
     *
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        SystemTest.Administrator = System.createRole( "administrator" );
        // SystemTest.Teacher =
        System.createRole( "teacher" );
        // SystemTest.Assistant =
        System.createRole( "assistant" );
        SystemTest.CoolGuy = System.createRole( "coolguy" );
        SystemTest.administrator = System.createUser( "administrator" );
        // SystemTest.ibuckley =
        System.createUser( "ibuckley" );
        SystemTest.lngibson = System.createUser( "lngibson" );
        System.assignRole( "administrator", "administrator" );
        System.assignRole( "administrator", "teacher" );
        System.assignRole( "administrator", "assistant" );
    }
    
    /**
     * TODO
     *
     */
    @AfterClass
    public static void tearDownAfterClass() {
        // not implemented
    }
    
    /**
     * TODO
     *
     */
    @Before
    public void setUp() {
        SystemTest.FakeRole = System.createRole( "fake_role" );
        SystemTest.fakeUser = System.createUser( "fake_user" );
    }
    
    /**
     * TODO
     *
     */
    @After
    public void tearDown() {
        if( System.hasRole( SystemTest.FakeRole ) )
            System.removeRole( SystemTest.FakeRole );
        if( System.hasUser( SystemTest.fakeUser ) )
            System.removeUser( SystemTest.fakeUser );
    }
    
    /**
     * TODO
     */
    @Test
    public void testAssignRole() {
        System.assignRole( SystemTest.lngibson, SystemTest.CoolGuy );
        Assert.assertTrue( System.isMemberOf( SystemTest.lngibson, SystemTest.CoolGuy ) );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalStateException.class )
    public void testAssignRoleAlreadyAssigned() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test
    public void testAssignRoleById() {
        System.assignRole( SystemTest.lngibson.getId(), SystemTest.CoolGuy.getId() );
        Assert.assertTrue( System.isMemberOf( SystemTest.lngibson, SystemTest.CoolGuy ) );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalStateException.class )
    public void testAssignRoleByIdAlreadyAssigned() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testAssignRoleByIdNonExistantRole() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testAssignRoleByIdNonExistantUser() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testAssignRoleByIdNullRoleId() {
        System.assignRole( SystemTest.lngibson.getId(), null );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testAssignRoleByIdNullUserId() {
        System.assignRole( null, SystemTest.CoolGuy.getId() );
    }
    
    /**
     * TODO
     */
    @Test
    public void testAssignRoleByName() {
        System.assignRole( "lngibson", "coolguy" );
        Assert.assertTrue( System.isMemberOf( SystemTest.lngibson, SystemTest.CoolGuy ) );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalStateException.class )
    public void testAssignRoleByNameAlreadyAssigned() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testAssignRoleByNameNonExistantRole() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testAssignRoleByNameNonExistantUser() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testAssignRoleByNameNullRoleName() {
        System.assignRole( "lngibson", null );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testAssignRoleByNameNullUserName() {
        System.assignRole( null, "coolguy" );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testAssignRoleNonExistantRole() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testAssignRoleNonExistantUser() {
        /* TODO */ Assert.fail( "Not yet implemented" );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testAssignRoleNullRole() {
        System.assignRole( SystemTest.lngibson, null );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testAssignRoleNullUser() {
        System.assignRole( null, SystemTest.CoolGuy );
    }
    
    /**
     * TODO
     */
    @Test
    public void testCreateRole() {
        Role r = System.createRole( "philosopher" );
        Assert.assertNotNull( r );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testCreateRoleDuplicateName() {
        System.createRole( "teacher" );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testCreateRoleNullName() {
        System.createRole( null );
    }
    
    /**
     * TODO
     */
    @Test
    public void testCreateUser() {
        User u = System.createUser( "plato" );
        Assert.assertNotNull( u );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testCreateUserDuplicateName() {
        System.createUser( "lngibson" );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testCreateUserNullName() {
        System.createUser( null );
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasRole() {
        Assert.assertTrue(System.hasRole( SystemTest.Administrator ));
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasRoleById() {
        Assert.assertTrue(System.hasRole( SystemTest.Administrator.getId() ));
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasRoleByIdNonExistantRole() {
        Assert.assertFalse( System.hasRole( -1 ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasRoleByName() {
        Assert.assertTrue( System.hasRole( "administrator" ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasRoleByNameNonExistantRole() {
        Assert.assertFalse( System.hasRole( "nonexistant role" ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasRoleNonExistantRole() {
        Role r = System.createRole( "nonexistant role" );
        System.removeRole( r );
        Assert.assertFalse( System.hasRole( r ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasUser() {
        Assert.assertTrue(System.hasUser( SystemTest.administrator ));
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasUserById() {
        Assert.assertTrue( System.hasUser( SystemTest.administrator.getId() ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasUserByIdNonExistantUser() {
        Assert.assertFalse( System.hasUser( -1 ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasUserByName() {
        Assert.assertTrue( System.hasUser( "administrator" ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testHasUserByNameNonExistantUser() {
        Assert.assertFalse( System.hasUser( "nonexistant user" ) );
    }

    /**
     * TODO
     */
    @Test
    public void testHasUserNonExistantUser() {
        User u = System.createUser( "nonexistant user" );
        System.removeUser( u );
        Assert.assertFalse( System.hasUser( u ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testRemoveRole() {
        System.removeRole( SystemTest.FakeRole );
        Assert.assertFalse( System.hasRole( SystemTest.FakeRole ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testRemoveRoleById() {
        System.removeRole( SystemTest.FakeRole.getId() );
        Assert.assertFalse( System.hasRole( SystemTest.FakeRole ) );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testRemoveRoleByIdNonExistantRole() {
        System.removeRole( -1 );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testRemoveRoleByIdNullId() {
        System.removeRole( ( Integer ) null );
    }
    
    /**
     * TODO
     */
    @Test
    public void testRemoveRoleByName() {
        System.removeRole( "fake_role" );
        Assert.assertFalse( System.hasRole( SystemTest.FakeRole ) );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testRemoveRoleByNameNonExistantRole() {
        System.removeRole( "nonexistant role" );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testRemoveRoleByNameNullName() {
        System.removeRole( ( String ) null );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testRemoveRoleNonExistantRole() {
        Role r = System.createRole( "nonexistant role" );
        System.removeRole( r );
        System.removeRole( r );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testRemoveRoleNullRole() {
        System.removeRole( ( Role ) null );
    }
    
    /**
     * TODO
     */
    @Test
    public void testRemoveUser() {
        System.removeUser( SystemTest.fakeUser );
        Assert.assertFalse( System.hasUser( SystemTest.fakeUser ) );
    }
    
    /**
     * TODO
     */
    @Test
    public void testRemoveUserById() {
        System.removeUser( SystemTest.fakeUser.getId() );
        Assert.assertFalse( System.hasUser( SystemTest.fakeUser ) );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testRemoveUserByIdNonExistantUser() {
        System.removeUser( -1 );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testRemoveUserByIdNullId() {
        System.removeUser( ( Integer ) null );
    }
    
    /**
     * TODO
     */
    @Test
    public void testRemoveUserByName() {
        System.removeUser( "fake_user" );
        Assert.assertFalse( System.hasUser( SystemTest.fakeUser ) );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testRemoveUserByNameNonExistantUser() {
        System.removeUser( "nonexistant user" );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testRemoveUserByNameNullName() {
        System.removeUser( ( String ) null );
    }
    
    /**
     * TODO
     */
    @Test( expected = IllegalArgumentException.class )
    public void testRemoveUserNonExistantUser() {
        User u = System.createUser( "nonexistant user" );
        System.removeUser( u );
        System.removeUser( u );
    }
    
    /**
     * TODO
     */
    @Test( expected = NullPointerException.class )
    public void testRemoveUserNullUser() {
        System.removeUser( ( User ) null );
    }
    
}