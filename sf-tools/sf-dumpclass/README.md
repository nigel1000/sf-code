
Dump classes from running JVM process by sa-jdi.jar.

Usage:
 java -jar dumpclass.jar <pid> <pattern> [outputDir] <--classLoaderPrefix>

pattern: support ? * wildcard match.
outputDir: default outputDir is current directory.
--classLoaderPrefix: every classloader has it's own output directory. When multi classloaders load same name classes, try this.

Example:
 java -jar dumpclass.jar 4345 *StringUtils
 java -jar dumpclass.jar 4345 *StringUtils /tmp
 java -jar dumpclass.jar 4345 *StringUtils /tmp --classLoaderPrefix

Use the specified sa-jdi.jar:
 java -cp "./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar" cn.sf.tools.dumpclass.DumpMain <pid> <pattern> [outputDir]

Problem:
* Try to use sudo
* Make sure use the same jdk version.
* One class loaded by multi ClassLoader



http://hengyunabc.github.io/depth-analysis-hibernate-validar-noclassdefounderror/
＃spring-boot应用hibernate-validator NoClassDefFoundError
1.spring boot在 BackgroundPreinitializer 类里用一个独立的线程来加载validator，并吃掉了原始异常
2.第一次加载失败的类，在jvm里会被标记为initialization_error，再次加载时会直接抛出NoClassDefFoundError: Could not initialize class
3.当在代码里吞掉异常时要谨慎，否则排查问题带来很大的困难



查看HotSpot VM的运行时数据
使用HSDB来确定类的状态   http://www.jianshu.com/p/a28ae76ac3b4
Try to use HSDB. After attach java process, "Tools", "Class Browser".
sudo java -classpath "$JAVA_HOME/lib/sa-jdi.jar" sun.jvm.hotspot.HSDB
hsdb> class org.hibernate.validator.internal.util.Version
org/hibernate/validator/internal/util/Version @0x00000007c0060218
然后在Inspector查找到这个地址










