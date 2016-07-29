/*
 * <!-- TODO -->
 */
package edu.fgcu.secrel;

import static org.junit.Assert.*;

import org.junit.*;

/**
 * The SystemMaintenanceTest class tests the SecRelSystem's service maintenance
 * functions.
 *
 * @author lngibson
 *        
 */
public class SecRelSystemServiceUnitTest {
	
	/**
	 * The administrator service. The administrator, ibuckley and lngibson users
	 * are assigned to this service in setUpBeforeClass. The
	 * Assign*AlreadyAssigned test cases attempt to assign the administrator
	 * user to this service. The HasService* test cases query the System on the
	 * existence of this service. The FindService* test cases attempt to
	 * instantiate Services corresponding to this service. The GetServices test
	 * case verifies the membership of the lngibson user in this service.
	 */
	private static Service Administrator;
	
	/**
	 * The teacher service. The ibuckley user is assigned to this service in
	 * setUpBeforeClass. The RegisterServiceDuplicateName test case attempts to
	 * create another service with this service's name.
	 */
	private static Service Teacher;
	
	/*
	 * The assistant service. The lngibson user is assigned to this service in
	 * setUpBeforeClass. The GetServices test case verifies the membership of
	 * the lngibson user in this service.
	 */
	// private static Service Assistant;
	
	/**
	 * The coolguy service. The AssignService* test cases attempt to assign the
	 * lngibson user to this service.
	 */
	private static Service CoolGuy;
	
	/*
	 * The lames service.
	 */
	// private static Service Lames;
	
	/*
	 * The administrator user. This user is assigned to the administrator
	 * service in setUpBeforeClass. The HasUser* test cases query the System on
	 * the existence of this user. The FindUser* test cases attempt to
	 * instantiate Users corresponding to this user. The GetMembers test case
	 * verifies the membership of this user in the administrator service.
	 */
	// private static User administrator;
	
	/*
	 * The ibuckley user. The GetMembers test case verifies the membership of
	 * this user in the administrator service.
	 */
	// private static User ibuckley;
	
	/**
	 * The lngibson user. The AssignService* test cases attempt to assign this
	 * user to the coolguy service. The CreateUserDuplicateName test case
	 * attempts to create another user with this user's name. The GetMembers
	 * test case verifies the membership of this user in the administrator
	 * service.
	 */
	private static User lngibson;
	
	/**
	 * Creates the users and services needed for testing that are not created or
	 * removed by tests.
	 *
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		// create services for testing
		// SecRelSystemServiceUnitTest.Administrator =
		// SecRelSystem.registerService("administrator");
		// SecRelSystemServiceUnitTest.Teacher =
		// SecRelSystem.registerService("teacher");
		// SecRelSystemServiceUnitTest.Assistant = System.registerService(
		// "assistant" );
		// SecRelSystemServiceUnitTest.CoolGuy =
		// SecRelSystem.registerService("coolguy");
		// SecRelSystemServiceUnitTest.Lames = System.registerService( "lames"
		// );
		// SecRelSystemServiceUnitTest.administrator = System.createUser(
		// "administrator" );
		// SecRelSystemServiceUnitTest.ibuckley = System.createUser( "ibuckley"
		// );
		SecRelSystemServiceUnitTest.lngibson = SecRelSystem.createUser("lngibson");
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * <!-- TODO -->
	 *
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		SecRelSystemDebuggingUtil.verify();
		if (SecRelSystem.hasService(SecRelSystemServiceUnitTest.Administrator))
			SecRelSystem.removeService(SecRelSystemServiceUnitTest.Administrator);
		if (SecRelSystem.hasService(SecRelSystemServiceUnitTest.Teacher))
			SecRelSystem.removeService(SecRelSystemServiceUnitTest.Teacher);
		if (SecRelSystem.hasService(SecRelSystemServiceUnitTest.CoolGuy))
			SecRelSystem.removeService(SecRelSystemServiceUnitTest.CoolGuy);
		if (SecRelSystem.hasUser(SecRelSystemServiceUnitTest.lngibson))
			SecRelSystem.removeUser(SecRelSystemServiceUnitTest.lngibson);
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * The fake_service service. The RemoveService* test cases attempt to remove
	 * this service.
	 */
	private Service FakeService;
	
	/**
	 * The fake_user user. The RemoveUser* test cases attempt to remove this
	 * user.
	 */
	private User fakeUser;
	
	/**
	 * Creates the users and services needed for testing that may be created or
	 * removed by tests.
	 *
	 */
	@Before
	public void setUp() {
		// the fake service and user must be created before each execution
		// because
		// the RemoveService* and RemoveUser test cases remove them
		// FakeService = SecRelSystem.registerService("fake_service");
		fakeUser = SecRelSystem.createUser("fake_user");
		SecRelSystemDebuggingUtil.verify();
	}
	
	/**
	 * Removes the users and services needed for testing that may be created or
	 * removed by tests.
	 *
	 */
	@After
	public void tearDown() {
		SecRelSystemDebuggingUtil.verify();
		// the fake service and user must be removed so that setUp does not fail
		// when (re)creating them
		if (SecRelSystem.hasService(FakeService))
			SecRelSystem.removeService(FakeService);
		if (SecRelSystem.hasUser(fakeUser))
			SecRelSystem.removeUser(fakeUser);
	}
	
	/**
	 * Attempt to instantiate a Service using a service id.
	 */
	@Test
	public void testFindServiceById() {
		Service r = SecRelSystem.findService(SecRelSystemServiceUnitTest.Administrator.getId());
		assertNotNull("System failed to return a Service instance for the administrator service", r);
		assertEquals("System return an invalid Service instance for the administrator service",
		        SecRelSystemServiceUnitTest.Administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent Service using a service id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindServiceByIdNonExistent() {
		SecRelSystem.findService(-1);
	}
	
	/**
	 * Attempt to instantiate a Service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindServiceByIdNullId() {
		SecRelSystem.findService((Integer) null);
	}
	
	/**
	 * Attempt to instantiate a Service using a service name.
	 */
	@Test
	public void testFindServiceByName() {
		Service r = SecRelSystem.findService(SecRelSystemServiceUnitTest.Administrator.getName());
		assertNotNull("System failed to return a Service instance for the administrator service", r);
		assertEquals("System return an invalid Service instance for the administrator service",
		        SecRelSystemServiceUnitTest.Administrator, r);
	}
	
	/**
	 * Attempt to instantiate a non existent Service using a service name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindServiceByNameNonExistent() {
		SecRelSystem.findService("nonexistent service");
	}
	
	/**
	 * Attempt to instantiate a Service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindServiceByNameNullName() {
		SecRelSystem.findService((String) null);
	}
	
	/**
	 * Queries the existence of the administrator service.
	 */
	@Test
	public void testHasService() {
		assertTrue("System did not acknowledge administrator service's existence",
		        SecRelSystem.hasService(SecRelSystemServiceUnitTest.Administrator));
	}
	
	/**
	 * Queries the existence of the administrator service.
	 */
	@Test
	public void testHasServiceById() {
		assertTrue("System did not acknowledge administrator service's existence",
		        SecRelSystem.hasService(SecRelSystemServiceUnitTest.Administrator.getId()));
	}
	
	/**
	 * Queries the existence of a non existent service.
	 */
	@Test
	public void testHasServiceByIdNonExistantService() {
		assertFalse("System claims existence of service with id -1", SecRelSystem.hasService(-1));
	}
	
	/**
	 * Queries the existence of the administrator service.
	 */
	@Test
	public void testHasServiceByName() {
		assertTrue("System did not acknowledge administrator service's existence",
		        SecRelSystem.hasService("administrator"));
	}
	
	/**
	 * Queries the existence of a non existent service.
	 */
	@Test
	public void testHasServiceByNameNonExistantService() {
		assertFalse("System claims existence of service with name 'nonexistent service'",
		        SecRelSystem.hasService("nonexistent service"));
	}
	
	/**
	 * Queries the existence of a non existent service.
	 */
	@Test
	public void testHasServiceNonExistantService() {
		// TODO: implement test
		fail("not implemented");
		// Service r = SecRelSystem.registerService("nonexistent service");
		// SecRelSystem.removeService(r);
		// assertFalse("System claims existence of service, nonexistent service,
		// which was removed",
		// SecRelSystem.hasService(r));
	}
	
	/**
	 * Attempts to create a new service, philosopher.
	 */
	@Test
	public void testRegisterService() {
		// TODO: implement test
		fail("not implemented");
		// Service r = SecRelSystem.registerService("philosopher");
		// assertNotNull("System failed to return a Service instance after the
		// philosopher service's creation", r);
		// assertTrue("The System failed to reflect the creation of the
		// philosopher service",
		// SecRelSystem.hasService(r));
	}
	
	/**
	 * Attempts to create a new service, teacher. This should fail because that
	 * service was already assigned to that service in setUpBeforeClass.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterServiceDuplicateName() {
		// TODO: implement test
		fail("not implemented");
		// SecRelSystem.registerService(SecRelSystemServiceUnitTest.Teacher.getName());
	}
	
	/**
	 * Attempts to create a new service using a null name.
	 */
	@Test(expected = NullPointerException.class)
	public void testRegisterServiceNullName() {
		SecRelSystem.registerService(null);
	}
	
	/**
	 * Attempt to remove the fake_service service.
	 */
	@Test
	public void testRemoveService() {
		SecRelSystem.removeService(FakeService);
		assertFalse("System claims existence of service, fake_service, which was removed",
		        SecRelSystem.hasService(FakeService));
	}
	
	/**
	 * Attempt to remove the fake_service service.
	 */
	@Test
	public void testRemoveServiceById() {
		SecRelSystem.removeService(FakeService.getId());
		assertFalse("System claims existence of service, fake_service, which was removed",
		        SecRelSystem.hasService(FakeService));
	}
	
	/**
	 * Attempts to remove a non existent service.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveServiceByIdNonExistantService() {
		SecRelSystem.removeService(-1);
	}
	
	/**
	 * Attempts to remove a service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveServiceByIdNullId() {
		SecRelSystem.removeService((Integer) null);
	}
	
	/**
	 * Attempt to remove the fake_service service.
	 */
	@Test
	public void testRemoveServiceByName() {
		SecRelSystem.removeService("fake_service");
		assertFalse("System claims existence of service, fake_service, which was removed",
		        SecRelSystem.hasService(FakeService));
	}
	
	/**
	 * Attempts to remove a non existent service.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveServiceByNameNonExistantService() {
		SecRelSystem.removeService("nonexistent service");
	}
	
	/**
	 * Attempts to remove a service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveServiceByNameNullName() {
		SecRelSystem.removeService((String) null);
	}
	
	/**
	 * Attempts to remove a non existent service.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveServiceNonExistantService() {
		// TODO: implement test
		fail("not implemented");
		// Service r = SecRelSystem.registerService("nonexistent service");
		// SecRelSystem.removeService(r);
		// SecRelSystem.removeService(r);
	}
	
	/**
	 * Attempts to remove a service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveServiceNullService() {
		SecRelSystem.removeService((Service) null);
	}
	
}