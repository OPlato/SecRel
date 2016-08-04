/*
 * This file defines the tests of SecRelSystem's user functions.
 */
package edu.fgcu.secrel;

import org.junit.*;

/**
 * The SystemMaintenanceTest class tests the SecRelSystem's user maintenance
 * functions.
 *
 * @author lngibson
 *
 */
public class SecRelSystemUserTest {
	
	/**
	 * The administrator user. This user is assigned to the administrator role
	 * in setUpBeforeClass. The HasUser* test cases query the System on the
	 * existence of this user. The FindUser* test cases attempt to instantiate
	 * Users corresponding to this user. The GetMembers test case verifies the
	 * membership of this user in the administrator role.
	 */
	private static User administrator;
	
	/*
	 * The ibuckley user. The GetMembers test case verifies the membership of
	 * this user in the administrator role.
	 */
	// private static User ibuckley;
	
	/**
	 * The lngibson user. The AssignRole* test cases attempt to assign this user
	 * to the coolguy role. The CreateUserDuplicateName test case attempts to
	 * create another user with this user's name. The GetMembers test case
	 * verifies the membership of this user in the administrator role.
	 */
	private static User lngibson;
	
	/**
	 * Creates the users and roles needed for testing that are not created or
	 * removed by tests.
	 *
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		// create users for testing
		SecRelSystemUserTest.administrator = Users.createUser("administrator");
		// SecRelSystemUserTest.ibuckley = System.createUser( "ibuckley" );
		SecRelSystemUserTest.lngibson = Users.createUser("lngibson");
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * Removes all users created in setUpBeforeClass.
	 *
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		SecRelSystemDebuggingUtil.verify();
		if (Users.hasUser(SecRelSystemUserTest.administrator))
			Users.removeUser(SecRelSystemUserTest.administrator);
		if (Users.hasUser(SecRelSystemUserTest.lngibson))
			Users.removeUser(SecRelSystemUserTest.lngibson);
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * The fake_user user. The RemoveUser* test cases attempt to remove this
	 * user.
	 */
	private User fakeUser;
	
	/**
	 * Creates the users and roles needed for testing that may be created or
	 * removed by tests.
	 *
	 */
	@Before
	public void setUp() {
		// the fake user must be created before each execution because
		// the RemoveUser test cases remove it
		fakeUser = Users.createUser("fake_user");
		Roles.createRole("fake_role1");
		Roles.createRole("fake_role2");
		Roles.createRole("fake_role3");
		Members.assignRole("fake_user", "fake_role1");
		Members.assignRole("fake_user", "fake_role2");
		Members.assignRole("fake_user", "fake_role3");
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * Removes the users and roles needed for testing that may be created or
	 * removed by tests.
	 *
	 */
	@After
	public void tearDown() {
		SecRelSystemDebuggingUtil.verify();
		// the fake user must be removed so that setUp does not fail
		// when (re)creating it
		if (Users.hasUser(fakeUser))
			Users.removeUser(fakeUser);
		if (Roles.hasRole("fake_role1"))
			Roles.removeRole("fake_role1");
		if (Roles.hasRole("fake_role2"))
			Roles.removeRole("fake_role2");
		if (Roles.hasRole("fake_role3"))
			Roles.removeRole("fake_role3");
		// the plato user is created by the CreateUser test case
		// there are no known side effects for not removing it but it is
		// removed just in case
		if (Users.hasUser("plato"))
			Users.removeUser("plato");
	}
	
	/**
	 * Attempts to create a new user, plato.
	 */
	@Test
	public void testCreateUser() {
		User u = Users.createUser("plato");
		Assert.assertNotNull("System failed to return a User instance after the plato user's creation", u);
		Assert.assertTrue("The System failed to reflect the creation of the plato user", Users.hasUser(u));
	}
	
	/**
	 * Attempts to create a new user, teacher. This should fail because that
	 * user was already assigned to that role in setUpBeforeClass.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserDuplicateName() {
		Users.createUser(SecRelSystemUserTest.lngibson.getName());
	}
	
	/**
	 * Attempts to create a new user using a null name.
	 */
	@Test(expected = NullPointerException.class)
	public void testCreateUserNullName() {
		Users.createUser(null);
	}
	
	/**
	 * Attempt to instantiate a User using a user id.
	 */
	@Test
	public void testFindUserById() {
		User r = Users.findUser(SecRelSystemUserTest.administrator.getId());
		Assert.assertNotNull("System failed to return a User instance for the administrator user", r);
		Assert.assertEquals("System return an invalid User instance for the administrator user",
				SecRelSystemUserTest.administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent User using a user id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindUserByIdNonExistent() {
		Users.findUser(-1);
	}
	
	/**
	 * Attempt to instantiate a User using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindUserByIdNullId() {
		Users.findUser((Integer) null);
	}
	
	/**
	 * Attempt to instantiate a User using a user name.
	 */
	@Test
	public void testFindUserByName() {
		User r = Users.findUser(SecRelSystemUserTest.administrator.getName());
		Assert.assertNotNull("System failed to return a User instance for the administrator user", r);
		Assert.assertEquals("System return an invalid User instance for the administrator user",
				SecRelSystemUserTest.administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent User using a user name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindUserByNameNonExistent() {
		Users.findUser("nonexistent user");
	}
	
	/**
	 * Attempt to instantiate a User using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindUserByNameNullName() {
		Users.findUser((String) null);
	}
	
	/**
	 * Queries the existence of the administrator user.
	 */
	@Test
	public void testHasUser() {
		Assert.assertTrue("System did not acknowledge administrator user's existence",
				Users.hasUser(SecRelSystemUserTest.administrator));
	}
	
	/**
	 * Queries the existence of the administrator user.
	 */
	@Test
	public void testHasUserById() {
		Assert.assertTrue("System did not acknowledge administrator user's existence",
				Users.hasUser(SecRelSystemUserTest.administrator.getId()));
	}
	
	/**
	 * Queries the existence of a non existent user.
	 */
	@Test
	public void testHasUserByIdNonExistantUser() {
		Assert.assertFalse("System claims existence of user with id -1", Users.hasUser(-1));
	}
	
	/**
	 * Queries the existence of the administrator user.
	 */
	@Test
	public void testHasUserByName() {
		Assert.assertTrue("System did not acknowledge administrator user's existence", Users.hasUser("administrator"));
	}
	
	/**
	 * Queries the existence of a non existent user.
	 */
	@Test
	public void testHasUserByNameNonExistantUser() {
		Assert.assertFalse("System claims existence of user with name 'nonexistent user'",
				Users.hasUser("nonexistent user"));
	}
	
	/**
	 * Queries the existence of a non existent user.
	 */
	@Test
	public void testHasUserNonExistantUser() {
		User u = Users.createUser("nonexistent user");
		Users.removeUser(u);
		Assert.assertFalse("System claims existence of user, nonexistent user, which was removed", Users.hasUser(u));
	}
	
	/**
	 * Attempt to remove the fake_user user.
	 */
	@Test
	public void testRemoveUser() {
		Users.removeUser(fakeUser);
		Assert.assertFalse("System claims existence of user, fake_user, which was removed", Users.hasUser(fakeUser));
	}
	
	/**
	 * Attempt to remove the fake_user user.
	 */
	@Test
	public void testRemoveUserById() {
		Users.removeUser(fakeUser.getId());
		Assert.assertFalse("System claims existence of user, fake_user, which was removed", Users.hasUser(fakeUser));
	}
	
	/**
	 * Attempts to remove a non existent user.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserByIdNonExistantUser() {
		Users.removeUser(-1);
	}
	
	/**
	 * Attempts to remove a user using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveUserByIdNullId() {
		Users.removeUser((Integer) null);
	}
	
	/**
	 * Attempt to remove the fake_user user.
	 */
	@Test
	public void testRemoveUserByName() {
		Users.removeUser("fake_user");
		Assert.assertFalse("System claims existence of user, fake_user, which was removed", Users.hasUser(fakeUser));
	}
	
	/**
	 * Attempts to remove a non existent user.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserByNameNonExistantUser() {
		Users.removeUser("nonexistent user");
	}
	
	/**
	 * Attempts to remove a user using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveUserByNameNullName() {
		Users.removeUser((String) null);
	}
	
	/**
	 * Attempts to remove a non existent user.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserNonExistantUser() {
		User u = Users.createUser("nonexistent user");
		Users.removeUser(u);
		Users.removeUser(u);
	}
	
	/**
	 * Attempts to remove a user using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveUserNullUser() {
		Users.removeUser((User) null);
	}
	
}