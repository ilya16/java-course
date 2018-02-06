package hw3.main.loaders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Loads compiled classes from a jar file in a specified location.
 *
 * @author Ilya Borovik
 */
public class CustomLoader extends ClassLoader {

    /** Path to the Jar file */
    private String libPath;

    /**
     * Constructor
     *
     * @param libPath the path to the jar file
     */
    public CustomLoader(String libPath) {
        this.libPath = libPath;
    }

    /**
     * Finds the class in a specified location.
     *
     * @param   name
     *          The binary name of the class
     *
     * @return  The resulting Class object
     *
     * @throws  ClassNotFoundException
     *          If the class could not be found
     *
     * @see     ClassLoader
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            JarFile jarLib = new JarFile(libPath);
            JarEntry jarEntry = jarLib.getJarEntry(
                    name.replace(".", "/") + ".class");

            InputStream libInputStream = jarLib.getInputStream(jarEntry);

            byte[] classBytes = new byte[(int) jarEntry.getSize()];
            if (libInputStream.read(classBytes) != classBytes.length) {
                throw new IOException("Could not completely read the file " + libPath);
            }

            return defineClass(name, classBytes, 0, classBytes.length);

        } catch (FileNotFoundException e) {
            System.out.printf("Jar File %s is not found\n", libPath);
            throw new ClassNotFoundException(e.getMessage(), e);
        } catch (IOException e) {
            System.out.printf("An error occurred while reading the lib path %s\n", libPath);
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }
}
