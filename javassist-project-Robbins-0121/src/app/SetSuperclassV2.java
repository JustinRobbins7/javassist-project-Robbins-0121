package app;

import java.io.File;
import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class SetSuperclassV2 {
	static final String SEP = File.separator;
	static String workDir = System.getProperty("user.dir");
	static String outputDir = workDir + SEP + "output\\";

	public static void main(String[] args) {
		// Get three class file names from the user
		String[] filenames = {};
		do {
			System.out.println("Enter three class names: ");
			filenames = util.UtilOption.getInputs();

			if (filenames.length != 3) {
				System.out.println("[WRN] Invalid Input");
			}
		} while (filenames.length != 3);

		// Get superclasses and subclasses as strings.
		int superClassId = findSuperClassIndex(filenames);
		String[] subclasses = { "", "" };
		String superclass = filenames[superClassId];
		
		for (int i = 0; i < filenames.length; i++) {
			if (i != superClassId) {
				if (subclasses[0].compareTo("") == 0) {
					subclasses[0] = filenames[i];
				} else {
					subclasses[1] = filenames[i];
				}
			}
		}
		
		//System.out.printf("Superclass: \n %s \n\n Subclasses: \n %s \n %s \n", superclass, subclasses[0], subclasses[1]);
		
		// Create files and assign superclass
		try 
		{
			ClassPool pool = ClassPool.getDefault();
			
			CtClass sc = pool.makeClass(superclass);
			sc.writeFile(outputDir);
			
			for(int i = 0; i < subclasses.length; i++) {
				CtClass cc = pool.makeClass(subclasses[i]);
				cc.defrost();
				setSuperclass(cc, superclass, pool);
				cc.writeFile(outputDir);
			}
			
			
		} catch (NotFoundException | CannotCompileException | IOException e) {
	        e.printStackTrace();
	    }
		
	}

	// Determines and returns the id of the superclass string in the argument array.
	private static int findSuperClassIndex(String[] args) {
		// Default to first element in the argument array.
		int ret = 0;

		for (int i = 0; i < args.length; i++) {
			// If current return superclass candidate doesn't have Common in its name,
			// overwrite it.
			if (args[i].length() >= 6) {
				if (args[i].substring(0, 6).compareTo("Common") == 0) {
					if (args[ret].length() < 6) {
						ret = i;
					} else {
						if (args[ret].substring(0, 6).compareTo("Common") != 0) {
							ret = i;
						} else {
							// If both have common, choose the longer argument.
							if (args[ret].length() < args[i].length()) {
								ret = i;
							}
						}
					}
				}
			}
		}

		return ret;
	}

	// Adds superclass to the given class
	static void setSuperclass(CtClass curClass, String superClass, ClassPool pool)
			throws NotFoundException, CannotCompileException {
		curClass.setSuperclass(pool.get(superClass));
		System.out.println("[DBG] set superclass: " + curClass.getSuperclass().getName() + //
				", subclass: " + curClass.getName());
	}
}
