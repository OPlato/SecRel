/*
 * This file defines the tests of SecRelSystem's service functions.
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
public class SecRelSystemServiceTest {

	/**
	 * Creates the users and services needed for testing that are not created or
	 * removed by tests.
	 *
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		// create services for testing
		SecRelSystemDebuggingUtil.verify();
	}

	/**
	 * Removes entities created by setUpBeforeClass.
	 *
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		SecRelSystemDebuggingUtil.verify();
		SecRelSystemDebuggingUtil.verify();
	}

	/**
	 * Creates the users and services needed for testing that may be created or
	 * removed by tests.
	 *
	 */
	@Before
	public void setUp() {
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
	}

	/**
	 * Attempt to retrieve a Service using a service id.
	 */
	@Test
	public void testFindServiceById() {
		fail("not implemented");
	}

	/**
	 * Attempt to retrieve a non existent Service using a service id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindServiceByIdNonExistent() {
		Services.findService(-1);
	}

	/**
	 * Attempt to retrieve a Service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindServiceByIdNullId() {
		Services.findService((Integer) null);
	}

	/**
	 * Attempt to retrieve a Service using a service name.
	 */
	@Test
	public void testFindServiceByName() {
		fail("not implemented");
	}

	/**
	 * Attempt to retrieve a non existent Service using a service name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindServiceByNameNonExistent() {
		Services.findService("nonexistent service");
	}

	/**
	 * Attempt to retrieve a Service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testFindServiceByNameNullName() {
		Services.findService((String) null);
	}

	/**
	 * Queries the existence of a service.
	 */
	@Test
	public void testHasService() {
		fail("not implemented");
	}

	/**
	 * Queries the existence of a service.
	 */
	@Test
	public void testHasServiceById() {
		fail("not implemented");
	}

	/**
	 * Queries the existence of a non existent service.
	 */
	@Test
	public void testHasServiceByIdNonExistantService() {
		assertFalse("System claims existence of service with id -1", Services.hasService(-1));
	}

	/**
	 * Queries the existence of a service.
	 */
	@Test
	public void testHasServiceByName() {
		fail("not implemented");
	}

	/**
	 * Queries the existence of a non existent service.
	 */
	@Test
	public void testHasServiceByNameNonExistantService() {
		assertFalse("System claims existence of service with name 'nonexistent service'",
				Services.hasService("nonexistent service"));
	}

	/**
	 * Queries the existence of a unregistered service.
	 */
	@Test
	public void testHasServiceNonExistantService() {
		fail("not implemented");
	}

	/**
	 * Attempts to register a new service.
	 */
	@Test
	public void testRegisterService() {
		fail("not implemented");
	}

	/**
	 * Attempts to register a service that has already been registered.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterServiceDuplicateService() {
		fail("not implemented");
	}

	/**
	 * Attempts to create a new service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRegisterServiceNullService() {
		Services.registerService(null);
	}

	/**
	 * Attempt to remove a service.
	 */
	@Test
	public void testRemoveService() {
		fail("not implemented");
	}

	/**
	 * Attempt to remove a service.
	 */
	@Test
	public void testRemoveServiceById() {
		fail("not implemented");
	}

	/**
	 * Attempts to remove a non existent service.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveServiceByIdNonExistantService() {
		Services.removeService(-1);
	}

	/**
	 * Attempts to remove a service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveServiceByIdNullId() {
		Services.removeService((Integer) null);
	}

	/**
	 * Attempt to remove a service.
	 */
	@Test
	public void testRemoveServiceByName() {
		fail("not implemented");
	}

	/**
	 * Attempts to remove a non existent service.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveServiceByNameNonExistantService() {
		Services.removeService("nonexistent service");
	}

	/**
	 * Attempts to remove a service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveServiceByNameNullName() {
		Services.removeService((String) null);
	}

	/**
	 * Attempts to remove a non existent service.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveServiceNonExistantService() {
		fail("not implemented");
	}

	/**
	 * Attempts to remove a service using a null reference.
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveServiceNullService() {
		Services.removeService((Service) null);
	}

}