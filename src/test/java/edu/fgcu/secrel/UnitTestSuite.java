/**
 * This file defines the junit test suite to run all the unit tests for the
 * SecRel project.
 */
package edu.fgcu.secrel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite runs all the unit tests for the project.
 *
 * @author lngibson
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
	SecRelSystemUnitTestSuite.class
})
public class UnitTestSuite {
	// Test suite requires no body
}