package j2s;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Random;

public class ReflectionEvaluation {
	public static void main(String[] args) {
		tester();
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
								if(args[i].getClass().getSimpleName().equals("String")) {
									arguments += ("\"" + args[i].toString() + "\"");
								} else {
									arguments += (args[i].toString());
								}
								argumentClassNames += (args[i].getClass().getSimpleName() + " " + (char) (i+65));
							} else {
								if(args[i].getClass().getSimpleName().equals("String")) {
									arguments += ("\"" + args[i].toString() + "\", ");
								} else {
									arguments += (args[i].toString() + ", ");
								}
								argumentClassNames += (args[i].getClass().getSimpleName() + " " + (char)(i+65) +", ");
							}
							
						}
					}
					
//					System.out.println(index);
					System.out.printf("public static %s %s_Wrapper(%s) {\n"
							+ "\tString testString = \"This is a test for native2native.\";\n"
							+ "\treturn testString.%s(%s);\n"
							+ "}\n\n", rType, mName, argumentClassNames, mName, arguments);
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
		} else if (paramType.contains("long") || paramType.contains("float")
				|| paramType.contains("double")) {
			return 0.0;
		}
		return "This is a test for native2native.";
	}
}