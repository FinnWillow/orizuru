package studio.paperwing.orizuru.core;

import java.util.LinkedHashMap;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public final class Application {
    public enum INIT_STATE {
        CLOSED,
        BOOTING,
        OPEN,
        TERMINATING;
    }

    private static INIT_STATE initState = INIT_STATE.CLOSED;
    private static GLFWErrorCallback glfwErrorCallback = null;
    private static Thread appThread = null;

    private static LinkedHashMap<Long, Window> windowMap = new LinkedHashMap<>();

    private Application() {
    }

    public static void init() {
        // at the start of initialization, check if the application initialized once
        // already. if so, a application has been initialized already and cannot happen 
        // again.
        if (initState != INIT_STATE.CLOSED) {
            throw new IllegalStateException(
                "Application already initialized; Late or double init() detected.\n" +
                "Please, make sure this is called before close() and only once."
            );
        }

        // swith init state
        initState = INIT_STATE.BOOTING;

        System.out.printf("Starting LWJGL %s!\n", Version.getVersion());

        // setup default error callback
        glfwErrorCallback = GLFWErrorCallback.createPrint(System.err).set();

        // initialize GLFW
        if (!GLFW.glfwInit()) {
            // kill the application early
            purgeApplication();
            initState = INIT_STATE.CLOSED;

            // 
            throw new IllegalStateException(
                "GLFW initialisation failed."
            );
        }

        // record thread
        appThread = Thread.currentThread();

        // at the end of initialization, raise app open.
        initState = INIT_STATE.OPEN;
    }

    public static void terminate() {
        // at the start of initialisation, check if a application exists, with a init
        // complete. otherwise, no application was created that can be closed.
        if (initState != INIT_STATE.OPEN) {
            throw new IllegalStateException(
                "Application not yet initialized; Early or double close() detected.\n" +
                "Please, make sure this is called after init() and only once."
            );
        }

        // start terminating the application
        initState = INIT_STATE.TERMINATING;

        // terminate the app.
        purgeWindows();
        purgeApplication();
        
        // free the state so the app can start back up again right after.
        initState = INIT_STATE.CLOSED;
    }

    private static void checkOpen() {
        if (initState != INIT_STATE.OPEN) {
            throw new IllegalStateException(
                "Application is not initialized and open."
            );
        }
    }


    public static Window createWindow(String name, int width, int height) {
        checkOpen();

        Window createdWindow = new Window(name, width, height);
        windowMap.put(createdWindow.getID(), createdWindow);

        return createdWindow;
    }

    public static void destroyWindow(Window toDestroy) {
        checkOpen();

        // find window and destroy it.
        if (windowMap.get(toDestroy.getID()) == null) {
            throw new IllegalArgumentException(
                "The handle of the window you want to destroy does not exist in the application's context."
            );
        }

        // wipe window from the map and terminate it.
        windowMap.remove(toDestroy.getID()).terminate();
    }

    private static void purgeApplication() {
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null);
        glfwErrorCallback.free();
        glfwErrorCallback = null;
    }

    private static void purgeWindows() {
        // loop through and terminate each window.
        for (Window window : windowMap.values()) {
            window.terminate();
        }

        windowMap.clear();
    }

    public static void pollEvents() {
        checkOpen();
        GLFW.glfwPollEvents();
    }

    public static INIT_STATE getInitState() {
        return initState;
    }

    public static Thread getAppThread() {
        return appThread;
    }
}
