package studio.paperwing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

// @Disabled("Enable for OpenGL specific tests.")
public class TestOpenGL {
    // this aims to test the creation and destriction of a window, along with
    // rendering the base
    // triangle. along the way it will try to test multiple succeeding and failing
    // cases.
    private static long window;

    // the triangle primitive vertex array
    private static final float[] prim_verts = { 
        -0.5f, -0.5f, 
         0.5f, -0.5f, 
         0.0f,  0.5f, 
    };

    // the triangle primitive vertex index
    // CCW winding = front face.
    private static final int[] prim_index = { 
        0, 1, 2 
    };

    @BeforeAll
    static void initAll() {

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

        // makes rendering bindings available for use. do not delete.
        GL.createCapabilities();
    }

    @Test
    void passingVaoCreationTest() {
        int VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        assertDoesNotThrow(() -> {
            Main.processErrors();
        });
    }

    @Test
    void failingVaoCreationTest() {
        int VAO = -1;
        glBindVertexArray(VAO);

        Exception thrown = assertThrows(Exception.class, () -> {
            Main.processErrors();
        });

        System.out.println("[ " + thrown.getClass().getName() + " ]\n" + thrown.getMessage());

        assertTrue(thrown.getMessage().contains("operation"));
    }

    @Test
    void passingVertexShaderCreationTest() {
        // the vertex shader
        final String vert_glsl = """
        #version 330 core

        in vec2 a_pos;

        void main() {
            gl_Position = vec4(a_pos.x, a_pos.y, 0.0, 1.0);
        }
        """;

        // create and compile the vertex shader.
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vert_glsl);
        glCompileShader(vertexShader);

        // check shader for errors
        assertDoesNotThrow(() -> {
            Main.processShaderErrors("PASSING_TEST::ERROR::SHADER::VERTEX::COMPILATION_FALIED", vertexShader);
            Main.processErrors();
        });

        // finally, delete the shaders.
        glDeleteShader(vertexShader);
    }

    @Test
    void failingVertexShaderCreationTest() {
        // the vertex shader
        final String vert_glsl = """
        #version 330 core

        in vec2 a_pos;

        void main() {
            gl_Position = vec4(a_pos.x, a_POS.y, 0.0, 1.0);
        }
        """;

        // create and compile the vertex shader.
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vert_glsl);
        glCompileShader(vertexShader);

        // check shader for errors
        Exception thrown = assertThrows(Exception.class, () -> {
            Main.processShaderErrors("FAILING_TEST::ERROR::SHADER::VERTEX::COMPILATION_FALIED", vertexShader);
            Main.processErrors();
        });

        System.out.println("[ " + thrown.getClass().getName() + " ]\n" + thrown.getMessage());

        assertTrue(thrown.getMessage().contains("a_POS"));

        // finally, delete the shaders.
        glDeleteShader(vertexShader);
    }

    @Test
    void passingFragmentShaderCreationTest() {
        // the fragment shader
        final String frag_glsl = """
        #version 330 core

        out vec4 fragColor;
        void main() {
            fragColor = vec4(1.0, 0.5, 0.2, 1.0);
        }
        """;

        // create and compile fragment shader.
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, frag_glsl);
        glCompileShader(fragmentShader);

        // check shader for errors
        assertDoesNotThrow(() -> {
            Main.processShaderErrors("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED", fragmentShader);
            Main.processErrors();
        });

        // finally, delete the shaders.
        glDeleteShader(fragmentShader);
    }

    @Test
    void failingFragmentShaderCreationTest() {
        // the fragment shader
        final String frag_glsl = """
        #version 330 core

        out vec4 fragColor;
        void main() {
            fragCOLOR = vec4(1.0, 0.5, 0.2, 1.0);
        }
        """;

        // create and compile fragment shader.
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, frag_glsl);
        glCompileShader(fragmentShader);

        // check shader for errors
        Exception thrown = assertThrows(Exception.class, () -> {
            Main.processShaderErrors("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED", fragmentShader);
            Main.processErrors();
        });

        System.out.println("[ " + thrown.getClass().getName() + " ]\n" + thrown.getMessage());

        assertTrue(thrown.getMessage().contains("fragCOLOR"));

        // finally, delete the shaders.
        glDeleteShader(fragmentShader);
    }

    @Test
    void passingShaderPogramCreationTest() {
        // the vertex shader
        final String vert_glsl = """
        #version 330 core

        in vec2 a_pos;

        void main() {
            gl_Position = vec4(a_pos.x, a_pos.y, 0.0, 1.0);
        }
        """;

        // create and compile the vertex shader.
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vert_glsl);
        glCompileShader(vertexShader);

        // check shader for errors
        assertDoesNotThrow(() -> {
            Main.processShaderErrors("PASSING_TEST::ERROR::SHADER::VERTEX::COMPILATION_FALIED", vertexShader);
            Main.processErrors();
        });

        // the fragment shader
        final String frag_glsl = """
        #version 330 core

        out vec4 fragColor;
        void main() {
            fragColor = vec4(1.0, 0.5, 0.2, 1.0);
        }
        """;

        // create and compile fragment shader.
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, frag_glsl);
        glCompileShader(fragmentShader);

        // check shader for errors
        assertDoesNotThrow(() -> {
            Main.processShaderErrors("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED", fragmentShader);
            Main.processErrors();
        });

        // create shader program
        int shaderProgram = glCreateProgram();

        // attach shaders together and to the program
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // check for errors.
        assertDoesNotThrow(() -> {
            Main.processProgramErrors("ERROR::SHADER::PROGRAM::COMPILATION_FAILED", shaderProgram);
            Main.processErrors();
        });

        // finally, delete the shaders.
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    @Test
    void failingShaderProgramCreationTest() {
        // the vertex shader
        final String vert_glsl = """
        #version 330 core

        in vec2 a_pos;

        void main() {
            gl_Position = vec4(a_pos.x, a_pos.y, 0.0, 1.0);
        }
        """;

        // create and compile the vertex shader.
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vert_glsl);
        glCompileShader(vertexShader);

        // check shader for errors
        assertDoesNotThrow(() -> {
            Main.processShaderErrors("PASSING_TEST::ERROR::SHADER::VERTEX::COMPILATION_FALIED", vertexShader);
            Main.processErrors();
        });

        // the fragment shader
        final String frag_glsl = """
        #version 330 core

        out vec4 fragColor;
        void main() {
            fragCOLOR = vec4(1.0, 0.5, 0.2, 1.0);
        }
        """;

        // create and compile fragment shader.
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, frag_glsl);
        glCompileShader(fragmentShader);

        // check shader for errors
        assertThrows(Exception.class, () -> {
            Main.processShaderErrors("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED", fragmentShader);
            Main.processErrors();
        });

        // create shader program
        int shaderProgram = glCreateProgram();

        // attach shaders together and to the program
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // check shader for errors
        Exception thrown = assertThrows(Exception.class, () -> {
            Main.processProgramErrors("ERROR::SHADER::PROGRAM::COMPILATION_FAILED", shaderProgram);
            Main.processErrors();
        });

        System.out.println("[ " + thrown.getClass().getName() + " ]\n" + thrown.getMessage());
    
        // finally, delete the shaders.
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    @Test
    void passingFullFame() {
        final String vert_glsl = """
        #version 330 core

        in vec2 a_pos;

        void main() {
            gl_Position = vec4(a_pos.x, a_pos.y, 0.0, 1.0);
        }
        """;

        final String frag_glsl = """
        #version 330 core

        out vec4 fragColor;
        void main() {
            fragColor = vec4(1.0, 0.5, 0.2, 1.0);
        }
        """;

        // create the vertex array object
        int VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        // create a vertex buffer object and bind its data.
        int VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, prim_verts, GL_STATIC_DRAW);

        // create a element buffer object
        int EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, prim_index, GL_STATIC_DRAW);

        // create and compile the vertex shader.
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vert_glsl);
        glCompileShader(vertexShader);

        // check shader for errors
        assertDoesNotThrow(() -> {
            Main.processShaderErrors("ERROR::SHADER::VERTEX::COMPILATION_FALIED", vertexShader);
            Main.processErrors();
        });

        // create and compile fragment shader.
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, frag_glsl);
        glCompileShader(fragmentShader);

        // check for errors.
        assertDoesNotThrow(() -> {
            Main.processShaderErrors("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED", fragmentShader);
            Main.processErrors();
        });

        // create shader program
        int shaderProgram = glCreateProgram();

        // attach shaders together and to the program
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // check for errors.
        assertDoesNotThrow(() -> {
            Main.processProgramErrors("ERROR::SHADER::PROGRAM::COMPILATION_FAILED", shaderProgram);
            Main.processErrors();
        });
        
        // use program
        glUseProgram(shaderProgram);

        // finally, delete the shaders.
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        // construct vertex attrubute pointer
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // clear frame buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // draw the triangle
        glUseProgram(shaderProgram);
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        assertDoesNotThrow(() -> { Main.processErrors(); });
    }

    @AfterAll
    static void cleanup() {
        // free the window callbacks to the system
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
