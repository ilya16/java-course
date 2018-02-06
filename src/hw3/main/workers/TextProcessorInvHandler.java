package hw3.main.workers;

import hw3.main.loaders.CustomLoader;
import hw3.main.utils.TextHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * Handles the invocation of the TextProcessor proxy instance.
 *
 * @author Ilya Borovik
 */
public class TextProcessorInvHandler implements InvocationHandler {

    /** Class name of the Text Processor used in invocation */
    private final String className;

    /** Text Handler that validates the text in the resource */
    private TextHandler textHandler;

    /** Collection used to store processed tokens */
    private final Map<String, Integer> dictionary;

    /** Monitor that stores the status of the whole job */
    private final StatusMonitor monitor;

    /**
     * Constructor
     *
     * @param className     the name of the class
     * @param textHandler   the text handler object
     * @param dictionary    the map for storing results
     * @param monitor       the status monitor of the the whole job
     */
    TextProcessorInvHandler(String className, TextHandler textHandler,
                                   Map<String, Integer> dictionary, StatusMonitor monitor) {
        this.className = className;
        this.textHandler = textHandler;
        this.dictionary = dictionary;
        this.monitor = monitor;
    }

    /**
     * Processes a method invocation on a proxy instance and returns the result.
     *
     * @see InvocationHandler
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object textProcessor;
        CustomLoader customLoader;

        customLoader = (CustomLoader)Thread.currentThread().getContextClassLoader();

        /* Using Reflection to create new instance of textProcessor using UniqueWordsChecker constructor */
        Class<TextProcessor> tempClass =
                (Class<TextProcessor>) customLoader.loadClass("hw3.main.workers." + className);

        Constructor<TextProcessor> constructor =
                tempClass.getDeclaredConstructor(TextHandler.class, Map.class, StatusMonitor.class);

        textProcessor = constructor.newInstance(textHandler, dictionary, monitor);

        return method.invoke(textProcessor, args);
    }
}
