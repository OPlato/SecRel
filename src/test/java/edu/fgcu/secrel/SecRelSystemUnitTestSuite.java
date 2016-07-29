/**
 * TODO
 */
package edu.fgcu.secrel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite runs all the SecRelSystem tests.
 *
 * @author lngibson
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
        SecRelSystemRoleTest.class, SecRelSystemUserTest.class, SecRelSystemAssignmentTest.class,
        SecRelSystemServiceUnitTest.class, SecRelSystemAuthorizationUnitTest.class
})
public class SecRelSystemUnitTestSuite {
	// Test suite requires no body
}