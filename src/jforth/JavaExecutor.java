package jforth;

import tools.MemoryClassLoader;
import tools.MemoryJavaFileManager;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Simple interface to Java compiler using JSR 199 Compiler API.
 */
public class JavaExecutor
{
    private final javax.tools.JavaCompiler tool;
    private final StandardJavaFileManager stdManager;

    public JavaExecutor ()
    {
        tool = ToolProvider.getSystemJavaCompiler();
        if (tool == null)
        {
            throw new RuntimeException("Could not get Java compiler. Please, ensure that JDK is used instead of JRE.");
        }
        stdManager = tool.getStandardFileManager(null, null, null);
    }

    /**
     * Compile a single static method.
     */
    public Method compileStaticMethod (final String methodName, final String className,
                                       final String source)
            throws ClassNotFoundException
    {
        final Map<String, byte[]> classBytes = compile(className + ".java", source);
        final MemoryClassLoader classLoader = new MemoryClassLoader(classBytes);
        final Class<?> clazz = classLoader.loadClass(className);
        final Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods)
        {
            if (method.getName().equals(methodName))
            {
                if (!method.isAccessible())
                {
                    method.setAccessible(true);
                }
                return method;
            }
        }
        throw new NoSuchMethodError(methodName);
    }


    public Map<String, byte[]> compile (String fileName, String source)
    {
        return compile(fileName, source, new PrintWriter(System.err), null, null);
    }


    /**
     * compile given String source and return bytecodes as a Map.
     *
     * @param fileName   source fileName to be used for error messages etc.
     * @param source     Java source as String
     * @param err        error writer where diagnostic messages are written
     * @param sourcePath location of additional .java source files
     * @param classPath  location of additional .class files
     */
    private Map<String, byte[]> compile (String fileName, String source,
                                         Writer err, String sourcePath, String classPath)
    {
        // to collect errors, warnings etc.
        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<>();

        // create a new memory JavaFileManager
        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(stdManager);

        // prepare the compilation unit
        List<JavaFileObject> compUnits = new ArrayList<>(1);
        compUnits.add(MemoryJavaFileManager.makeStringSource(fileName, source));

        return compile(compUnits, fileManager, err, sourcePath, classPath);
    }

    private Map<String, byte[]> compile (final List<JavaFileObject> compUnits,
                                         final MemoryJavaFileManager fileManager,
                                         Writer err, String sourcePath, String classPath)
    {
        // to collect errors, warnings etc.
        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<>();

        // javac options
        List<String> options = new ArrayList<>();
        options.add("-Xlint:all");
        //       options.add("-g:none");
        options.add("-deprecation");
        if (sourcePath != null)
        {
            options.add("-sourcepath");
            options.add(sourcePath);
        }

        if (classPath != null)
        {
            options.add("-classpath");
            options.add(classPath);
        }

        // create a compilation task
        javax.tools.JavaCompiler.CompilationTask task =
                tool.getTask(err, fileManager, diagnostics,
                        options, null, compUnits);

        if (!task.call())
        {
            PrintWriter perr = new PrintWriter(err);
            for (Diagnostic diagnostic : diagnostics.getDiagnostics())
            {
                perr.println(diagnostic);
            }
            perr.flush();
            return null;
        }

        Map<String, byte[]> classBytes = fileManager.getClassBytes();
        try
        {
            fileManager.close();
        }
        catch (IOException exp)
        {
        }

        return classBytes;
    }
}


