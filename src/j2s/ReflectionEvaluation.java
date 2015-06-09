package j2s;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Random;

public class ReflectionEvaluation {
	public static void main(String[] args) {
		// tester(); 
		System.out.println(equals_Wrapper(""));
		System.out.println(toString_Wrapper());
		System.out.println(compareTo_Wrapper(""));
		System.out.println(indexOf_Wrapper("", 0));
		System.out.println(indexOf_Wrapper(""));
		System.out.println(valueOf_Wrapper(true));
		System.out.println(valueOf_Wrapper(0.0));
		System.out.println(valueOf_Wrapper(0));
		System.out.println(valueOf_Wrapper(""));
		System.out.println(length_Wrapper());
		System.out.println(isEmpty_Wrapper());
		System.out.println(charAt_Wrapper(0));
		System.out.println(equalsIgnoreCase_Wrapper(""));
		System.out.println(compareToIgnoreCase_Wrapper(""));
		System.out.println(startsWith_Wrapper(""));
		System.out.println(startsWith_Wrapper("", 0));
		System.out.println(endsWith_Wrapper(""));
		System.out.println(substring_Wrapper(0));
		System.out.println(substring_Wrapper(0, 0));
		System.out.println(concat_Wrapper(""));
		System.out.println(replaceAll_Wrapper("", ""));
		System.out.println(replaceFirst_Wrapper("", ""));
		System.out.println(toLowerCase_Wrapper());
		System.out.println(toUpperCase_Wrapper());
		System.out.println(trim_Wrapper());
	}

	public static void tester() {
		try {
			int index = 1;
			Class<?> c = Class.forName("java.lang.String");
			Object t = "This is a test for native2native.";

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
					Object o = m.invoke(t, args);

					String argumentClassNames = "";
					String arguments = "";
					if (args != null) {
						for (int i = 0; i < args.length; i++) {

							if (i + 1 == args.length) {
								if (args[i].getClass().getSimpleName().equals("String")) {
									arguments += ("\"" + args[i].toString() + "\"");
								} else {
									arguments += (args[i].toString());
								}
								argumentClassNames += (args[i].getClass().getSimpleName() + " " + (char) (i + 65));
							} else {
								if (args[i].getClass().getSimpleName().equals("String")) {
									arguments += ("\"" + args[i].toString() + "\", ");
								} else {
									arguments += (args[i].toString() + ", ");
								}
								argumentClassNames += (args[i].getClass().getSimpleName() + " " + (char) (i + 65) + ", ");
							}

						}
					}

					// System.out.println(index);
					System.out.printf("public static %s %s_Wrapper(%s) {\n" + "\tString testString = \"This is a test for native2native.\";\n"
							+ "\treturn testString.%s(%s);\n" + "}\n\n", rType, mName, argumentClassNames, mName, arguments);
					index++;

					// Handle any exceptions thrown by method to be
					// invoked.
				} catch (InvocationTargetException x) {
					// Throwable cause = x.getCause();
					// System.out.printf("invocation of %s failed: %s%n", mname,
					// cause.getMessage());
					continue;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		String testString = "This is a test for native2native.";
		return testString.equals("This is a test for native2native.");
	}

	/*
	 * This object (which is already a string!) is itself returned.
	 */
	public static String toString_Wrapper() {
		String testString = "This is a test for native2native.";
		return testString.toString();
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
	public static int compareTo_Wrapper(String A) {
		String testString = "This is a test for native2native.";
		return testString.compareTo("This is a test for native2native.");
	}

	/*
	 * Returns the index within this string of the first occurrence of the
	 * specified substring, starting at the specified index.
	 */
	public static int indexOf_Wrapper(String A, Integer B) {
		String testString = "This is a test for native2native.";
		return testString.indexOf("test", 0);
	}

	/*
	 * Returns the index within this string of the first occurrence of the
	 * specified substring.
	 */
	public static int indexOf_Wrapper(String A) {
		String testString = "This is a test for native2native.";
		return testString.indexOf("test");
	}

	/*
	 * Returns the string representation of the String argument.
	 */
	public static String valueOf_Wrapper(String A) {
		return String.valueOf("This is a test for native2native.");
	}

	/*
	 * Returns the string representation of the Boolean argument.
	 */
	public static String valueOf_Wrapper(Boolean A) {
		return String.valueOf(true);
	}

	/*
	 * Returns the string representation of the Integer argument.
	 */
	public static String valueOf_Wrapper(Integer A) {
		return String.valueOf(1);
	}

	/*
	 * Returns the string representation of the Double argument.
	 */
	public static String valueOf_Wrapper(Double A) {
		return String.valueOf(0.0);
	}

	/*
	 * Returns the length of this string. The length is equal to the number of
	 * Unicode code units in the string.
	 */
	public static int length_Wrapper() {
		String testString = "This is a test for native2native.";
		return testString.length();
	}

	/*
	 * Returns true if, and only if, length() is 0.
	 */
	public static boolean isEmpty_Wrapper() {
		String testString = "This is a test for native2native.";
		return testString.isEmpty();
	}

	/*
	 * Returns the char value at the specified index. An index ranges from 0 to
	 * length() - 1. The first char value of the sequence is at index 0, the
	 * next at index 1, and so on, as for array indexing.
	 */
	public static char charAt_Wrapper(Integer A) {
		String testString = "This is a test for native2native.";
		return testString.charAt(0);
	}

	/*
	 * Compares this String to another String, ignoring case considerations. Two
	 * strings are considered equal ignoring case if they are of the same length
	 * and corresponding characters in the two strings are equal ignoring case.
	 */
	public static boolean equalsIgnoreCase_Wrapper(String A) {
		String testString = "This is a test for native2native.";
		return testString.equalsIgnoreCase("test");
	}

	/*
	 * Compares two strings lexicographically, ignoring case differences. This
	 * method returns an integer whose sign is that of calling compareTo with
	 * normalized versions of the strings where case differences have been
	 * eliminated by calling
	 * Character.toLowerCase(Character.toUpperCase(character)) on each
	 * character.
	 */
	public static int compareToIgnoreCase_Wrapper(String A) {
		String testString = "This is a test for native2native.";
		return testString.compareToIgnoreCase("test") > 0 ? 1 : 0;
	}

	/*
	 * Tests if the substring of this string beginning at the specified index
	 * starts with the specified prefix.
	 */
	public static boolean startsWith_Wrapper(String A, Integer B) {
		String testString = "This is a test for native2native.";
		return testString.startsWith("test", 1);
	}

	/*
	 * Tests if this string starts with the specified prefix.
	 */
	public static boolean startsWith_Wrapper(String A) {
		String testString = "This is a test for native2native.";
		return testString.startsWith("test");
	}

	/*
	 * Tests if this string ends with the specified suffix.
	 */
	public static boolean endsWith_Wrapper(String A) {
		String testString = "This is a test for native2native.";
		return testString.endsWith("test");
	}

	/*
	 * Returns a new string that is a substring of this string. The substring
	 * begins at the specified beginIndex and extends to the character at index
	 * endIndex - 1. Thus the length of the substring is endIndex-beginIndex.
	 */
	public static String substring_Wrapper(Integer A, Integer B) {
		String testString = "This is a test for native2native.";
		return testString.substring(0, 0);
	}

	/*
	 * Returns a new string that is a substring of this string. The substring
	 * begins with the character at the specified index and extends to the end
	 * of this string.
	 */
	public static String substring_Wrapper(Integer A) {
		String testString = "This is a test for native2native.";
		return testString.substring(0);
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
	public static String concat_Wrapper(String A) {
		String testString = "This is a test for native2native.";
		return testString.concat("test");
	}

	/*
	 * Replaces the first substring of this string that matches the given
	 * regular expression with the given replacement.
	 */
	public static String replaceFirst_Wrapper(String A, String B) {
		String testString = "This is a test for native2native.";
		return testString.replaceFirst("test", "test2");
	}

	/*
	 * Replaces each substring of this string that matches the given regular
	 * expression with the given replacement.
	 */
	public static String replaceAll_Wrapper(String A, String B) {
		String testString = "This is a test for native2native.";
		return testString.replaceAll("test", "test2");
	}

	/*
	 * Converts all of the characters in this String to lower case using the
	 * rules of the default locale. This is equivalent to calling
	 * toLowerCase(Locale.getDefault()).
	 */
	public static String toLowerCase_Wrapper() {
		String testString = "This is a test for native2native.";
		return testString.toLowerCase();
	}

	/*
	 * Converts all of the characters in this String to upper case using the
	 * rules of the default locale. This method is equivalent to
	 * toUpperCase(Locale.getDefault()).
	 */
	public static String toUpperCase_Wrapper() {
		String testString = "This is a test for native2native.";
		return testString.toUpperCase();
	}

	/*
	 * Returns a copy of the string, with leading and trailing whitespace
	 * omitted.
	 */
	public static String trim_Wrapper() {
		String testString = "This is a test for native2native.";
		return testString.trim();
	}
}