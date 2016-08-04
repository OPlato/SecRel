/**
 * <p>
 * The SecRel system is a role-based application for protecting certain services
 * from being invoked.
 * </p>
 * <h2>Usage</h2>
 * <p>
 * To use this system, the security scheme must be set up using calls to
 * SecRelSystem.createUser, SecRelSystem.createRole and SecRelSystem.assignRole.
 * These functions create named users, named roles and assign certain users to
 * roles, respectively. The available Services must also be added to the system
 * by calls to SecRelSystem.registerService. To allow certain users to invoke a
 * role, one must call SecRelSystem.authorizeRole. This will authorize a
 * specified role to invoke a specific Service.
 * </p>
 * <p>
 * The main logic of the SecRel system is contained in two types, the
 * SecRelSystem class (the logic in that class has since been split up into 5
 * additional classes) and the Service abstract class. The SecRelSystem class
 * contains static methods for setting up and maintaining the user, roles,
 * services and their relationships. The Service class both serves to contain
 * logic for Service execution and as a template for different Service
 * implementations.
 * </p>
 * <h2>Status</h2>
 * <p>
 * The project is practically finished, however, more than half is still
 * untested.
 * </p>
 * <h2>Unfinished Sections</h2>
 * <p>
 * The Service functions in the SecRelSystem are mostly untested. A toy Service
 * has been implemented but is also untested.
 * </p>
 * <h3>Proposed</h3>
 * <p>
 * It was intended to have delegate methods in the Role and User classes to
 * create the illusion that they actually contained some logic.
 * </p>
 */
package edu.fgcu.secrel;