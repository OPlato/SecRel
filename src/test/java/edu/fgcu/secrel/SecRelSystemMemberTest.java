/*
 * This file defines the tests of SecRelSystem's member functions.
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
public class SecRelSystemMemberTest {
	
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
		SecRelSystemMemberTest.Administrator = Roles.createRole("administrator");
		// SecRelSystemAssignmentTest.Teacher = System.createRole( "teacher" );
		SecRelSystemMemberTest.Assistant = Roles.createRole("assistant");
		SecRelSystemMemberTest.CoolGuy = Roles.createRole("coolguy");
		SecRelSystemMemberTest.Lames = Roles.createRole("lames");
		SecRelSystemMemberTest.administrator = Users.createUser("administrator");
		SecRelSystemMemberTest.ibuckley = Users.createUser("ibuckley");
		SecRelSystemMemberTest.lngibson = Users.createUser("lngibson");
		Members.assignRole("administrator", "administrator");
		Members.assignRole("ibuckley", "administrator");
		// SecRelSystem.assignRole( "ibuckley", "teacher" );
		Members.assignRole("lngibson", "administrator");
		Members.assignRole("lngibson", "assistant");
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * Removes all users and roles created in setUpBeforeClass.
	 *
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		SecRelSystemDebuggingUtil.verify();
		if (Roles.hasRole(SecRelSystemMemberTest.Administrator))
			Roles.removeRole(SecRelSystemMemberTest.Administrator);
		if (Roles.hasRole(SecRelSystemMemberTest.Assistant))
			Roles.removeRole(SecRelSystemMemberTest.Assistant);
		if (Roles.hasRole(SecRelSystemMemberTest.CoolGuy))
			Roles.removeRole(SecRelSystemMemberTest.CoolGuy);
		if (Roles.hasRole(SecRelSystemMemberTest.Lames))
			Roles.removeRole(SecRelSystemMemberTest.Lames);
		if (Users.hasUser(SecRelSystemMemberTest.administrator))
			Users.removeUser(SecRelSystemMemberTest.administrator);
		if (Users.hasUser(SecRelSystemMemberTest.ibuckley))
			Users.removeUser(SecRelSystemMemberTest.ibuckley);
		if (Users.hasUser(SecRelSystemMemberTest.lngibson))
			Users.removeUser(SecRelSystemMemberTest.lngibson);
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
		FakeRole = Roles.createRole("fake_role");
		fakeUser = Users.createUser("fake_user");
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
		if (Users.hasUser(fakeUser))
			Users.removeUser(fakeUser);
		if (Members.isMemberOf(SecRelSystemMemberTest.lngibson, SecRelSystemMemberTest.CoolGuy))
			Members.unassignRole(SecRelSystemMemberTest.lngibson, SecRelSystemMemberTest.CoolGuy);
		// the philosopher role and plato user are created by the CreateRole and
		// CreateUser test cases
		// there are no known side effects for not removing them but they are
		// removed just in case
		if (Roles.hasRole("philosopher"))
			Roles.removeRole("philosopher");
		if (Users.hasUser("plato"))
			Users.removeUser("plato");
	}
	
	/**
	 * Attempts to assign the lngibson user to the coolguy role.
	 */
	@Test
	public void testAssignRole() {
		Members.assignRole(SecRelSystemMemberTest.lngibson, SecRelSystemMemberTest.CoolGuy);
		Assert.assertTrue("The System failed to reflect the lngibson user's assignment to the coolguy role",
				Members.isMemberOf(SecRelSystemMemberTest.lngibson, SecRelSystemMemberTest.CoolGuy));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberForwardCheck(SecRelSystemMemberTest.lngibson.getId(),
				SecRelSystemMemberTest.CoolGuy.getId()));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberBackwardCheck(SecRelSystemMemberTest.lngibson.getId(),
				SecRelSystemMemberTest.CoolGuy.getId()));
	}
	
	/**
	 * Attempts to assign the administrator user to the administrator role. This
	 * should fail because that user was already assigned to that role in
	 * setUpBeforeClass.
	 */
	@Test(expected = IllegalStateException.class)
	public void testAssignRoleAlreadyAssigned() {
		Members.assignRole(SecRelSystemMemberTest.administrator, SecRelSystemMemberTest.Administrator);
	}
	
	/**
	 * Attempts to assign the lngibson user to the coolguy role.
	 */
	@Test
	public void testAssignRoleById() {
		Members.assignRole(SecRelSystemMemberTest.lngibson.getId(), SecRelSystemMemberTest.CoolGuy.getId());
		Assert.assertTrue("The System failed to reflect the lngibson user's assignment to the coolguy role",
				Members.isMemberOf(SecRelSystemMemberTest.lngibson, SecRelSystemMemberTest.CoolGuy));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberForwardCheck(SecRelSystemMemberTest.lngibson,
				SecRelSystemMemberTest.CoolGuy));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberBackwardCheck(SecRelSystemMemberTest.lngibson,
				SecRelSystemMemberTest.CoolGuy));
	}
	
	/**
	 * Attempts to assign the administrator user to the administrator role. This
	 * should fail because that user was already assigned to that role in
	 * setUpBeforeClass.
	 */
	@Test(expected = IllegalStateException.class)
	public void testAssignRoleByIdAlreadyAssigned() {
		Members.assignRole(SecRelSystemMemberTest.administrator.getId(),
				SecRelSystemMemberTest.Administrator.getId());
	}
	
	/**
	 * Attempts to assign a user, fake_user to a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleByIdNonExistantRole() {
		Members.assignRole(fakeUser.getId(), -1);
	}
	
	/**
	 * Attempts to assign a non existent user to a role, fake_role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleByIdNonExistantUser() {
		Members.assignRole(-1, FakeRole.getId());
	}
	
	/**
	 * Attempts to assign the lngibson user to a role using a null reference for
	 * role.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleByIdNullRoleId() {
		Members.assignRole(SecRelSystemMemberTest.lngibson.getId(), null);
	}
	
	/**
	 * Attempts to assign a user to the coolguy role using a null reference for
	 * user.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleByIdNullUserId() {
		Members.assignRole(null, SecRelSystemMemberTest.CoolGuy.getId());
	}
	
	/**
	 * Attempts to assign the lngibson user to the coolguy role.
	 */
	@Test
	public void testAssignRoleByName() {
		Members.assignRole("lngibson", "coolguy");
		Assert.assertTrue("The System failed to reflect the lngibson user's assignment to the coolguy role",
				Members.isMemberOf(SecRelSystemMemberTest.lngibson, SecRelSystemMemberTest.CoolGuy));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberForwardCheck(SecRelSystemMemberTest.lngibson.getId(),
				SecRelSystemMemberTest.CoolGuy.getId()));
		Assert.assertTrue(SecRelSystemDebuggingUtil.memberBackwardCheck(SecRelSystemMemberTest.lngibson.getId(),
				SecRelSystemMemberTest.CoolGuy.getId()));
	}
	
	/**
	 * Attempts to assign the administrator user to the administrator role. This
	 * should fail because that user was already assigned to that role in
	 * setUpBeforeClass.
	 */
	@Test(expected = IllegalStateException.class)
	public void testAssignRoleByNameAlreadyAssigned() {
		Members.assignRole(SecRelSystemMemberTest.administrator.getName(),
				SecRelSystemMemberTest.Administrator.getName());
	}
	
	/**
	 * Attempts to assign a user, fake_user to a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleByNameNonExistantRole() {
		Members.assignRole(fakeUser.getName(), "nonexistent role");
	}
	
	/**
	 * Attempts to assign a non existent user to a role, fake_role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleByNameNonExistantUser() {
		Members.assignRole("nonexistent user", FakeRole.getName());
	}
	
	/**
	 * Attempts to assign the lngibson user to a role using a null reference for
	 * role.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleByNameNullRoleName() {
		Members.assignRole("lngibson", null);
	}
	
	/**
	 * Attempts to assign a user to the coolguy role using a null reference for
	 * user.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleByNameNullUserName() {
		Members.assignRole(null, "coolguy");
	}
	
	/**
	 * Attempts to assign a user, fake_user to a non existent role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleNonExistantRole() {
		Roles.removeRole(FakeRole);
		Members.assignRole(fakeUser, FakeRole);
	}
	
	/**
	 * Attempts to assign a non existent user to a role, fake_role.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAssignRoleNonExistantUser() {
		Users.removeUser(fakeUser);
		Members.assignRole(fakeUser, FakeRole);
	}
	
	/**
	 * Attempts to assign the lngibson user to a role using a null reference for
	 * role.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleNullRole() {
		Members.assignRole(SecRelSystemMemberTest.lngibson, null);
	}
	
	/**
	 * Attempts to assign a user to the coolguy role using a null reference for
	 * user.
	 */
	@Test(expected = NullPointerException.class)
	public void testAssignRoleNullUser() {
		Members.assignRole(null, SecRelSystemMemberTest.CoolGuy);
	}
	
	/**
	 * Queries the members of the administrator role.
	 */
	@Test
	public void testGetMembers() {
		Collection<User> as = Members.getMembers(SecRelSystemMemberTest.Administrator.getId());
		List<User> es = Arrays.asList(SecRelSystemMemberTest.administrator, SecRelSystemMemberTest.ibuckley,
				SecRelSystemMemberTest.lngibson);
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
		Collection<Role> as = Members.getRoles(SecRelSystemMemberTest.lngibson.getId());
		List<Role> es = Arrays.asList(SecRelSystemMemberTest.Administrator, SecRelSystemMemberTest.Assistant);
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
		Assert.assertTrue("System denies membership of the administrator user in the administrator role",
				Members.isMemberOf(SecRelSystemMemberTest.administrator, SecRelSystemMemberTest.Administrator));
		Assert.assertFalse("System claims membership of the lngibson user in the lames role",
				Members.isMemberOf(SecRelSystemMemberTest.lngibson, SecRelSystemMemberTest.Lames));
	}
	
	/**
	 * Queries if the administrator user is assigned to the administrator role.
	 */
	@Test
	public void testIsMemberOfById() {
		Assert.assertTrue("System denies membership of the administrator user in the administrator role",
				Members.isMemberOf(SecRelSystemMemberTest.administrator.getId(),
						SecRelSystemMemberTest.Administrator.getId()));
		Assert.assertFalse("System claims membership of the lngibson user in the lames role", Members
				.isMemberOf(SecRelSystemMemberTest.lngibson.getId(), SecRelSystemMemberTest.Lames.getId()));
	}
	
	/**
	 * Queries if the administrator user is assigned to the administrator role.
	 */
	@Test
	public void testIsMemberOfByName() {
		Assert.assertTrue("System denies membership of the administrator user in the administrator role",
				Members.isMemberOf(SecRelSystemMemberTest.administrator.getName(),
						SecRelSystemMemberTest.Administrator.getName()));
		Assert.assertFalse("System claims membership of the lngibson user in the lames role", Members
				.isMemberOf(SecRelSystemMemberTest.lngibson.getName(), SecRelSystemMemberTest.Lames.getName()));
	}
	
	/**
	 * Attempts to unassign the fake_user user from the fake_role role.
	 */
	@Test
	public void testUnassignRole() {
		Members.assignRole("fake_user", "fake_role");
		Assert.assertTrue("System denies membership of the fake_user user in the fake_role role",
				Members.isMemberOf(fakeUser, FakeRole));
		Members.unassignRole(fakeUser, FakeRole);
		Assert.assertFalse("System claims membership of the fake_user user in the fake_role role",
				Members.isMemberOf(fakeUser, FakeRole));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberForwardCheck(fakeUser.getId(), FakeRole.getId()));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberBackwardCheck(fakeUser.getId(), FakeRole.getId()));
	}
	
	/**
	 * Attempts to unassign the fake_user user from the fake_role role.
	 */
	@Test
	public void testUnassignRoleById() {
		Members.assignRole("fake_user", "fake_role");
		Assert.assertTrue("System denies membership of the fake_user user in the fake_role role",
				Members.isMemberOf(fakeUser, FakeRole));
		Members.unassignRole(fakeUser.getId(), FakeRole.getId());
		Assert.assertFalse("System claims membership of the fake_user user in the fake_role role",
				Members.isMemberOf(fakeUser, FakeRole));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberForwardCheck(fakeUser.getId(), FakeRole.getId()));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberBackwardCheck(fakeUser.getId(), FakeRole.getId()));
	}
	
	/**
	 * Attempts to unassign the fake_user user from the fake_role role.
	 */
	@Test
	public void testUnassignRoleByName() {
		Members.assignRole("fake_user", "fake_role");
		Assert.assertTrue("System denies membership of the fake_user user in the fake_role role",
				Members.isMemberOf(fakeUser, FakeRole));
		Members.unassignRole(fakeUser.getName(), FakeRole.getName());
		Assert.assertFalse("System claims membership of the fake_user user in the fake_role role",
				Members.isMemberOf(fakeUser, FakeRole));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberForwardCheck(fakeUser.getId(), FakeRole.getId()));
		Assert.assertFalse(SecRelSystemDebuggingUtil.memberBackwardCheck(fakeUser.getId(), FakeRole.getId()));
	}
	
}