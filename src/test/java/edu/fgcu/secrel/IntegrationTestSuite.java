/**
 * TODO
 */
package edu.fgcu.secrel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <!-- TODO: AllTests comments -->
 *
 * @author lngibson
 *         
 */
@RunWith(Suite.class)
@SuiteClasses({
        SecRelSystemServiceIntegrationTest.class, SecRelSystemAuthorizationIntegrationTest.class
})
public class IntegrationTestSuite {
	// Test suite requires no body
}