package studio.paperwing.orizuru.core;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;

public class WindowSettings {
    public enum PROFILE {
        ANY(GLFW.GLFW_OPENGL_ANY_PROFILE),
        CORE(GLFW.GLFW_OPENGL_CORE_PROFILE),
        COMPAT(GLFW.GLFW_OPENGL_COMPAT_PROFILE),

        ; /* BODY */

        private int flag;
        private PROFILE(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return flag;
        }
    }
    private int vMajor = 3;
    private int vMinor = 3;

    private PROFILE profile = PROFILE.CORE;

    private boolean contextDebug = true;
    private boolean forwardCompatible = true;
    private boolean visible = true;

    private String name = "Hello Window!";
    private Vector2i size = new Vector2i(640, 480);
    
    // ------------------------------------------
    // CONSTRUCTORS
    public WindowSettings() {
    }

    // ------------------------------------------
    // GETTERS
    public int getVMajor() {
        return vMajor;
    }

    public int getVMinor() {
        return vMinor;
    }

    public String getVersion() {
        return vMajor + "." + vMinor;
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

    // ------------------------------------------
    // SETTERS
    public WindowSettings setVMajor(int vMajor) {
        this.vMajor = vMajor;
        return this;
    }

    public WindowSettings setVMinor(int vMinor) {
        this.vMinor = vMinor;
        return this;
    }

    public WindowSettings setVersion(int vMajor, int vMinor) {
        return setVMajor(vMajor).setVMinor(vMinor);
    }

    public WindowSettings setProfile(PROFILE profile) {
        this.profile = profile;
        return this;
    }

    public WindowSettings setContextDebug(boolean contextDebug) {
        this.contextDebug = contextDebug;
        return this;
    }

    public WindowSettings setForwardCompatible(boolean forwardCompatible) {
        this.forwardCompatible = forwardCompatible;
        return this;
    }

    public WindowSettings setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public WindowSettings setName(String name) {
        this.name = name;
        return this;
    }

    public WindowSettings setSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                "One or both bounds of the size are smaller than or equal to 0."
            );
        }
        
        this.size = new Vector2i(width, height);
        return this;
    }

    public WindowSettings setSize(Vector2i size) {
        return setSize(size.x, size.y);
    }


}