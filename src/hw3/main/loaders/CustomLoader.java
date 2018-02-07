package hw3.main.loaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Loads compiled classes from a jar file
 * or a directory with jar files in a specified location.
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
            JarFile jarLib = null;
            String[] libFiles = new String[0];

            if (libPath.endsWith(".jar")) {
                jarLib = new JarFile(libPath);
            } else {
                /* directory was specified */
                File dir = new File(libPath);
                libFiles = dir.list();
            }

            if (libFiles != null) {
                for (String filepath : libFiles) {
                    if (filepath.endsWith(".jar")) {
                        /*  selecting the first jar file in the lib directory */
                        jarLib = new JarFile(libPath + filepath);
                        break;
                    }
                }
            } else {
                throw new IOException();
            }

            if (jarLib != null) {
                JarEntry jarEntry = jarLib.getJarEntry(
                        name.replace(".", "/") + ".class");

                InputStream libInputStream = jarLib.getInputStream(jarEntry);

                byte[] classBytes = new byte[(int) jarEntry.getSize()];
                if (libInputStream.read(classBytes) != classBytes.length) {
                    throw new IOException("Could not completely read the file " + libPath);
                }

                System.out.println("\tLoading class from " + jarLib.getName());

                return defineClass(name, classBytes, 0, classBytes.length);
            }

            throw new IOException();

        } catch (FileNotFoundException e) {
            System.out.printf("Jar File %s is not found\n", libPath);
            throw new ClassNotFoundException(e.getMessage(), e);
        } catch (IOException e) {
            System.out.printf("An error occurred while reading the lib path %s\n", libPath);
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }
}
