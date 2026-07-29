package studio.paperwing;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class Main {
    // the window handle
    private static long window;

    // the vertex shader
    private static final String vert_glsl = """
        #version 330 core

        in vec2 a_pos;

        void main() {
            gl_Position = vec4(a_pos.x, a_pos.y, 0.0, 1.0);
        }
        """;

    // the fragment shader
    private static final String frag_glsl = """
        #version 330 core

        out vec4 fragColor;
        void main() {
            fragColor = vec4(1.0, 0.5, 0.2, 1.0);
        }
        """;

    // the triangle primitive vertex array
    //
    // 2 ------- 3
    // |  \      |
    // |    I    |
    // |      \  |
    // 0 ------- 1
    //
    // I =  0.0,  0.0
    // 0 = -0.5, -0.5
    // 1 =  0.5, -0.5
    // 2 = -0.5,  0.5
    // 3 =  0.5,  0.5
    private static final float[] prim_verts = { 
        // first triangle (0, 1, 2)
        -0.5f, -0.5f,
         0.5f, -0.5f,
        -0.5f,  0.5f,

        // second triangle (3, 2, 1)
         0.5f,  0.5f,
        -0.5f,  0.5f,
         0.5f, -0.5f,
    };

    // the triangle primitive vertex index
    // CCW winding = front face.
    private static final int[] prim_index = { 
        0, 1, 2,
        3, 2, 1,
    };

    private static void init() {
        System.out.printf("Starting LWJGL %s!\n", Version.getVersion());

        // setup default error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException();
        }

        // continue GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        // create the window
        window = glfwCreateWindow(640, 480, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window.");
        }

        // setup a key callback; called per frame, per key event.
        glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowHandle, true); // we will detect this in the rendering loop
            }
        });

        // get the thread stack and push a new frame
        try (@SuppressWarnings("unused")
        MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stackMallocInt(1);
            IntBuffer pHeight = stackMallocInt(1);

            // get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // get the resolution of the primary monitor
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // center the window
            glfwSetWindowPos(window, (vidMode.width() - pWidth.get(0)) / 2, (vidMode.height() - pHeight.get(0)) / 2);
        }

        // make the OpenGl context current
        glfwMakeContextCurrent(window);

        // enable v-sync
        glfwSwapInterval(1);

        // make the window visible
        glfwShowWindow(window);
    }

    public static void processErrors() throws Exception {
        int errCode = glGetError();
        if (errCode != GL_NO_ERROR) {
            switch (errCode) {
                case GL_INVALID_ENUM:
                    throw new Exception("An unacceptable value is specified for an enumerated argument.");

                case GL_INVALID_VALUE:
                    throw new Exception("A numeric argument is out of range.");

                case GL_INVALID_OPERATION:
                    throw new Exception("The specified operation is not allowed in the current state.");

                case GL_INVALID_FRAMEBUFFER_OPERATION:
                    throw new Exception("The framebuffer object is not complete.");

                case GL_OUT_OF_MEMORY:
                    throw new Exception("There is not enough memory left to execute the command.");

                case GL_STACK_UNDERFLOW:
                    throw new Exception("An attempt has been made to perform an operation that would "
                            + "cause an internal stack to underflow.");
                case GL_STACK_OVERFLOW:
                    throw new Exception("An attempt has been made to perform an operation that would "
                            + "cause an internal stack to overflow.");
            }
        }
    }

    public static void processShaderErrors(String prefix, int shader) throws Exception {
        try (
            @SuppressWarnings("unused")
            MemoryStack stack = stackPush()
        ) {
            IntBuffer pSuccess = stackMallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, pSuccess);

            if (pSuccess.get() != GL_TRUE) {
                String log = glGetShaderInfoLog(shader);
                throw new Exception(prefix + "\n" + log);
            }
        }
    }

    public static void processProgramErrors(String prefix, int program) throws Exception {
        try (
            @SuppressWarnings("unused")
            MemoryStack stack = stackPush()
        ) {
            IntBuffer pSuccess = stackMallocInt(1);
            glGetProgramiv(program, GL_LINK_STATUS, pSuccess);

            if (pSuccess.get() != GL_TRUE) {
                String log = glGetProgramInfoLog(program);
                throw new Exception(prefix + "\n" + log);
            }
        }
    }

    private static void loop() {
        // makes rendering bindings available for use. do not delete.
        GL.createCapabilities();

        // set the clear color.
        glClearColor(51 / 255.0f, 76 / 255.0f, 76 / 255.0f, 0.0f);

        // wireframe mode
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        int vertexShader = 0;
        int fragmentShader = 0;
        int shaderProgram = 0;
        int VAO = 0;
        int VBO = 0;
        int EBO = 0;

        try {
            // create the vertex array object
            VAO = glGenVertexArrays();
            glBindVertexArray(VAO);
            processErrors();

            // create a vertex buffer object and bind its data.
            VBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, VBO);
            glBufferData(GL_ARRAY_BUFFER, prim_verts, GL_STATIC_DRAW);
            processErrors();

            // create a element buffer object
            EBO = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, prim_index, GL_STATIC_DRAW);
            processErrors();

            // create and compile the vertex shader.
            vertexShader = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexShader, vert_glsl);
            glCompileShader(vertexShader);
            

            // check shader for errors
            processErrors();
            processShaderErrors("ERROR::SHADER::VERTEX::COMPILATION_FALIED", vertexShader);

            // create and compile fragment shader.
            fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragmentShader, frag_glsl);
            glCompileShader(fragmentShader);
            
            // check for errors.
            processErrors();
            processShaderErrors("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED", fragmentShader);

            // create shader program
            shaderProgram = glCreateProgram();

            // attach shaders together and to the program
            glAttachShader(shaderProgram, vertexShader);
            glAttachShader(shaderProgram, fragmentShader);
            glLinkProgram(shaderProgram);
            
            // check for errors.
            processErrors();
            processProgramErrors("ERROR::SHADER::PROGRAM::COMPILATION_FAILED", shaderProgram);
            
            // use program
            glUseProgram(shaderProgram);

            // finally, delete the shaders.
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);

            // construct vertex attrubute pointer
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);
            
            processErrors();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // run the rendering loop until the user has attempted to close the window
        while (!glfwWindowShouldClose(window)) {
            try {
                // clear frame buffer
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                // draw the triangle
                glUseProgram(shaderProgram);
                glBindVertexArray(VAO);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
                processErrors();

                // swap buffers
                glfwSwapBuffers(window);

                // poll input events.
                glfwPollEvents();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private static void cleanup() {
        // free the window callbacks to the system
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void run() {
        init();
        loop();
        cleanup();
    }

    public static void main(String[] args) {
        run();
    }
}