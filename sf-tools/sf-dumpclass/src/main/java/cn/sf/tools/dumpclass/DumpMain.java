package cn.sf.tools.dumpclass;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class DumpMain {

	public static void main(String[] args)
			throws MalformedURLException, SecurityException, NoSuchMethodException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, InterruptedException {

		// check if need to reluanch
		checkRelaunch(args);

		ClassLoader classLoader = DumpMain.class.getClassLoader();
		//入参处理
		Queue<String> argsQueue = new LinkedList<>();
		if (args != null) {
			for (String arg : args) {
				if (arg.equals("--classLoaderPrefix")) {
					DumpWrapperFilterConfig.setClassLoaderPrefix(true);
				} else {
					argsQueue.add(arg);
				}
			}
		}
		if (argsQueue.isEmpty()) {
			// print usage
			System.out.println("命令输错了"+args);
			System.exit(-1);
		}
		String pid = argsQueue.poll();
		if (!argsQueue.isEmpty()) {
			DumpWrapperFilterConfig.setPattern(argsQueue.poll());
		}
		if (!argsQueue.isEmpty()) {
			DumpWrapperFilterConfig.setOutputDirectory(argsQueue.poll());
		}
		//增加hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				// print stat
				System.out.println("dumped classes counter: " + DumpWrapperFilterConfig.getDumpedCounter());
				System.out.println("output directory: "
						+ new File(DumpWrapperFilterConfig.getOutputDirectory()).getAbsolutePath());
		}));

		System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "cn.sf.tools.dumpclass.DumpWrapperFilter");
		Method mainMethod = classLoader.loadClass("sun.jvm.hotspot.tools.jcore.ClassDump").getMethod("main",
				String[].class);
		// sun.jvm.hotspot.tools.jcore.ClassDump.main(new String[] { pid });
		mainMethod.invoke(null, new Object[] { new String[] { pid } });
	}

	private static void checkRelaunch(final String[] args)
			throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		try {
			DumpMain.class.getClassLoader().loadClass("sun.jvm.hotspot.tools.jcore.ClassDump");
			return;
		} catch (ClassNotFoundException e) {
			System.out.println("can not find sa-jdi.jar from classpath, try to load it from java.home.");
			//why no class， order system.javaHome  env.javaHome jdk jre
			String javaHome = System.getProperty("java.home");
			if (javaHome == null) {
				javaHome = System.getenv("JAVA_HOME");
			}
			if (javaHome == null) {
				System.out.println("can not get java.home, can not load sa-jdi.jar.");
				System.exit(-1);
			}
			File file = new File(javaHome + "/lib/sa-jdi.jar");
			if (!file.exists()) {
				// java.home maybe jre
				file = new File(javaHome + "/../lib/sa-jdi.jar");
				if (!file.exists()) {
					System.out.println("can not find lib/sa-jdi.jar from java.home: " + javaHome);
				}
			}
			// build a new classloader, a trick.  let class loaded by the same classloader
			List<URL> urls = new ArrayList<>();
			Collections.addAll(urls, ((URLClassLoader) DumpMain.class.getClassLoader()).getURLs());
			urls.add(file.toURI().toURL());
			URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]),
					ClassLoader.getSystemClassLoader().getParent());
			Class<?> startClass = classLoader.loadClass(DumpMain.class.getName());
			final Method mainMethod = startClass.getMethod("main", String[].class);
			if (!mainMethod.isAccessible()) {
				mainMethod.setAccessible(true);
			}
			new Thread(() -> {
				try {
					mainMethod.invoke(null, new Object[] { args });
				} catch (IllegalAccessException e1) {
					e.printStackTrace();
					e1.printStackTrace();
				} catch (IllegalArgumentException e2) {
					e.printStackTrace();
					e2.printStackTrace();
				} catch (InvocationTargetException e3) {
					e.printStackTrace();
					e3.printStackTrace();
				}
			});
			Thread.currentThread().sleep(Long.MAX_VALUE);
		}
	}
}
