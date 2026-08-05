package studio.paperwing.orizuru.core;

import static org.lwjgl.system.MemoryUtil.NULL;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class Window {
    // OpenGL window handle.
    private long ID;

    private String name = "Hello Window!";
    private Vector2i size = new Vector2i(640, 480);

    /* package-private */ Window(String name, int width, int height) {
        // initialize the window
        this.name = name;
        this.size = new Vector2i(width, height);

        // reset window hints for this window to default.
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

        // set up the window
        ID = GLFW.glfwCreateWindow(this.size.x, this.size.y, this.name, NULL, NULL);

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

        GLFW.glfwSetWindowPos(ID, (vidMode.width() - this.size.x) / 2, (vidMode.height() - this.size.y) / 2);

        // set the window context to current for editing
        GLFW.glfwMakeContextCurrent(ID);

        // apply different settings to the window.
        GLFW.glfwSwapInterval(1);

        // create OpenGL window capabilities.
        GL.createCapabilities();

        // finalize the window by showing it.
        GLFW.glfwShowWindow(ID);
    }

    public long getID() { // TODO: make this /* package-private */ once there is no temporary api calls that need it.
        return ID;
    }

    /* package-private */ void terminate() {
        Callbacks.glfwFreeCallbacks(ID);
        GLFW.glfwDestroyWindow(ID);
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

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(ID);
    }
}
