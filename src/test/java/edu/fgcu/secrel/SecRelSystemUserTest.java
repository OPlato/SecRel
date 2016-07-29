/*
 * <!-- TODO -->
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
		SecRelSystemUserTest.administrator = SecRelSystem.createUser("administrator");
		// SecRelSystemUserTest.ibuckley = System.createUser( "ibuckley" );
		SecRelSystemUserTest.lngibson = SecRelSystem.createUser("lngibson");
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * Removes all users created in setUpBeforeClass.
	 *
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		SecRelSystemDebuggingUtil.verify();
		if (SecRelSystem.hasUser(SecRelSystemUserTest.administrator))
			SecRelSystem.removeUser(SecRelSystemUserTest.administrator);
		if (SecRelSystem.hasUser(SecRelSystemUserTest.lngibson))
			SecRelSystem.removeUser(SecRelSystemUserTest.lngibson);
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
		fakeUser = SecRelSystem.createUser("fake_user");
		SecRelSystem.createRole("fake_role1");
		SecRelSystem.createRole("fake_role2");
		SecRelSystem.createRole("fake_role3");
		SecRelSystem.assignRole("fake_user", "fake_role1");
		SecRelSystem.assignRole("fake_user", "fake_role2");
		SecRelSystem.assignRole("fake_user", "fake_role3");
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
		if (SecRelSystem.hasUser(fakeUser))
			SecRelSystem.removeUser(fakeUser);
		if (SecRelSystem.hasRole("fake_role1"))
			SecRelSystem.removeRole("fake_role1");
		if (SecRelSystem.hasRole("fake_role2"))
			SecRelSystem.removeRole("fake_role2");
		if (SecRelSystem.hasRole("fake_role3"))
			SecRelSystem.removeRole("fake_role3");
		// the plato user is created by the CreateUser test case
		// there are no known side effects for not removing it but it is
		// removed just in case
		if (SecRelSystem.hasUser("plato"))
			SecRelSystem.removeUser("plato");
	}
	
	/**
	 * Attempts to create a new user, plato.
	 */
	@Test
	public void testCreateUser() {
		User u = SecRelSystem.createUser("plato");
		Assert.assertNotNull("System failed to return a User instance after the plato user's creation", u);
		Assert.assertTrue("The System failed to reflect the creation of the plato user", SecRelSystem.hasUser(u));
	}
	
	/**
	 * Attempts to create a new user, teacher. This should fail because that
	 * user was already assigned to that role in setUpBeforeClass.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserDuplicateName() {
		SecRelSystem.createUser(SecRelSystemUserTest.lngibson.getName());
	}
	
	/**
	 * Attempts to create a new user using a null name.
	 */
	@Test(expected = NullPointerException.class)
	public void testCreateUserNullName() {
		SecRelSystem.createUser(null);
	}
	
	/**
	 * Attempt to instantiate a User using a user id.
	 */
	@Test
	public void testFindUserById() {
		User r = SecRelSystem.findUser(SecRelSystemUserTest.administrator.getId());
		Assert.assertNotNull("System failed to return a User instance for the administrator user", r);
		Assert.assertEquals("System return an invalid User instance for the administrator user",
		        SecRelSystemUserTest.administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent User using a user id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindUserByIdNonExistent() {
		SecRelSystem.findUser(-1);
	}
	
	/**
	 * Attempt to instantiate a User using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindUserByIdNullId() {
		SecRelSystem.findUser((Integer) null);
	}
	
	/**
	 * Attempt to instantiate a User using a user name.
	 */
	@Test
	public void testFindUserByName() {
		User r = SecRelSystem.findUser(SecRelSystemUserTest.administrator.getName());
		Assert.assertNotNull("System failed to return a User instance for the administrator user", r);
		Assert.assertEquals("System return an invalid User instance for the administrator user",
		        SecRelSystemUserTest.administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent User using a user name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindUserByNameNonExistent() {
		SecRelSystem.findUser("nonexistent user");
	}
	
	/**
	 * Attempt to instantiate a User using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindUserByNameNullName() {
		SecRelSystem.findUser((String) null);
	}
	
	/**
	 * Queries the existence of the administrator user.
	 */
	@Test
	public void testHasUser() {
		Assert.assertTrue("System did not acknowledge administrator user's existence",
		        SecRelSystem.hasUser(SecRelSystemUserTest.administrator));
	}
	
	/**
	 * Queries the existence of the administrator user.
	 */
	@Test
	public void testHasUserById() {
		Assert.assertTrue("System did not acknowledge administrator user's existence",
		        SecRelSystem.hasUser(SecRelSystemUserTest.administrator.getId()));
	}
	
	/**
	 * Queries the existence of a non existent user.
	 */
	@Test
	public void testHasUserByIdNonExistantUser() {
		Assert.assertFalse("System claims existence of user with id -1", SecRelSystem.hasUser(-1));
	}
	
	/**
	 * Queries the existence of the administrator user.
	 */
	@Test
	public void testHasUserByName() {
		Assert.assertTrue("System did not acknowledge administrator user's existence",
		        SecRelSystem.hasUser("administrator"));
	}
	
	/**
	 * Queries the existence of a non existent user.
	 */
	@Test
	public void testHasUserByNameNonExistantUser() {
		Assert.assertFalse("System claims existence of user with name 'nonexistent user'",
		        SecRelSystem.hasUser("nonexistent user"));
	}
	
	/**
	 * Queries the existence of a non existent user.
	 */
	@Test
	public void testHasUserNonExistantUser() {
		User u = SecRelSystem.createUser("nonexistent user");
		SecRelSystem.removeUser(u);
		Assert.assertFalse("System claims existence of user, nonexistent user, which was removed",
		        SecRelSystem.hasUser(u));
	}
	
	/**
	 * Attempt to remove the fake_user user.
	 */
	@Test
	public void testRemoveUser() {
		SecRelSystem.removeUser(fakeUser);
		Assert.assertFalse("System claims existence of user, fake_user, which was removed",
		        SecRelSystem.hasUser(fakeUser));
	}
	
	/**
	 * Attempt to remove the fake_user user.
	 */
	@Test
	public void testRemoveUserById() {
		SecRelSystem.removeUser(fakeUser.getId());
		Assert.assertFalse("System claims existence of user, fake_user, which was removed",
		        SecRelSystem.hasUser(fakeUser));
	}
	
	/**
	 * Attempts to remove a non existent user.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserByIdNonExistantUser() {
		SecRelSystem.removeUser(-1);
	}
	
	/**
	 * Attempts to remove a user using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveUserByIdNullId() {
		SecRelSystem.removeUser((Integer) null);
	}
	
	/**
	 * Attempt to remove the fake_user user.
	 */
	@Test
	public void testRemoveUserByName() {
		SecRelSystem.removeUser("fake_user");
		Assert.assertFalse("System claims existence of user, fake_user, which was removed",
		        SecRelSystem.hasUser(fakeUser));
	}
	
	/**
	 * Attempts to remove a non existent user.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserByNameNonExistantUser() {
		SecRelSystem.removeUser("nonexistent user");
	}
	
	/**
	 * Attempts to remove a user using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveUserByNameNullName() {
		SecRelSystem.removeUser((String) null);
	}
	
	/**
	 * Attempts to remove a non existent user.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserNonExistantUser() {
		User u = SecRelSystem.createUser("nonexistent user");
		SecRelSystem.removeUser(u);
		SecRelSystem.removeUser(u);
	}
	
	/**
	 * Attempts to remove a user using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveUserNullUser() {
		SecRelSystem.removeUser((User) null);
	}
	
}