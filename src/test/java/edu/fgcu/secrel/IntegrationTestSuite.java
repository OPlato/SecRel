/**
 * This file defines the junit test suite to run all the integration tests for
 * the SecRel project.
 */
package edu.fgcu.secrel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite runs all the integration tests for the project.
 *
 * @author lngibson
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
        SecRelSystemInvocationTest.class
})
public class IntegrationTestSuite {
	// Test suite requires no body
}