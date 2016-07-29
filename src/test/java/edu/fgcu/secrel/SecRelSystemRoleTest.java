/*
 * <!-- TODO -->
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
		SecRelSystemRoleTest.Administrator = SecRelSystem.createRole("administrator");
		SecRelSystemRoleTest.Teacher = SecRelSystem.createRole("teacher");
		// SecRelSystemRoleTest.Assistant = System.createRole( "assistant" );
		SecRelSystemRoleTest.CoolGuy = SecRelSystem.createRole("coolguy");
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
		if (SecRelSystem.hasRole(SecRelSystemRoleTest.Administrator))
			SecRelSystem.removeRole(SecRelSystemRoleTest.Administrator);
		if (SecRelSystem.hasRole(SecRelSystemRoleTest.Teacher))
			SecRelSystem.removeRole(SecRelSystemRoleTest.Teacher);
		if (SecRelSystem.hasRole(SecRelSystemRoleTest.CoolGuy))
			SecRelSystem.removeRole(SecRelSystemRoleTest.CoolGuy);
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
		FakeRole = SecRelSystem.createRole("fake_role");
		SecRelSystem.createUser("fake_user1");
		SecRelSystem.createUser("fake_user2");
		SecRelSystem.createUser("fake_user3");
		SecRelSystem.assignRole("fake_user1", "fake_role");
		SecRelSystem.assignRole("fake_user2", "fake_role");
		SecRelSystem.assignRole("fake_user3", "fake_role");
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
		if (SecRelSystem.hasRole(FakeRole))
			SecRelSystem.removeRole(FakeRole);
		if (SecRelSystem.hasUser("fake_user1"))
			SecRelSystem.removeUser("fake_user1");
		if (SecRelSystem.hasUser("fake_user2"))
			SecRelSystem.removeUser("fake_user2");
		if (SecRelSystem.hasUser("fake_user3"))
			SecRelSystem.removeUser("fake_user3");
		// the philosopher role is created by the CreateRole test case
		// there are no known side effects for not removing it but it is
		// removed just in case
		if (SecRelSystem.hasRole("philosopher"))
			SecRelSystem.removeRole("philosopher");
	}
	
	/**
	 * Attempts to create a new role, philosopher.
	 */
	@Test
	public void testCreateRole() {
		Role r = SecRelSystem.createRole("philosopher");
		Assert.assertNotNull("System failed to return a Role instance after the philosopher role's creation", r);
		Assert.assertTrue("The System failed to reflect the creation of the philosopher role", SecRelSystem.hasRole(r));
	}
	
	/**
	 * Attempts to create a new role, teacher. This should fail because that
	 * role was already assigned to that role in setUpBeforeClass.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateRoleDuplicateName() {
		SecRelSystem.createRole(SecRelSystemRoleTest.Teacher.getName());
	}
	
	/**
	 * Attempts to create a new role using a null name.
	 */
	@Test(expected = NullPointerException.class)
	public void testCreateRoleNullName() {
		SecRelSystem.createRole(null);
	}
	
	/**
	 * Attempt to instantiate a Role using a role id.
	 */
	@Test
	public void testFindRoleById() {
		Role r = SecRelSystem.findRole(SecRelSystemRoleTest.Administrator.getId());
		Assert.assertNotNull("System failed to return a Role instance for the administrator role", r);
		Assert.assertEquals("System return an invalid Role instance for the administrator role",
		        SecRelSystemRoleTest.Administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent Role using a role id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindRoleByIdNonExistent() {
		SecRelSystem.findRole(-1);
	}
	
	/**
	 * Attempt to instantiate a Role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindRoleByIdNullId() {
		SecRelSystem.findRole((Integer) null);
	}
	
	/**
	 * Attempt to instantiate a Role using a role name.
	 */
	@Test
	public void testFindRoleByName() {
		Role r = SecRelSystem.findRole(SecRelSystemRoleTest.Administrator.getName());
		Assert.assertNotNull("System failed to return a Role instance for the administrator role", r);
		Assert.assertEquals("System return an invalid Role instance for the administrator role",
		        SecRelSystemRoleTest.Administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent Role using a role name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindRoleByNameNonExistent() {
		SecRelSystem.findRole("nonexistent role");
	}
	
	/**
	 * Attempt to instantiate a Role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindRoleByNameNullName() {
		SecRelSystem.findRole((String) null);
	}
	
	/**
	 * Queries the existence of the administrator role.
	 */
	@Test
	public void testHasRole() {
		Assert.assertTrue("System did not acknowledge administrator role's existence",
		        SecRelSystem.hasRole(SecRelSystemRoleTest.Administrator));
	}
	
	/**
	 * Queries the existence of the administrator role.
	 */
	@Test
	public void testHasRoleById() {
		Assert.assertTrue("System did not acknowledge administrator role's existence",
		        SecRelSystem.hasRole(SecRelSystemRoleTest.Administrator.getId()));
	}
	
	/**
	 * Queries the existence of a non existent role.
	 */
	@Test
	public void testHasRoleByIdNonExistantRole() {
		Assert.assertFalse("System claims existence of role with id -1", SecRelSystem.hasRole(-1));
	}
	
	/**
	 * Queries the existence of the administrator role.
	 */
	@Test
	public void testHasRoleByName() {
		Assert.assertTrue("System did not acknowledge administrator role's existence",
		        SecRelSystem.hasRole("administrator"));
	}
	
	/**
	 * Queries the existence of a non existent role.
	 */
	@Test
	public void testHasRoleByNameNonExistantRole() {
		Assert.assertFalse("System claims existence of role with name 'nonexistent role'",
		        SecRelSystem.hasRole("nonexistent role"));
	}
	
	/**
	 * Queries the existence of a non existent role.
	 */
	@Test
	public void testHasRoleNonExistantRole() {
		Role r = SecRelSystem.createRole("nonexistent role");
		SecRelSystem.removeRole(r);
		Assert.assertFalse("System claims existence of role, nonexistent role, which was removed",
		        SecRelSystem.hasRole(r));
	}
	
	/**
	 * Attempt to remove the fake_role role.
	 */
	@Test
	public void testRemoveRole() {
		SecRelSystem.removeRole(FakeRole);
		Assert.assertFalse("System claims existence of role, fake_role, which was removed",
		        SecRelSystem.hasRole(FakeRole));
	}
	
	/**
	 * Attempt to remove the fake_role role.
	 */
	@Test
	public void testRemoveRoleById() {
		SecRelSystem.removeRole(FakeRole.getId());
		Assert.assertFalse("System claims existence of role, fake_role, which was removed",
		        SecRelSystem.hasRole(FakeRole));
	}
	
	/**
	 * Attempts to remove a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveRoleByIdNonExistantRole() {
		SecRelSystem.removeRole(-1);
	}
	
	/**
	 * Attempts to remove a role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveRoleByIdNullId() {
		SecRelSystem.removeRole((Integer) null);
	}
	
	/**
	 * Attempt to remove the fake_role role.
	 */
	@Test
	public void testRemoveRoleByName() {
		SecRelSystem.removeRole("fake_role");
		Assert.assertFalse("System claims existence of role, fake_role, which was removed",
		        SecRelSystem.hasRole(FakeRole));
	}
	
	/**
	 * Attempts to remove a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveRoleByNameNonExistantRole() {
		SecRelSystem.removeRole("nonexistent role");
	}
	
	/**
	 * Attempts to remove a role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveRoleByNameNullName() {
		SecRelSystem.removeRole((String) null);
	}
	
	/**
	 * Attempts to remove a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveRoleNonExistantRole() {
		Role r = SecRelSystem.createRole("nonexistent role");
		SecRelSystem.removeRole(r);
		SecRelSystem.removeRole(r);
	}
	
	/**
	 * Attempts to remove a role using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveRoleNullRole() {
		SecRelSystem.removeRole((Role) null);
	}
	
}