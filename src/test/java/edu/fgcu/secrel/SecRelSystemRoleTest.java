/*
 * This file defines the tests of SecRelSystem's role functions.
 */
package edu.fgcu.secrel;

import org.junit.*;

/**
 * The SystemMaintenanceTest class tests the SecRelSystem's role maintenance
 * functions.
 *
 * @author lngibson
 *
 */
public class SecRelSystemRoleTest {
	
	/**
	 * The administrator role. The administrator, ibuckley and lngibson users
	 * are assigned to this role in setUpBeforeClass. The Assign*AlreadyAssigned
	 * test cases attempt to assign the administrator user to this role. The
	 * HasRole* test cases query the System on the existence of this role. The
	 * FindRole* test cases attempt to instantiate Roles corresponding to this
	 * role. The GetRoles test case verifies the membership of the lngibson user
	 * in this role.
	 */
	private static Role Administrator;
	
	/**
	 * The teacher role. The ibuckley user is assigned to this role in
	 * setUpBeforeClass. The CreateRoleDuplicateName test case attempts to
	 * create another role with this role's name.
	 */
	private static Role Teacher;
	
	/*
	 * The assistant role. The lngibson user is assigned to this role in
	 * setUpBeforeClass. The GetRoles test case verifies the membership of the
	 * lngibson user in this role.
	 */
	// private static Role Assistant;
	
	/**
	 * The coolguy role. The AssignRole* test cases attempt to assign the
	 * lngibson user to this role.
	 */
	private static Role CoolGuy;
	
	/**
	 * Creates the users and roles needed for testing that are not created or
	 * removed by tests.
	 *
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		// create roles for testing
		SecRelSystemRoleTest.Administrator = Roles.createRole("administrator");
		SecRelSystemRoleTest.Teacher = Roles.createRole("teacher");
		// SecRelSystemRoleTest.Assistant = System.createRole( "assistant" );
		SecRelSystemRoleTest.CoolGuy = Roles.createRole("coolguy");
		// SecRelSystemRoleTest.Lames = System.createRole( "lames" );
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * Removes all roles created in setUpBeforeClass.
	 *
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		SecRelSystemDebuggingUtil.verify();
		if (Roles.hasRole(SecRelSystemRoleTest.Administrator))
			Roles.removeRole(SecRelSystemRoleTest.Administrator);
		if (Roles.hasRole(SecRelSystemRoleTest.Teacher))
			Roles.removeRole(SecRelSystemRoleTest.Teacher);
		if (Roles.hasRole(SecRelSystemRoleTest.CoolGuy))
			Roles.removeRole(SecRelSystemRoleTest.CoolGuy);
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * The fake_role role. The RemoveRole* test cases attempt to remove this
	 * role.
	 */
	private Role FakeRole;
	
	/**
	 * Creates the users and roles needed for testing that may be created or
	 * removed by tests.
	 *
	 */
	@Before
	public void setUp() {
		// the fake role must be created before each execution because
		// the RemoveRole* test cases remove it
		FakeRole = Roles.createRole("fake_role");
		Users.createUser("fake_user1");
		Users.createUser("fake_user2");
		Users.createUser("fake_user3");
		Members.assignRole("fake_user1", "fake_role");
		Members.assignRole("fake_user2", "fake_role");
		Members.assignRole("fake_user3", "fake_role");
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
		// the fake role and user must be removed so that setUp does not fail
		// when (re)creating them
		if (Roles.hasRole(FakeRole))
			Roles.removeRole(FakeRole);
		if (Users.hasUser("fake_user1"))
			Users.removeUser("fake_user1");
		if (Users.hasUser("fake_user2"))
			Users.removeUser("fake_user2");
		if (Users.hasUser("fake_user3"))
			Users.removeUser("fake_user3");
		// the philosopher role is created by the CreateRole test case
		// there are no known side effects for not removing it but it is
		// removed just in case
		if (Roles.hasRole("philosopher"))
			Roles.removeRole("philosopher");
	}
	
	/**
	 * Attempts to create a new role, philosopher.
	 */
	@Test
	public void testCreateRole() {
		Role r = Roles.createRole("philosopher");
		Assert.assertNotNull("System failed to return a Role instance after the philosopher role's creation", r);
		Assert.assertTrue("The System failed to reflect the creation of the philosopher role", Roles.hasRole(r));
	}
	
	/**
	 * Attempts to create a new role, teacher. This should fail because that
	 * role was already assigned to that role in setUpBeforeClass.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateRoleDuplicateName() {
		Roles.createRole(SecRelSystemRoleTest.Teacher.getName());
	}
	
	/**
	 * Attempts to create a new role using a null name.
	 */
	@Test(expected = NullPointerException.class)
	public void testCreateRoleNullName() {
		Roles.createRole(null);
	}
	
	/**
	 * Attempt to instantiate a Role using a role id.
	 */
	@Test
	public void testFindRoleById() {
		Role r = Roles.findRole(SecRelSystemRoleTest.Administrator.getId());
		Assert.assertNotNull("System failed to return a Role instance for the administrator role", r);
		Assert.assertEquals("System return an invalid Role instance for the administrator role",
				SecRelSystemRoleTest.Administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent Role using a role id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindRoleByIdNonExistent() {
		Roles.findRole(-1);
	}
	
	/**
	 * Attempt to instantiate a Role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindRoleByIdNullId() {
		Roles.findRole((Integer) null);
	}
	
	/**
	 * Attempt to instantiate a Role using a role name.
	 */
	@Test
	public void testFindRoleByName() {
		Role r = Roles.findRole(SecRelSystemRoleTest.Administrator.getName());
		Assert.assertNotNull("System failed to return a Role instance for the administrator role", r);
		Assert.assertEquals("System return an invalid Role instance for the administrator role",
				SecRelSystemRoleTest.Administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent Role using a role name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindRoleByNameNonExistent() {
		Roles.findRole("nonexistent role");
	}
	
	/**
	 * Attempt to instantiate a Role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindRoleByNameNullName() {
		Roles.findRole((String) null);
	}
	
	/**
	 * Queries the existence of the administrator role.
	 */
	@Test
	public void testHasRole() {
		Assert.assertTrue("System did not acknowledge administrator role's existence",
				Roles.hasRole(SecRelSystemRoleTest.Administrator));
	}
	
	/**
	 * Queries the existence of the administrator role.
	 */
	@Test
	public void testHasRoleById() {
		Assert.assertTrue("System did not acknowledge administrator role's existence",
				Roles.hasRole(SecRelSystemRoleTest.Administrator.getId()));
	}
	
	/**
	 * Queries the existence of a non existent role.
	 */
	@Test
	public void testHasRoleByIdNonExistantRole() {
		Assert.assertFalse("System claims existence of role with id -1", Roles.hasRole(-1));
	}
	
	/**
	 * Queries the existence of the administrator role.
	 */
	@Test
	public void testHasRoleByName() {
		Assert.assertTrue("System did not acknowledge administrator role's existence", Roles.hasRole("administrator"));
	}
	
	/**
	 * Queries the existence of a non existent role.
	 */
	@Test
	public void testHasRoleByNameNonExistantRole() {
		Assert.assertFalse("System claims existence of role with name 'nonexistent role'",
				Roles.hasRole("nonexistent role"));
	}
	
	/**
	 * Queries the existence of a non existent role.
	 */
	@Test
	public void testHasRoleNonExistantRole() {
		Role r = Roles.createRole("nonexistent role");
		Roles.removeRole(r);
		Assert.assertFalse("System claims existence of role, nonexistent role, which was removed", Roles.hasRole(r));
	}
	
	/**
	 * Attempt to remove the fake_role role.
	 */
	@Test
	public void testRemoveRole() {
		Roles.removeRole(FakeRole);
		Assert.assertFalse("System claims existence of role, fake_role, which was removed", Roles.hasRole(FakeRole));
	}
	
	/**
	 * Attempt to remove the fake_role role.
	 */
	@Test
	public void testRemoveRoleById() {
		Roles.removeRole(FakeRole.getId());
		Assert.assertFalse("System claims existence of role, fake_role, which was removed", Roles.hasRole(FakeRole));
	}
	
	/**
	 * Attempts to remove a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveRoleByIdNonExistantRole() {
		Roles.removeRole(-1);
	}
	
	/**
	 * Attempts to remove a role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveRoleByIdNullId() {
		Roles.removeRole((Integer) null);
	}
	
	/**
	 * Attempt to remove the fake_role role.
	 */
	@Test
	public void testRemoveRoleByName() {
		Roles.removeRole("fake_role");
		Assert.assertFalse("System claims existence of role, fake_role, which was removed", Roles.hasRole(FakeRole));
	}
	
	/**
	 * Attempts to remove a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveRoleByNameNonExistantRole() {
		Roles.removeRole("nonexistent role");
	}
	
	/**
	 * Attempts to remove a role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveRoleByNameNullName() {
		Roles.removeRole((String) null);
	}
	
	/**
	 * Attempts to remove a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveRoleNonExistantRole() {
		Role r = Roles.createRole("nonexistent role");
		Roles.removeRole(r);
		Roles.removeRole(r);
	}
	
	/**
	 * Attempts to remove a role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveRoleNullRole() {
		Roles.removeRole((Role) null);
	}
	
}