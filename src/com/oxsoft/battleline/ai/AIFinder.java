package com.oxsoft.battleline.ai;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @see <a href="http://dzone.com/snippets/get-all-classes-within-package">Get
 *      All Classes Within A Package</a>
 * @see <a
 *      href="http://stackoverflow.com/questions/176527/how-can-i-enumerate-all-classes-in-a-package-and-add-them-to-a-list">How
 *      can I enumerate all classes in a package and add them to a List?</a>
 */
public class AIFinder {

	public static ArrayList<Class<? extends ArtificialIntelligence>> getAIs() {
		ArrayList<Class<? extends ArtificialIntelligence>> classes = new ArrayList<Class<? extends ArtificialIntelligence>>();
		try {
			for (Class<?> klass : getClasses(AIFinder.class.getPackage().getName())) {
				if (ArtificialIntelligence.class.isAssignableFrom(klass)) {
					if (klass != ArtificialIntelligence.class) {
						Class<? extends ArtificialIntelligence> aiClass = klass.asSubclass(ArtificialIntelligence.class);
						classes.add(aiClass);
					}
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		if (classes.size() == 0) { // On executable jar
			classes.add(DrWedge.class);
			classes.add(MrPhalanx.class);
			classes.add(MsBattalion.class);
			classes.add(TheFool.class);
		}
		return classes;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

}