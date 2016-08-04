/**
 * This file defines the SecRelSystemDebuggingUtil class. This class provides
 * functions for verifying the validity of the SecRelSystem's data
 */
package edu.fgcu.secrel;

import java.util.*;

/**
 * The SecRelSystemDebuggingUtil provides functions to verify that the
 * SecRelSystems data is kept in a valid state. There are four functions for
 * checking the existence of mapping long values. The main function is verify.
 * This verifies all the data in the system at once.
 *
 * @author lngibson
 *
 */
public class SecRelSystemDebuggingUtil {

	/**
	 * Checks for the existence of backward row entry in the backward member
	 * map.
	 *
	 * @param a
	 *            <p>
	 *            the user
	 *            </p>
	 *            <p>
	 *            This can be either a User instance, a String name or an
	 *            Integer id. Any other type will cause unpredictable
	 *            behavior<!-- most likely a class cast exception -->.
	 *            </p>
	 * @param b
	 *            <p>
	 *            the role
	 *            </p>
	 *            <p>
	 *            This can be either a User instance, a String name or an
	 *            Integer id. Any other type will cause unpredictable
	 *            behavior<!-- most likely a class cast exception -->.
	 *            </p>
	 * @return true if the backward row entry is present
	 */
	protected static boolean memberBackwardCheck(Object a, Object b) {
		Integer ai = a instanceof User ? ((User) a).getId()
				: a instanceof String ? SecRelSystem.userNames.get(a) : (Integer) a;
				Integer bi = b instanceof Role ? ((Role) b).getId()
						: b instanceof String ? SecRelSystem.roleNames.get(b) : (Integer) b;
						return SecRelSystem.memberBackwardMap.contains(SecRelSystem.memberBackwardRow(ai, bi));
	}

	/**
	 * Checks for the existence of forward row entry in the forward member map.
	 *
	 * @param a
	 *            <p>
	 *            the user
	 *            </p>
	 *            <p>
	 *            This can be either a User instance, a String name or an
	 *            Integer id. Any other type will cause unpredictable
	 *            behavior<!-- most likely a class cast exception -->.
	 *            </p>
	 * @param b
	 *            <p>
	 *            the role
	 *            </p>
	 *            <p>
	 *            This can be either a User instance, a String name or an
	 *            Integer id. Any other type will cause unpredictable
	 *            behavior<!-- most likely a class cast exception -->.
	 *            </p>
	 * @return true if the forward row entry is present
	 */
	protected static boolean memberForwardCheck(Object a, Object b) {
		Integer ai = a instanceof User ? ((User) a).getId()
				: a instanceof String ? SecRelSystem.userNames.get(a) : (Integer) a;
				Integer bi = b instanceof Role ? ((Role) b).getId()
						: b instanceof String ? SecRelSystem.roleNames.get(b) : (Integer) b;
						return SecRelSystem.memberForwardMap.contains(SecRelSystem.memberForwardRow(ai, bi));
	}

	/**
	 * Checks for the existence of backward row entry in the backward service
	 * map.
	 *
	 * @param a
	 *            <p>
	 *            the user
	 *            </p>
	 *            <p>
	 *            This can be either a Role instance, a String name or an
	 *            Integer id. Any other type will cause unpredictable
	 *            behavior<!-- most likely a class cast exception -->.
	 *            </p>
	 * @param b
	 *            <p>
	 *            the role
	 *            </p>
	 *            <p>
	 *            This can be either a Service instance, a String name or an
	 *            Integer id. Any other type will cause unpredictable
	 *            behavior<!-- most likely a class cast exception -->.
	 *            </p>
	 * @return true if the backward row entry is present
	 */
	protected static boolean serviceBackwardCheck(Object a, Object b) {
		Integer ai = a instanceof Role ? ((Role) a).getId()
				: a instanceof String ? SecRelSystem.roleNames.get(a) : (Integer) a;
				Integer bi = b instanceof Service ? ((Service) b).getId()
						: b instanceof String ? SecRelSystem.serviceNames.get(b) : (Integer) b;
						return SecRelSystem.serviceBackwardMap.containsKey(SecRelSystem.serviceBackwardRow(ai, bi));
	}

	/**
	 * Checks for the existence of forward row entry in the backward service
	 * map.
	 *
	 * @param a
	 *            <p>
	 *            the user
	 *            </p>
	 *            <p>
	 *            This can be either a Role instance, a String name or an
	 *            Integer id. Any other type will cause unpredictable
	 *            behavior<!-- most likely a class cast exception -->.
	 *            </p>
	 * @param b
	 *            <p>
	 *            the role
	 *            </p>
	 *            <p>
	 *            This can be either a Service instance, a String name or an
	 *            Integer id. Any other type will cause unpredictable
	 *            behavior<!-- most likely a class cast exception -->.
	 *            </p>
	 * @return true if the forward row entry is present
	 */
	protected static boolean serviceForwardCheck(Object a, Object b) {
		Integer ai = a instanceof Role ? ((Role) a).getId()
				: a instanceof String ? SecRelSystem.roleNames.get(a) : (Integer) a;
				Integer bi = b instanceof Service ? ((Service) b).getId()
						: b instanceof String ? SecRelSystem.serviceNames.get(b) : (Integer) b;
						return SecRelSystem.serviceForwardMap.containsKey(SecRelSystem.serviceForwardRow(ai, bi));
	}

	/**
	 * <p>
	 * Verifies that the system is in a consistent and valid state and throws an
	 * exception if it is not.
	 * </p>
	 * <p>
	 * This method will verify the inverse map pairs for users and roles and the
	 * composite map pair for services. It will also check the backward and
	 * forward maps for role assignments and authorizations.
	 * </p>
	 *
	 * @throws IllegalStateException if the system is found to be in an illegal
	 *             or inconsistent state
	 */
	protected static void verify() throws IllegalStateException {
		SecRelSystemDebuggingUtil.verifyInverseMap("User", SecRelSystem.userIds, SecRelSystem.userNames);
		SecRelSystemDebuggingUtil.verifyInverseMap("Role", SecRelSystem.roleIds, SecRelSystem.roleNames);
		SecRelSystemDebuggingUtil.verifyCompositeMap("Service", SecRelSystem.serviceNames, SecRelSystem.serviceIds);
		SecRelSystemDebuggingUtil.verifyInverseRelation("Role Assignment", SecRelSystem.memberForwardMap,
				SecRelSystem.memberBackwardMap, SecRelSystem.userIds.keySet(), SecRelSystem.roleIds.keySet());
		SecRelSystemDebuggingUtil.verifyInverseRelation("Role Authorization", SecRelSystem.serviceForwardMap.keySet(),
				SecRelSystem.serviceBackwardMap.keySet(), SecRelSystem.roleIds.keySet(),
				SecRelSystem.serviceIds.keySet());
	}

	/**
	 * <p>
	 * Throws an exception if an composite map pair is not in a consistent and
	 * valid state.
	 * </p>
	 * <p>
	 * This method will compare the length of the two maps, check that every id
	 * is present exactly once in each map and that the entities contain valid
	 * ids and names.
	 * </p>
	 *
	 * @param type the name of the maps to use in the exception's message
	 * @param names the name to id map
	 * @param ids the id to entity map
	 */
	protected static <T extends Entity> void verifyCompositeMap(String type, Map<String, Integer> names,
			Map<Integer, T> ids) {
		// check if ids and names have equal sizes
		if (ids.size() != names.size())
			throw new IllegalStateException(String.format("%s ids map and %s names map have different sizes: %d <> %d",
					type, type, ids.size(), names.size()));
		// check if first and second maps are consistent
		for (String name : names.keySet()) {
			Integer id = names.get(name);
			// check if names contains name
			if (!ids.containsKey(id))
				throw new IllegalStateException(
						String.format("%s names map contains an id not in the %s ids map: %d", type, type, id));
			// check if entity name is consistent
			T entity = ids.get(id);
			if (!name.equals(entity.getName()))
				throw new IllegalStateException(
						String.format("%s names map sends %s to %d but %s ids map sends %d to %s", type, name, id, type,
								id, entity.getName()));
		}
		// check if second map is consistent
		for (Integer id : ids.keySet()) {
			T entity = ids.get(id);
			// check if entity id is consistent
			if (!id.equals(entity.getId()))
				throw new IllegalStateException(String.format("%s ids map sends %d to entity %s with id %d", type, id,
						entity.getName(), entity.getId()));
		}
	}

	/**
	 * <p>
	 * Throws an exception if an inverse map pair is not in a consistent and
	 * valid state.
	 * </p>
	 * <p>
	 * This method will compare the length of the two maps, check that every
	 * element is present exactly once in each map and that the mappings are
	 * inverses of each other.
	 * </p>
	 *
	 * @param type the name of the maps to use in the exception's message
	 * @param ids the id to name map
	 * @param names the name to id map
	 */
	protected static void verifyInverseMap(String type, Map<Integer, String> ids, Map<String, Integer> names) {
		// check if ids and names have equal sizes
		if (ids.size() != names.size())
			throw new IllegalStateException(String.format("%s ids map and %s names map have different sizes: %d <> %d",
					type, type, ids.size(), names.size()));
		// check if forward and backward maps are consistent
		for (Integer id : ids.keySet()) {
			String name = ids.get(id);
			// check if names contains name
			if (!names.containsKey(name))
				throw new IllegalStateException(
						String.format("%s ids map contains a name not in the %s names map: %s", type, type, name));
			// check if inverse is consistent
			Integer rid = names.get(name);
			if (!id.equals(rid))
				throw new IllegalStateException(String.format(
						"%s ids map sends %d to %s but %s names map sends %s to %d", type, id, name, type, name, rid));
		}
		for (String name : names.keySet()) {
			Integer id = names.get(name);
			// check if ids contains id
			if (!ids.containsKey(id))
				throw new IllegalStateException(
						String.format("%s names map contains an id not in the %s ids map: %d", type, type, id));
			// check if inverse is consistent
			String rname = ids.get(id);
			if (!name.equals(rname))
				throw new IllegalStateException(String.format(
						"%s names map sends %s to %d but %s ids map sends %d to %s", type, name, id, type, id, rname));
		}
	}

	/**
	 * <p>
	 * Throws an exception if an inverse relation pair is not in a consistent
	 * and valid state.
	 * </p>
	 * <p>
	 * This method will compare the length of the two maps and that the mappings
	 * are inverses of each other.
	 * </p>
	 *
	 * @param type the name of the relations to use in the exception's message
	 * @param forward the relation from A to B
	 * @param backward the relation from B to A
	 * @param A the set of the first element of forward and the second element
	 *            of backward
	 * @param B the set of the second element of forward and the first element
	 *            of backward
	 */
	protected static void verifyInverseRelation(String type, Set<Long> forward, Set<Long> backward, Set<Integer> A,
			Set<Integer> B) {
		// check if forward and backward have equal sizes
		if (forward.size() != backward.size())
			throw new IllegalStateException(
					String.format("%s forward relation and %s backward relation have different sizes: %d <> %d", type,
							type, forward.size(), backward.size()));
		for (Long row : forward) {
			Integer a = (int) (row >> 32), b = (int) (row & 0xffffffff);
			// check if a is contained in A
			if (!A.contains(a))
				throw new IllegalStateException(
						String.format("%s forward relation contains an first coordinate not in domain: %d", type, a));
			// check if b is contained in B
			if (!B.contains(b))
				throw new IllegalStateException(
						String.format("%s forward relation contains an second coordinate not in range: %d", type, b));
			// check if inverse is in backward
			Long brow = b.longValue() << 32 | a;
			if (!backward.contains(brow))
				throw new IllegalStateException(String.format(
						"%s forward relation contains row (%d,%d) but %s backward relation does not contain inverse: %d",
						type, a, b, type));
		}
		for (Long row : backward) {
			Integer b = (int) (row >> 32), a = (int) (row & 0xffffffff);
			// check if a is contained in A
			if (!A.contains(a))
				throw new IllegalStateException(
						String.format("%s backward relation contains an first coordinate not in domain: %d", type, a));
			// check if b is contained in B
			if (!B.contains(b))
				throw new IllegalStateException(
						String.format("%s backward relation contains an second coordinate not in range: %d", type, b));
			// check if inverse is in backward
			Long brow = b.longValue() << 32 | a;
			if (!backward.contains(brow))
				throw new IllegalStateException(String.format(
						"%s backward relation contains row (%d,%d) but %s forward relation does not contain inverse: %d",
						type, b, a, type));
		}
	}

}