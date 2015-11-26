package j2s;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ReflectionEvaluation {
	public static void main(String[] args) {
		// generateMethods("java.lang.String");
		// generateMethods("java.util.ArrayList");
		// generateMethods("java.util.HashMap");
//		generateMethods("java.lang.Integer");
		// runMethods();
		String filename =
				 "C:\\Users\\Sanchit\\Downloads\\test.txt";
		BufferedReader br = null;
		boolean test = false;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));
			
			while ((sCurrentLine = br.readLine()) != null) {	
				sCurrentLine = sCurrentLine.trim();
				if (sCurrentLine.contains("Google Rank")) {
					test = false;
				}
				if (test) {
					if (sCurrentLine != "\r\n")
					System.out.println(sCurrentLine);
				}
				if (sCurrentLine.contains("Pre-rank")) {
					test = true;
				}
				
			}
		} catch(Exception e) {
			
		}
	}

	public static void runMethods() {
		String test = "This is a test for native2native.";
		System.out.println(equals_Wrapper(test));
		System.out.println(toString_Wrapper(test));
		System.out.println(compareTo_Wrapper(test, test));
		System.out.println(indexOf_Wrapper(test, "test", 0));
		System.out.println(indexOf_Wrapper(test, "test"));
		System.out.println(valueOf_Wrapper(true));
		System.out.println(valueOf_Wrapper(0.0));
		System.out.println(valueOf_Wrapper(0));
		System.out.println(valueOf_Wrapper(test));
		System.out.println(length_Wrapper(test));
		System.out.println(isEmpty_Wrapper(test));
		System.out.println(charAt_Wrapper(test, 0));
		System.out.println(equalsIgnoreCase_Wrapper(test, "test"));
		System.out.println(compareToIgnoreCase_Wrapper(test, "test"));
		System.out.println(startsWith_Wrapper(test, "test"));
		System.out.println(startsWith_Wrapper(test, "test", 0));
		System.out.println(endsWith_Wrapper(test, "test"));
		System.out.println(substring_Wrapper(test, 0));
		System.out.println(substring_Wrapper(test, 0, 1));
		System.out.println(concat_Wrapper(test, "test"));
		System.out.println(replaceAll_Wrapper(test, "test", "test2"));
		System.out.println(replaceFirst_Wrapper(test, "test", "test1"));
		System.out.println(toLowerCase_Wrapper(test));
		System.out.println(toUpperCase_Wrapper(test));
		System.out.println(trim_Wrapper(test));
	}

	public static void generateMethods(String className) {
		try {
			Class<?> c = Class.forName(className);

			Method[] allMethods = c.getDeclaredMethods();
			for (Method m : allMethods) {
				String mName = m.getName();
				if (mName.contains("hashCode")) {
					continue;
				}

				String rType = m.getReturnType().getSimpleName();
				if (rType.contains("[]")) {
					continue;
				}
				// System.out.println(m);
				int numParams = m.getParameterTypes().length;
				// if (mname.contains("UpperCase")) {
				Type[] pType = m.getGenericParameterTypes();

				Object[] args = new Object[numParams];
				if (numParams != 0) {
					for (int i = 0; i < numParams; i++) {
						String paramType = pType[i].toString();
						args[i] = processParameters(paramType);
					}
				} else {
					args = null;
				}

				try {
					m.setAccessible(true);

					String argumentClassNames = "";
					String arguments = "";

					if (className.contains("String")) {
						if (args != null) {
							for (int i = 0; i < args.length; i++) {

								if (i + 1 == args.length) {
									if (args[i].getClass().getSimpleName().equals("String")) {
										arguments += ("\"" + args[i].toString() + "\"");
									} else {
										arguments += (args[i].toString());
									}
									argumentClassNames += (args[i].getClass().getSimpleName() + " " + (char) (i + 66));
								} else {
									if (args[i].getClass().getSimpleName().equals("String")) {
										arguments += ("\"" + args[i].toString() + "\", ");
									} else {
										arguments += (args[i].toString() + ", ");
									}
									argumentClassNames += (args[i].getClass().getSimpleName() + " " + (char) (i + 66) + ", ");
								}

							}
						}

						System.out.printf("public static %s %s_Wrapper(%s) {\n" + "\tSystem.out.println(\"%s_Wrapper testing\");\n"
								+ "\tString %sString = A;\n" + "\treturn %sString.%s(%s);\n" + "}\n\n", rType, mName, argumentClassNames, mName,
								mName, mName, mName, arguments);
					} else if (className.contains("ArrayList")) {
						if (args != null) {
							for (int i = 0; i < args.length; i++) {
								if (i + 1 == args.length) {
									arguments += (char) (i + 66);
									argumentClassNames += (args[i].getClass().getSimpleName() + " " + (char) (i + 66));
								} else {
									arguments += (char) (i + 66) + ", ";
									argumentClassNames += (args[i].getClass().getSimpleName() + " " + (char) (i + 66) + ", ");
								}

							}
						}
						System.out.printf("public static ArrayList<String> %s_Wrapper(ArrayList<String> A, %s) {\n"
								+ "\tSystem.out.println(\"%s_Wrapper testing\");\n" + "\tArrayList<String> %sArrayList = A;\n"
								+ "\t%sArrayList.%s(%s);\n" + "\treturn %sArrayList;\n" + "}\n\n", mName, argumentClassNames, mName, mName, mName,
								mName, arguments, mName);
					} else if (className.contains("HashMap")) {
						if (args != null) {
							for (int i = 0; i < args.length; i++) {
								if (i + 1 == args.length) {
									arguments += (char) (i + 66);
									argumentClassNames += ", " + (args[i].getClass().getSimpleName() + " " + (char) (i + 66));
								} else {
									arguments += (char) (i + 66) + ", ";
									argumentClassNames += ", " + (args[i].getClass().getSimpleName() + " " + (char) (i + 66));
								}

							}
						}
						System.out.printf("public static HashMap<String, String> %s_Wrapper(HashMap<String, String> A%s) {\n"
								+ "\tSystem.out.println(\"%s_Wrapper testing\");\n" + "\tHashMap<String, String> %sHashMap = A;\n"
								+ "\t%sHashMap.%s(%s);\n" + "\treturn %sHashMap;\n" + "}\n\n", mName, argumentClassNames, mName, mName, mName, mName,
								arguments, mName);
					}

					// Handle any exceptions thrown by method to be
					// invoked.
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					continue;
				}
			}

			// production code should handle these exceptions more gracefully
		} catch (ClassNotFoundException x) {
			x.printStackTrace();
		}
	}

	private static Object processParameters(String paramType) {
		// TODO Auto-generated method stub
		Random rand = new Random();
		if (paramType.contains("String")) {
			return "test";
		} else if (paramType.contains("int")) {
			return rand.nextInt(2);
		} else if (paramType.contains("boolean")) {
			return true;
		} else if (paramType.contains("long") || paramType.contains("float") || paramType.contains("double")) {
			return 0.0;
		}
		return "This is a test for native2native.";
	}

	/*
	 * Compares this string to the specified object. The result is true if and
	 * only if the argument is not null and is a String object that represents
	 * the same sequence of characters as this object.
	 */
	public static boolean equals_Wrapper(String A) {
		System.out.println("equals_Wrapper testing");
		String equalsString = "";
		return equalsString.equals(A);
	}

	/*
	 * This object (which is already a string!) is itself returned.
	 */
	public static String toString_Wrapper(String A) {
		System.out.println("toString_Wrapper testing");
		String toString = A;
		return toString.toString();
	}

	/*
	 * Compares two strings lexicographically. The comparison is based on the
	 * Unicode value of each character in the strings. The character sequence
	 * represented by this String object is compared lexicographically to the
	 * character sequence represented by the argument string. The result is a
	 * negative integer if this String object lexicographically precedes the
	 * argument string. The result is a positive integer if this String object
	 * lexicographically follows the argument string. The result is zero if the
	 * strings are equal; compareTo returns 0 exactly when the equals(Object)
	 * method would return true.
	 */
	public static int compareTo_Wrapper(String A, String B) {
		System.out.println("compareTo_Wrapper testing");
		String compareToString = A;
		return compareToString.compareTo(B);
	}

	/*
	 * Returns the index within this string of the first occurrence of the
	 * specified substring, starting at the specified index.
	 */
	public static int indexOf_Wrapper(String A, String B, Integer C) {
		System.out.println("indexOfStartingAt_Wrapper testing");
		String indexOfStartingAtString = A;
		return indexOfStartingAtString.indexOf(B, C);
	}

	/*
	 * Returns the index within this string of the first occurrence of the
	 * specified substring.
	 */
	public static int indexOf_Wrapper(String A, String B) {
		System.out.println("indexOfSubstring_Wrapper testing");
		String indexOfSubstringString = A;
		return indexOfSubstringString.indexOf(B);
	}

	/*
	 * Returns the string representation of the String argument.
	 */
	public static String valueOf_Wrapper(String A) {
		System.out.println("valueOfString_Wrapper testing");
		String valueOfString = A;
		return String.valueOf(valueOfString);
	}

	/*
	 * Returns the string representation of the Boolean argument.
	 */
	public static String valueOf_Wrapper(Boolean A) {
		System.out.println("valueOfBoolean_Wrapper testing");
		Boolean valueOfBoolean = A;
		return String.valueOf(valueOfBoolean);
	}

	/*
	 * Returns the string representation of the Integer argument.
	 */
	public static String valueOf_Wrapper(Integer A) {
		System.out.println("valueOfInteger_Wrapper testing");
		Integer valueOfInteger_Wrapper = A;
		return String.valueOf(valueOfInteger_Wrapper);
	}

	/*
	 * Returns the string representation of the Double argument.
	 */
	public static String valueOf_Wrapper(Double A) {
		System.out.println("valueOfDouble_Wrapper testing");
		Double valueOfDouble_Wrapper = A;
		return String.valueOf(valueOfDouble_Wrapper);
	}

	/*
	 * Returns the length of this string. The length is equal to the number of
	 * Unicode code units in the string.
	 */
	public static int length_Wrapper(String A) {
		System.out.println("length_Wrapper testing");
		String lengthString = A;
		return lengthString.length();
	}

	/*
	 * Returns true if, and only if, length() is 0.
	 */
	public static boolean isEmpty_Wrapper(String A) {
		System.out.println("isEmpty_Wrapper testing");
		String isEmptyString = A;
		return isEmptyString.isEmpty();
	}

	/*
	 * Returns the char value at the specified index. An index ranges from 0 to
	 * length() - 1. The first char value of the sequence is at index 0, the
	 * next at index 1, and so on, as for array indexing.
	 */
	public static char charAt_Wrapper(String A, Integer B) {
		System.out.println("charAt_Wrapper testing");
		String charAtString = A;
		return charAtString.charAt(B);
	}

	/*
	 * Compares this String to another String, ignoring case considerations. Two
	 * strings are considered equal ignoring case if they are of the same length
	 * and corresponding characters in the two strings are equal ignoring case.
	 */
	public static boolean equalsIgnoreCase_Wrapper(String A, String B) {
		System.out.println("equalsIgnoreCase_Wrapper testing");
		String equalsIgnoreCaseString = A;
		return equalsIgnoreCaseString.equalsIgnoreCase(B);
	}

	/*
	 * Compares two strings lexicographically, ignoring case differences. This
	 * method returns an integer whose sign is that of calling compareTo with
	 * normalized versions of the strings where case differences have been
	 * eliminated by calling
	 * Character.toLowerCase(Character.toUpperCase(character)) on each
	 * character.
	 */
	public static int compareToIgnoreCase_Wrapper(String A, String B) {
		System.out.println("compareToIgnoreCase_Wrapper testing");
		String compareToIgnoreCaseString = A;
		return compareToIgnoreCaseString.compareToIgnoreCase(B) > 0 ? 1 : 0;
	}

	/*
	 * Tests if the substring of this string beginning at the specified index
	 * starts with the specified prefix.
	 */
	public static boolean startsWith_Wrapper(String A, String B, Integer C) {
		System.out.println("startsWith_index_Wrapper testing");
		String startsWithIndexString = A;
		return startsWithIndexString.startsWith(B, C);
	}

	/*
	 * Tests if this string starts with the specified prefix.
	 */
	public static boolean startsWith_Wrapper(String A, String B) {
		System.out.println("startsWith_Wrapper testing");
		String startsWithString = A;
		return startsWithString.startsWith(B);
	}

	/*
	 * Tests if this string ends with the specified suffix.
	 */
	public static boolean endsWith_Wrapper(String A, String B) {
		System.out.println("endsWith_Wrapper testing");
		String endsWithString = A;
		return endsWithString.endsWith(B);
	}

	/*
	 * Returns a new string that is a substring of this string. The substring
	 * begins at the specified beginIndex and extends to the character at index
	 * endIndex - 1. Thus the length of the substring is endIndex-beginIndex.
	 */
	public static String substring_Wrapper(String A, Integer B, Integer C) {
		System.out.println("substring_begins_ends_Wrapper testing");
		String substringBeginsEndsString = A;
		return substringBeginsEndsString.substring(B, C);
	}

	/*
	 * Returns a new string that is a substring of this string. The substring
	 * begins with the character at the specified index and extends to the end
	 * of this string.
	 */
	public static String substring_Wrapper(String A, Integer B) {
		System.out.println("substring_begins_Wrapper testing");
		String substringBeginsString = A;
		return substringBeginsString.substring(B);
	}

	/*
	 * Concatenates the specified string to the end of this string.
	 * 
	 * If the length of the argument string is 0, then this String object is
	 * returned. Otherwise, a new String object is created, representing a
	 * character sequence that is the concatenation of the character sequence
	 * represented by this String object and the character sequence represented
	 * by the argument string.
	 */
	public static String concat_Wrapper(String A, String B) {
		System.out.println("concat_Wrapper testing");
		String concatString = A;
		return concatString.concat(B);
	}

	/*
	 * Replaces the first substring of this string that matches the given
	 * regular expression with the given replacement.
	 */
	public static String replaceFirst_Wrapper(String A, String B, String C) {
		System.out.println("replaceFirst_Wrapper testing");
		String replaceFirstString = A;
		return replaceFirstString.replaceFirst(B, C);
	}

	/*
	 * Replaces each substring of this string that matches the given regular
	 * expression with the given replacement.
	 */
	public static String replaceAll_Wrapper(String A, String B, String C) {
		System.out.println("replaceAll_Wrapper testing");
		String replaceAllString = A;
		return replaceAllString.replaceAll(B, C);
	}

	/*
	 * Converts all of the characters in this String to lower case using the
	 * rules of the default locale. This is equivalent to calling
	 * toLowerCase(Locale.getDefault()).
	 */
	public static String toLowerCase_Wrapper(String A) {
		System.out.println("toLowerCase_Wrapper testing");
		String toLowerCaseString = A;
		return toLowerCaseString.toLowerCase();
	}

	/*
	 * Converts all of the characters in this String to upper case using the
	 * rules of the default locale. This method is equivalent to
	 * toUpperCase(Locale.getDefault()).
	 */
	public static String toUpperCase_Wrapper(String A) {
		System.out.println("toUpperCase_Wrapper testing");
		String toUpperCaseString = A;
		return toUpperCaseString.toUpperCase();
	}

	/*
	 * Returns a copy of the string, with leading and trailing whitespace
	 * omitted.
	 */
	public static String trim_Wrapper(String A) {
		System.out.println("trim_Wrapper testing");
		String trimString = A;
		return trimString.trim();
	}

	/*
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 */
	public static ArrayList<String> add_Wrapper(ArrayList<String> A, Integer B, String C) {
		System.out.println("addToIndex_Wrapper testing");
		ArrayList<String> addToIndexArrayList = A;
		addToIndexArrayList.add(B, C);
		return addToIndexArrayList;
	}

	/*
	 * Appends the specified element to the end of this list.
	 */
	public static ArrayList<String> add_Wrapper(ArrayList<String> A, String B) {
		System.out.println("addToEnd_Wrapper testing");
		ArrayList<String> addToEndArrayList = A;
		addToEndArrayList.add(B);
		return addToEndArrayList;
	}

	/*
	 * Removes the element at the specified position in this list. Shifts any
	 * subsequent elements to the left (subtracts one from their indices).
	 */
	public static ArrayList<String> remove_Wrapper(ArrayList<String> A, Integer B) {
		System.out.println("removeAtIndex_Wrapper testing");
		ArrayList<String> removeAtIndexArrayList = A;
		removeAtIndexArrayList.remove(B.intValue());
		return removeAtIndexArrayList;
	}

	/*
	 * Removes the first occurrence of the specified element from this list, if
	 * it is present.
	 */
	public static ArrayList<String> remove_Wrapper(ArrayList<String> A, String B) {
		System.out.println("remove_Wrapper testing");
		ArrayList<String> removeArrayList = A;
		removeArrayList.remove(B);
		return removeArrayList;
	}

	/*
	 * Returns the element at the specified position in this list.
	 */
	public static String get_Wrapper(ArrayList<String> A, Integer B) {
		System.out.println("get_Wrapper testing");
		ArrayList<String> getArrayList = A;
		return getArrayList.get(B);
	}

	/*
	 * Returns a shallow copy of this ArrayList instance. (The elements
	 * themselves are not copied.)
	 */
	public static ArrayList<String> clone_Wrapper(ArrayList<String> A) {
		System.out.println("clone_Wrapper testing");
		ArrayList<String> cloneArrayList = A;
		cloneArrayList.clone();
		return cloneArrayList;
	}

	/*
	 * Returns the index of the first occurrence of the specified element in
	 * this list, or -1 if this list does not contain the element.
	 */
	public static Integer indexOf_Wrapper(ArrayList<String> A, String B) {
		System.out.println("indexOf_Wrapper testing");
		ArrayList<String> indexOfArrayList = A;
		return indexOfArrayList.indexOf(B);
	}

	/*
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public static ArrayList<String> clear_Wrapper(ArrayList<String> A) {
		System.out.println("clear_Wrapper testing");
		ArrayList<String> clearArrayList = A;
		clearArrayList.clear();
		return clearArrayList;
	}

	/*
	 * Returns true if this list contains no elements.
	 */
	public static boolean isEmpty_Wrapper(ArrayList<String> A) {
		System.out.println("isEmpty_Wrapper testing");
		ArrayList<String> isEmptyArrayList = A;
		return isEmptyArrayList.isEmpty();
	}

	/*
	 * Returns the index of the last occurrence of the specified element in this
	 * list, or -1 if this list does not contain the element. More formally,
	 * returns the highest index i such that (o==null ? get(i)==null :
	 * o.equals(get(i))), or -1 if there is no such index.
	 */
	public static Integer lastIndexOf_Wrapper(ArrayList<String> A, String B) {
		System.out.println("lastIndexOf_Wrapper testing");
		ArrayList<String> lastIndexOfArrayList = A;
		return lastIndexOfArrayList.lastIndexOf(B);
	}

	/*
	 * Returns true if this list contains the specified element
	 */
	public static Boolean contains_Wrapper(ArrayList<String> A, String B) {
		System.out.println("contains_Wrapper testing");
		ArrayList<String> containsArrayList = A;
		return containsArrayList.contains(B);
	}

	/*
	 * Returns the number of elements in this list.
	 */
	public static Integer size_Wrapper(ArrayList<String> A) {
		System.out.println("size_Wrapper testing");
		ArrayList<String> sizeArrayList = A;
		return sizeArrayList.size();
	}

	/*
	 * Returns a view of the portion of this list between the specified
	 * fromIndex, inclusive, and toIndex, exclusive. (If fromIndex and toIndex
	 * are equal, the returned list is empty.) The returned list is backed by
	 * this list, so non-structural changes in the returned list are reflected
	 * in this list, and vice-versa. The returned list supports all of the
	 * optional list operations.
	 */
	public static List<String> subList_Wrapper(ArrayList<String> A, Integer B, Integer C) {
		System.out.println("subList_Wrapper testing");
		ArrayList<String> subListArrayList = A;
		return subListArrayList.subList(B, C);
	}

	/*
	 * Appends all of the elements in the specified collection to the end of
	 * this list, in the order that they are returned by the specified
	 * collection's Iterator. The behavior of this operation is undefined if the
	 * specified collection is modified while the operation is in progress.
	 * (This implies that the behavior of this call is undefined if the
	 * specified collection is this list, and this list is nonempty.)
	 */
	public static boolean addAll_Wrapper(ArrayList<String> A, ArrayList<String> B) {
		System.out.println("addAll_Wrapper testing");
		ArrayList<String> addAllArrayList = A;
		return addAllArrayList.addAll(B);
	}

	/*
	 * nserts all of the elements in the specified collection into this list,
	 * starting at the specified position. Shifts the element currently at that
	 * position (if any) and any subsequent elements to the right (increases
	 * their indices). The new elements will appear in the list in the order
	 * that they are returned by the specified collection's iterator.
	 */
	public static boolean addAll_Wrapper(ArrayList<String> A, Integer B, ArrayList<String> C) {
		System.out.println("addAllAtIndex_Wrapper testing");
		ArrayList<String> addAllAtIndexArrayList = A;
		return addAllAtIndexArrayList.addAll(B, C);
	}

	/*
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 */
	public static ArrayList<String> set_Wrapper(ArrayList<String> A, Integer B, String C) {
		System.out.println("set_Wrapper testing");
		ArrayList<String> setArrayList = A;
		setArrayList.set(B, C);
		return setArrayList;
	}

	/*
	 * Increases the capacity of this ArrayList instance, if necessary, to
	 * ensure that it can hold at least the number of elements specified by the
	 * minimum capacity argument.
	 */
	public static ArrayList<String> ensureCapacity_Wrapper(ArrayList<String> A, Integer B) {
		System.out.println("ensureCapacity_Wrapper testing");
		ArrayList<String> ensureCapacityArrayList = A;
		ensureCapacityArrayList.ensureCapacity(B);
		return ensureCapacityArrayList;
	}

	/*
	 * Trims the capacity of this ArrayList instance to be the list's current
	 * size. An application can use this operation to minimize the storage of an
	 * ArrayList instance.
	 */
	public static ArrayList<String> trimToSize_Wrapper(ArrayList<String> A) {
		System.out.println("trimToSize_Wrapper testing");
		ArrayList<String> trimToSizeArrayList = A;
		trimToSizeArrayList.trimToSize();
		return trimToSizeArrayList;
	}

	/*
	 * Removes from this list all of its elements that are contained in the
	 * specified collection.
	 */
	public static ArrayList<String> removeAll_Wrapper(ArrayList<String> A, ArrayList<String> B) {
		System.out.println("removeAll_Wrapper testing");
		ArrayList<String> removeAllArrayList = A;
		removeAllArrayList.removeAll(B);
		return removeAllArrayList;
	}

	/*
	 * Retains only the elements in this list that are contained in the
	 * specified collection. In other words, removes from this list all of its
	 * elements that are not contained in the specified collection.
	 */
	public static ArrayList<String> retainAll_Wrapper(ArrayList<String> A, ArrayList<String> B) {
		System.out.println("retainAll_Wrapper testing");
		ArrayList<String> retainAllArrayList = A;
		retainAllArrayList.retainAll(B);
		return retainAllArrayList;
	}

	/*
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence), starting at the specified position in the list
	 */
	public static ArrayList<String> listIterator_Wrapper(ArrayList<String> A, Integer B) {
		System.out.println("listIteratorFromIndex_Wrapper testing");
		ArrayList<String> listIteratorFromIndexArrayList = A;
		listIteratorFromIndexArrayList.listIterator(B);
		return listIteratorFromIndexArrayList;
	}

	/*
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence).
	 */
	public static ArrayList<String> listIterator_Wrapper(ArrayList<String> A) {
		System.out.println("listIterator_Wrapper testing");
		ArrayList<String> listIteratorArrayList = A;
		listIteratorArrayList.listIterator();
		return listIteratorArrayList;
	}

	/*
	 * Removes the mapping for the specified key from this map if present.
	 */
	public static HashMap<String, String> remove_Wrapper(HashMap<String, String> A, String B) {
		System.out.println("remove_Wrapper testing");
		HashMap<String, String> removeHashMap = A;
		removeHashMap.remove(B);
		return removeHashMap;
	}

	/*
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key.
	 */
	public static HashMap<String, String> get_Wrapper(HashMap<String, String> A, String B) {
		System.out.println("get_Wrapper testing");
		HashMap<String, String> getHashMap = A;
		getHashMap.get(B);
		return getHashMap;
	}

	/*
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for the key, the old value is
	 * replaced.
	 */
	public static HashMap<String, String> put_Wrapper(HashMap<String, String> A, String B, String C) {
		System.out.println("put_Wrapper testing");
		HashMap<String, String> putHashMap = A;
		putHashMap.put(B, C);
		return putHashMap;
	}

	/*
	 * Returns a Collection view of the values contained in this map. The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa. If the map is modified while an iteration
	 * over the collection is in progress (except through the iterator's own
	 * remove operation), the results of the iteration are undefined. The
	 * collection supports element removal, which removes the corresponding
	 * mapping from the map, via the Iterator.remove, Collection.remove,
	 * removeAll, retainAll and clear operations. It does not support the add or
	 * addAll operations
	 */
	public static HashMap<String, String> values_Wrapper(HashMap<String, String> A) {
		System.out.println("values_Wrapper testing");
		HashMap<String, String> valuesHashMap = A;
		valuesHashMap.values();
		return valuesHashMap;
	}

	/*
	 * Returns a shallow copy of this HashMap instance: the keys and values
	 * themselves are not cloned.
	 */
	public static HashMap<String, String> clone_Wrapper(HashMap<String, String> A) {
		System.out.println("clone_Wrapper testing");
		HashMap<String, String> cloneHashMap = A;
		cloneHashMap.clone();
		return cloneHashMap;
	}

	/*
	 * Removes all of the mappings from this map. The map will be empty after
	 * this call returns.
	 */
	public static HashMap<String, String> clear_Wrapper(HashMap<String, String> A) {
		System.out.println("clear_Wrapper testing");
		HashMap<String, String> clearHashMap = A;
		clearHashMap.clear();
		return clearHashMap;
	}

	/*
	 * Returns true if this map contains no key-value mappings.
	 */
	public static HashMap<String, String> isEmpty_Wrapper(HashMap<String, String> A) {
		System.out.println("isEmpty_Wrapper testing");
		HashMap<String, String> isEmptyHashMap = A;
		isEmptyHashMap.isEmpty();
		return isEmptyHashMap;
	}

	/*
	 * Returns the number of key-value mappings in this map.
	 */
	public static HashMap<String, String> size_Wrapper(HashMap<String, String> A) {
		System.out.println("size_Wrapper testing");
		HashMap<String, String> sizeHashMap = A;
		sizeHashMap.size();
		return sizeHashMap;
	}

	/*
	 * Returns a Set view of the mappings contained in this map.
	 */
	public static HashMap<String, String> entrySet_Wrapper(HashMap<String, String> A) {
		System.out.println("entrySet_Wrapper testing");
		HashMap<String, String> entrySetHashMap = A;
		entrySetHashMap.entrySet();
		return entrySetHashMap;
	}

	/*
	 * Copies all of the mappings from the specified map to this map. These
	 * mappings will replace any mappings that this map had for any of the keys
	 * currently in the specified map.
	 */
	public static HashMap<String, String> putAll_Wrapper(HashMap<String, String> A, Map<String, String> B) {
		System.out.println("putAll_Wrapper testing");
		HashMap<String, String> putAllHashMap = A;
		putAllHashMap.putAll(B);
		return putAllHashMap;
	}

	/*
	 * Returns a Set view of the keys contained in this map.
	 */
	public static HashMap<String, String> keySet_Wrapper(HashMap<String, String> A) {
		System.out.println("keySet_Wrapper testing");
		HashMap<String, String> keySetHashMap = A;
		keySetHashMap.keySet();
		return keySetHashMap;
	}

	/*
	 * Returns true if this map maps one or more keys to the specified value.
	 */
	public static HashMap<String, String> containsValue_Wrapper(HashMap<String, String> A, String B) {
		System.out.println("containsValue_Wrapper testing");
		HashMap<String, String> containsValueHashMap = A;
		containsValueHashMap.containsValue(B);
		return containsValueHashMap;
	}

	/*
	 * Returns true if this map contains a mapping for the specified key.
	 */
	public static HashMap<String, String> containsKey_Wrapper(HashMap<String, String> A, String B) {
		System.out.println("containsKey_Wrapper testing");
		HashMap<String, String> containsKeyHashMap = A;
		containsKeyHashMap.containsKey(B);
		return containsKeyHashMap;
	}
}