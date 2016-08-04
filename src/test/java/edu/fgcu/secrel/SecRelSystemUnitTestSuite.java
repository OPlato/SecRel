/**
 * This file defines the junit test suite to run all the unit tests for the
 * SecRelSystem class.
 */
package edu.fgcu.secrel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite runs all the SecRelSystem unit tests.
 *
 * @author lngibson
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
	SecRelSystemUserTest.class, SecRelSystemRoleTest.class, SecRelSystemMemberTest.class,
	SecRelSystemServiceTest.class, SecRelSystemAuthorizationTest.class
})
public class SecRelSystemUnitTestSuite {
	// Test suite requires no body
}