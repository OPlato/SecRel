/*
 * <!-- TODO -->
 */
package edu.fgcu.secrel;

import java.util.*;

import org.junit.*;

/**
 * The SystemMaintenanceTest class tests the ability of the SecRelSystem to
 * make, remove and query role assignments.
 *
 * @author lngibson
 *        
 */
public class SecRelSystemAssignmentTest {
	
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
	
	/*
	 * The teacher role. The ibuckley user is assigned to this role in
	 * setUpBeforeClass. The CreateRoleDuplicateName test case attempts to
	 * create another role with this role's name.
	 */
	// private static Role Teacher;
	
	/**
	 * The assistant role. The lngibson user is assigned to this role in
	 * setUpBeforeClass. The GetRoles test case verifies the membership of the
	 * lngibson user in this role.
	 */
	private static Role Assistant;
	
	/**
	 * The coolguy role. The AssignRole* test cases attempt to assign the
	 * lngibson user to this role.
	 */
	private static Role CoolGuy;
	
	/**
	 * The lames role.
	 */
	private static Role Lames;
	
	/**
	 * The administrator user. This user is assigned to the administrator role
	 * in setUpBeforeClass. The HasUser* test cases query the System on the
	 * existence of this user. The FindUser* test cases attempt to instantiate
	 * Users corresponding to this user. The GetMembers test case verifies the
	 * membership of this user in the administrator role.
	 */
	private static User administrator;
	
	/**
	 * The ibuckley user. The GetMembers test case verifies the membership of
	 * this user in the administrator role.
	 */
	private static User ibuckley;
	
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
		// create roles and users for testing
		SecRelSystemAssignmentTest.Administrator = SecRelSystem.createRole("administrator");
		// SecRelSystemAssignmentTest.Teacher = System.createRole( "teacher" );
		SecRelSystemAssignmentTest.Assistant = SecRelSystem.createRole("assistant");
		SecRelSystemAssignmentTest.CoolGuy = SecRelSystem.createRole("coolguy");
		SecRelSystemAssignmentTest.Lames = SecRelSystem.createRole("lames");
		SecRelSystemAssignmentTest.administrator = SecRelSystem.createUser("administrator");
		SecRelSystemAssignmentTest.ibuckley = SecRelSystem.createUser("ibuckley");
		SecRelSystemAssignmentTest.lngibson = SecRelSystem.createUser("lngibson");
		SecRelSystem.assignRole("administrator", "administrator");
		SecRelSystem.assignRole("ibuckley", "administrator");
		// SecRelSystem.assignRole( "ibuckley", "teacher" );
		SecRelSystem.assignRole("lngibson", "administrator");
		SecRelSystem.assignRole("lngibson", "assistant");
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * Removes all users and roles created in setUpBeforeClass.
	 *
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		SecRelSystemDebuggingUtil.verify();
		if (SecRelSystem.hasRole(SecRelSystemAssignmentTest.Administrator))
			SecRelSystem.removeRole(SecRelSystemAssignmentTest.Administrator);
		if (SecRelSystem.hasRole(SecRelSystemAssignmentTest.Assistant))
			SecRelSystem.removeRole(SecRelSystemAssignmentTest.Assistant);
		if (SecRelSystem.hasRole(SecRelSystemAssignmentTest.CoolGuy))
			SecRelSystem.removeRole(SecRelSystemAssignmentTest.CoolGuy);
		if (SecRelSystem.hasRole(SecRelSystemAssignmentTest.Lames))
			SecRelSystem.removeRole(SecRelSystemAssignmentTest.Lames);
		if (SecRelSystem.hasUser(SecRelSystemAssignmentTest.administrator))
			SecRelSystem.removeUser(SecRelSystemAssignmentTest.administrator);
		if (SecRelSystem.hasUser(SecRelSystemAssignmentTest.ibuckley))
			SecRelSystem.removeUser(SecRelSystemAssignmentTest.ibuckley);
		if (SecRelSystem.hasUser(SecRelSystemAssignmentTest.lngibson))
			SecRelSystem.removeUser(SecRelSystemAssignmentTest.lngibson);
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * The fake_role role. The RemoveRole* test cases attempt to remove this
	 * role.
	 */
	private Role FakeRole;
	
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
		// the fake role and user must be created before each execution because
		// the RemoveRole* and RemoveUser test cases remove them
		FakeRole = SecRelSystem.createRole("fake_role");
		fakeUser = SecRelSystem.createUser("fake_user");
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
		if (SecRelSystem.hasUser(fakeUser))
			SecRelSystem.removeUser(fakeUser);
		if (SecRelSystem.isMemberOf(SecRelSystemAssignmentTest.lngibson, SecRelSystemAssignmentTest.CoolGuy))
			SecRelSystem.unassignRole(SecRelSystemAssignmentTest.lngibson, SecRelSystemAssignmentTest.CoolGuy);
		// the philosopher role and plato user are created by the CreateRole and
		// CreateUser test cases
		// there are no known side effects for not removing them but they are
		// removed just in case
		if (SecRelSystem.hasRole("philosopher"))
			SecRelSystem.removeRole("philosopher");
		if (SecRelSystem.hasUser("plato"))
			SecRelSystem.removeUser("plato");
	}
	
	/**
	 * Attempts to assign the lngibson user to the coolguy role.
	 */
	@Test
	public void testAssignRole() {
		SecRelSystem.assignRole(SecRelSystemAssignmentTest.lngibson, SecRelSystemAssignmentTest.CoolGuy);
		Assert.assertTrue("The System failed to reflect the lngibson user's assignment to the coolguy role",
		        SecRelSystem.isMemberOf(SecRelSystemAssignmentTest.lngibson, SecRelSystemAssignmentTest.CoolGuy));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberForwardCheck(SecRelSystemAssignmentTest.lngibson.getId(),
		        SecRelSystemAssignmentTest.CoolGuy.getId()));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberBackwardCheck(SecRelSystemAssignmentTest.lngibson.getId(),
		        SecRelSystemAssignmentTest.CoolGuy.getId()));
	}
	
	/**
	 * Attempts to assign the administrator user to the administrator role. This
	 * should fail because that user was already assigned to that role in
	 * setUpBeforeClass.
	 */
	@Test(expected = IllegalStateException.class)
	public void testAssignRoleAlreadyAssigned() {
		SecRelSystem.assignRole(SecRelSystemAssignmentTest.administrator, SecRelSystemAssignmentTest.Administrator);
	}
	
	/**
	 * Attempts to assign the lngibson user to the coolguy role.
	 */
	@Test
	public void testAssignRoleById() {
		SecRelSystem.assignRole(SecRelSystemAssignmentTest.lngibson.getId(),
		        SecRelSystemAssignmentTest.CoolGuy.getId());
		Assert.assertTrue("The System failed to reflect the lngibson user's assignment to the coolguy role",
		        SecRelSystem.isMemberOf(SecRelSystemAssignmentTest.lngibson, SecRelSystemAssignmentTest.CoolGuy));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberForwardCheck(SecRelSystemAssignmentTest.lngibson,
		        SecRelSystemAssignmentTest.CoolGuy));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberBackwardCheck(SecRelSystemAssignmentTest.lngibson,
		        SecRelSystemAssignmentTest.CoolGuy));
	}
	
	/**
	 * Attempts to assign the administrator user to the administrator role. This
	 * should fail because that user was already assigned to that role in
	 * setUpBeforeClass.
	 */
	@Test(expected = IllegalStateException.class)
	public void testAssignRoleByIdAlreadyAssigned() {
		SecRelSystem.assignRole(SecRelSystemAssignmentTest.administrator.getId(),
		        SecRelSystemAssignmentTest.Administrator.getId());
	}
	
	/**
	 * Attempts to assign a user, fake_user to a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleByIdNonExistantRole() {
		SecRelSystem.assignRole(fakeUser.getId(), -1);
	}
	
	/**
	 * Attempts to assign a non existent user to a role, fake_role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleByIdNonExistantUser() {
		SecRelSystem.assignRole(-1, FakeRole.getId());
	}
	
	/**
	 * Attempts to assign the lngibson user to a role using a null reference for
	 * role.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleByIdNullRoleId() {
		SecRelSystem.assignRole(SecRelSystemAssignmentTest.lngibson.getId(), null);
	}
	
	/**
	 * Attempts to assign a user to the coolguy role using a null reference for
	 * user.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleByIdNullUserId() {
		SecRelSystem.assignRole(null, SecRelSystemAssignmentTest.CoolGuy.getId());
	}
	
	/**
	 * Attempts to assign the lngibson user to the coolguy role.
	 */
	@Test
	public void testAssignRoleByName() {
		SecRelSystem.assignRole("lngibson", "coolguy");
		Assert.assertTrue("The System failed to reflect the lngibson user's assignment to the coolguy role",
		        SecRelSystem.isMemberOf(SecRelSystemAssignmentTest.lngibson, SecRelSystemAssignmentTest.CoolGuy));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberForwardCheck(SecRelSystemAssignmentTest.lngibson.getId(),
		        SecRelSystemAssignmentTest.CoolGuy.getId()));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberBackwardCheck(SecRelSystemAssignmentTest.lngibson.getId(),
		        SecRelSystemAssignmentTest.CoolGuy.getId()));
	}
	
	/**
	 * Attempts to assign the administrator user to the administrator role. This
	 * should fail because that user was already assigned to that role in
	 * setUpBeforeClass.
	 */
	@Test(expected = IllegalStateException.class)
	public void testAssignRoleByNameAlreadyAssigned() {
		SecRelSystem.assignRole(SecRelSystemAssignmentTest.administrator.getName(),
		        SecRelSystemAssignmentTest.Administrator.getName());
	}
	
	/**
	 * Attempts to assign a user, fake_user to a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleByNameNonExistantRole() {
		SecRelSystem.assignRole(fakeUser.getName(), "nonexistent role");
	}
	
	/**
	 * Attempts to assign a non existent user to a role, fake_role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleByNameNonExistantUser() {
		SecRelSystem.assignRole("nonexistent user", FakeRole.getName());
	}
	
	/**
	 * Attempts to assign the lngibson user to a role using a null reference for
	 * role.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleByNameNullRoleName() {
		SecRelSystem.assignRole("lngibson", null);
	}
	
	/**
	 * Attempts to assign a user to the coolguy role using a null reference for
	 * user.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleByNameNullUserName() {
		SecRelSystem.assignRole(null, "coolguy");
	}
	
	/**
	 * Attempts to assign a user, fake_user to a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleNonExistantRole() {
		SecRelSystem.removeRole(FakeRole);
		SecRelSystem.assignRole(fakeUser, FakeRole);
	}
	
	/**
	 * Attempts to assign a non existent user to a role, fake_role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleNonExistantUser() {
		SecRelSystem.removeUser(fakeUser);
		SecRelSystem.assignRole(fakeUser, FakeRole);
	}
	
	/**
	 * Attempts to assign the lngibson user to a role using a null reference for
	 * role.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleNullRole() {
		SecRelSystem.assignRole(SecRelSystemAssignmentTest.lngibson, null);
	}
	
	/**
	 * Attempts to assign a user to the coolguy role using a null reference for
	 * user.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleNullUser() {
		SecRelSystem.assignRole(null, SecRelSystemAssignmentTest.CoolGuy);
	}
	
	/**
	 * Queries the members of the administrator role.
	 */
	@Test
	public void testGetMembers() {
		Collection<User> as = SecRelSystem.getMembers(SecRelSystemAssignmentTest.Administrator.getId());
		List<User> es = Arrays.asList(SecRelSystemAssignmentTest.administrator, SecRelSystemAssignmentTest.ibuckley,
		        SecRelSystemAssignmentTest.lngibson);
		Collections.sort(es);
		Iterator<User> ai = as.iterator(), ei = es.iterator();
		for (; ai.hasNext() && ei.hasNext();)
			Assert.assertEquals("Systems roles for the lngibson user are incorrect", ei.next(), ai.next());
		Assert.assertFalse("System claims membership of the lngibson user in an extra role", ai.hasNext());
		Assert.assertFalse("System denies membership of the lngibson user in its role", ai.hasNext());
	}
	
	/**
	 * Queries the roles of the lngibson user.
	 */
	@Test
	public void testGetRoles() {
		Collection<Role> as = SecRelSystem.getRoles(SecRelSystemAssignmentTest.lngibson.getId());
		List<Role> es = Arrays.asList(SecRelSystemAssignmentTest.Administrator, SecRelSystemAssignmentTest.Assistant);
		Collections.sort(es);
		Iterator<Role> ai = as.iterator(), ei = es.iterator();
		for (; ai.hasNext() && ei.hasNext();)
			Assert.assertEquals("Systems roles for the lngibson user are incorrect", ei.next(), ai.next());
		Assert.assertFalse("System claims membership of the lngibson user in an extra role", ai.hasNext());
		Assert.assertFalse("System denies membership of the lngibson user its role", ai.hasNext());
	}
	
	/**
	 * Queries if the administrator user is assigned to the administrator role.
	 */
	@Test
	public void testIsMemberOf() {
		Assert.assertTrue("System denies membership of the administrator user in the administrator role", SecRelSystem
		        .isMemberOf(SecRelSystemAssignmentTest.administrator, SecRelSystemAssignmentTest.Administrator));
		Assert.assertFalse("System claims membership of the lngibson user in the lames role",
		        SecRelSystem.isMemberOf(SecRelSystemAssignmentTest.lngibson, SecRelSystemAssignmentTest.Lames));
	}
	
	/**
	 * Queries if the administrator user is assigned to the administrator role.
	 */
	@Test
	public void testIsMemberOfById() {
		Assert.assertTrue("System denies membership of the administrator user in the administrator role",
		        SecRelSystem.isMemberOf(SecRelSystemAssignmentTest.administrator.getId(),
		                SecRelSystemAssignmentTest.Administrator.getId()));
		Assert.assertFalse("System claims membership of the lngibson user in the lames role", SecRelSystem
		        .isMemberOf(SecRelSystemAssignmentTest.lngibson.getId(), SecRelSystemAssignmentTest.Lames.getId()));
	}
	
	/**
	 * Queries if the administrator user is assigned to the administrator role.
	 */
	@Test
	public void testIsMemberOfByName() {
		Assert.assertTrue("System denies membership of the administrator user in the administrator role",
		        SecRelSystem.isMemberOf(SecRelSystemAssignmentTest.administrator.getName(),
		                SecRelSystemAssignmentTest.Administrator.getName()));
		Assert.assertFalse("System claims membership of the lngibson user in the lames role", SecRelSystem
		        .isMemberOf(SecRelSystemAssignmentTest.lngibson.getName(), SecRelSystemAssignmentTest.Lames.getName()));
	}
	
	/**
	 * Attempts to unassign the fake_user user from the fake_role role.
	 */
	@Test
	public void testUnassignRole() {
		SecRelSystem.assignRole("fake_user", "fake_role");
		Assert.assertTrue("System denies membership of the fake_user user in the fake_role role",
		        SecRelSystem.isMemberOf(fakeUser, FakeRole));
		SecRelSystem.unassignRole(fakeUser, FakeRole);
		Assert.assertFalse("System claims membership of the fake_user user in the fake_role role",
		        SecRelSystem.isMemberOf(fakeUser, FakeRole));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberForwardCheck(fakeUser.getId(), FakeRole.getId()));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberBackwardCheck(fakeUser.getId(), FakeRole.getId()));
	}
	
	/**
	 * Attempts to unassign the fake_user user from the fake_role role.
	 */
	@Test
	public void testUnassignRoleById() {
		SecRelSystem.assignRole("fake_user", "fake_role");
		Assert.assertTrue("System denies membership of the fake_user user in the fake_role role",
		        SecRelSystem.isMemberOf(fakeUser, FakeRole));
		SecRelSystem.unassignRole(fakeUser.getId(), FakeRole.getId());
		Assert.assertFalse("System claims membership of the fake_user user in the fake_role role",
		        SecRelSystem.isMemberOf(fakeUser, FakeRole));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberForwardCheck(fakeUser.getId(), FakeRole.getId()));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberBackwardCheck(fakeUser.getId(), FakeRole.getId()));
	}
	
	/**
	 * Attempts to unassign the fake_user user from the fake_role role.
	 */
	@Test
	public void testUnassignRoleByName() {
		SecRelSystem.assignRole("fake_user", "fake_role");
		Assert.assertTrue("System denies membership of the fake_user user in the fake_role role",
		        SecRelSystem.isMemberOf(fakeUser, FakeRole));
		SecRelSystem.unassignRole(fakeUser.getName(), FakeRole.getName());
		Assert.assertFalse("System claims membership of the fake_user user in the fake_role role",
		        SecRelSystem.isMemberOf(fakeUser, FakeRole));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberForwardCheck(fakeUser.getId(), FakeRole.getId()));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberBackwardCheck(fakeUser.getId(), FakeRole.getId()));
	}
	
}