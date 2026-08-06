package studio.paperwing.orizuru.core;

import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import studio.paperwing.orizuru.core.WindowSettings.PROFILE;

public class Window {
    // OpenGL window handle.
    private long ID;
    private String glVersion; // TODO: Log this when loggging is a thing.
    private String glRenderer; // TODO: Log this when loggging is a thing.
    private int vMajor;
    private int vMinor;
    private PROFILE profile;
    private boolean contextDebug;
    private boolean forwardCompatible;
    private boolean visible;
    private String name;
    private Vector2ic size;

    /* package-private */ Window(WindowSettings settings) {
        // initialize the window
        this.vMajor = settings.getVMajor();
        this.vMinor = settings.getVMinor();
        this.profile = settings.getProfile();
        this.contextDebug = settings.isContextDebug();
        this.forwardCompatible = settings.isForwardCompatible();
        this.visible = settings.isVisible();
        this.name = settings.getName();
        this.size = settings.getSize();

        // reset window hints for this window to default.
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, vMajor);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, vMinor);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, profile.getFlag());
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // starts out invisible no matter what, for centering.
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, forwardCompatible ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_DEBUG, contextDebug ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);

        // set up the window
        ID = GLFW.glfwCreateWindow(this.size.x(), this.size.y(), this.name, NULL, NULL);

        // check if the window got created properly.
        if (ID == NULL) {
            throw new RuntimeException("Window initialization failed.");
        }

        long monitorID = GLFW.glfwGetPrimaryMonitor();

        if (monitorID == NULL) {
            GLFW.glfwDestroyWindow(ID);
            throw new RuntimeException("Primary monitor could not be found.");
        }

        // center window
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitorID);
        if (vidMode == null) {
            GLFW.glfwDestroyWindow(ID);
            throw new RuntimeException("Video mode could not be found.");
        }

        GLFW.glfwSetWindowPos(ID, 
            (vidMode.width() - this.size.x()) / 2,
            (vidMode.height() - this.size.y()) / 2
        );

        // set the window context to current for editing
        GLFW.glfwMakeContextCurrent(ID);

        // apply different settings to the window.
        GLFW.glfwSwapInterval(1);
        
        // create OpenGL window capabilities.
        GL.createCapabilities();
        glVersion = GL30.glGetString(GL30.GL_VERSION);
        glRenderer = GL30.glGetString(GL30.GL_RENDERER);

        // finalize the window by showing it, if the window is visible.
        if (this.visible) {
            GLFW.glfwShowWindow(ID);
        }
    }

    public long getID() { // TODO: make this /* package-private */ once there is no temporary api calls that need it.
        return ID;
    }

    /* package-private */ void terminate() {
        Callbacks.glfwFreeCallbacks(ID);
        GLFW.glfwDestroyWindow(ID);
    }

    public String getGlVersion() {
        return glVersion;
    }

    public String getGlRenderer() {
        return glRenderer;
    }

    public int getVMajor() {
        return vMajor;
    }

    public int getVMinor() {
        return vMinor;
    }

    public PROFILE getProfile() {
        return profile;
    }

    public boolean isContextDebug() {
        return contextDebug;
    }

    public boolean isForwardCompatible() {
        return forwardCompatible;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getName() {
        return name;
    }

    public Vector2ic getSize() {
        return size;
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(ID);
    }

    public void setShouldClose(boolean condition) {
        GLFW.glfwSetWindowShouldClose(ID, condition);
    }

    public void setVisibility(boolean visible) {
        if (visible && !this.visible) {
            GLFW.glfwShowWindow(ID);
            this.visible = true;
        } else if (!visible && this.visible){
            GLFW.glfwHideWindow(ID);
            this.visible = false;
        }
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(ID);
    }
}
