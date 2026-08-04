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
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class Main {
    // the window handle
    private static long window;

    // the rectangle primitive vertex array
    //
    // 2 ------- 3
    // | \       |
    // |    I    |
    // |       \ |
    // 0 ------- 1
    //
    // I = 0.0, 0.0
    // 0 = -0.5, -0.5
    // 1 = 0.5, -0.5
    // 2 = -0.5, 0.5
    // 3 = 0.5, 0.5
    private static final float[] prim_verts = {
        // first triangle  (0, 1, 2)
        // position colors          (r, g, b)     texture coords
        -0.5f, -0.5f, 0.0f,     1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
         0.5f, -0.5f, 0.0f,     0.5f, 0.7f, 0.0f,   1.0f, 0.0f,
        -0.5f,  0.5f, 0.0f,     0.0f, 0.7f, 0.5f,   0.0f, 1.0f,

        // second triangle (3, 2, 1)
        // position colors          (r, g, b)     texture coords
         0.5f,  0.5f, 0.0f,     0.0f, 0.0f, 1.0f,   1.0f, 1.0f,
        -0.5f,  0.5f, 0.0f,     0.0f, 0.7f, 0.5f,   0.0f, 1.0f,
         0.5f, -0.5f, 0.0f,     0.5f, 0.7f, 0.0f,   1.0f, 0.0f,
    };

    // the rectangle primitive vertex index
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
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

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

    private static void loop() {
        // makes rendering bindings available for use. do not delete.
        GL.createCapabilities();

        // set the clear color.
        glClearColor(51 / 255.0f, 76 / 255.0f, 76 / 255.0f, 0.0f);

        // wireframe mode
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        int VAO = 0;
        int VBO = 0;
        int EBO = 0;
        int texture1 = 0;
        int texture2 = 0;

        // create and compile shaders
        Shader ourShader = new Shader("/vertex_shader.vert", "/fragment_shader.frag");

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

            // construct vertex position attrubute
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // construct vertex color attribute
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);
            processErrors();

            // create texture 1
            texture1 = glGenTextures();
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture1);
            processErrors();

            // set wrapping and mipmap settings
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // load the image for texture 1.
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);
                IntBuffer pNrChannels = stack.mallocInt(1);

                STBImage.stbi_set_flip_vertically_on_load(true);
                ByteBuffer pData = STBImage.stbi_load(
                    "target/classes/copper_sheet.png", 
                    pWidth, pHeight, pNrChannels, 4
                );

                if (pData == null) {
                    throw new IOException("STBI image loading failed: " + STBImage.stbi_failure_reason());
                }

                // bind image data
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 
                    pWidth.get(0), pHeight.get(0), 0, GL_RGBA, 
                    GL_UNSIGNED_BYTE, pData
                );
                glGenerateMipmap(GL_TEXTURE_2D);
                processErrors();

                STBImage.stbi_image_free(pData);
            }

            // create texture
            texture2 = glGenTextures();
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, texture2);
            processErrors();

            // set wrapping and mipmap settings
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // load the image for texture 1.
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);
                IntBuffer pNrChannels = stack.mallocInt(1);

                STBImage.stbi_set_flip_vertically_on_load(true);
                ByteBuffer pData = STBImage.stbi_load(
                    "target/classes/a_stone.png",
                    pWidth, pHeight, pNrChannels, 4
                );

                if (pData == null) {
                    throw new IOException("STBI image loading failed: " + STBImage.stbi_failure_reason());
                }

                // bind image data
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 
                    pWidth.get(0), pHeight.get(0), 0, GL_RGBA, 
                    GL_UNSIGNED_BYTE, pData
                );
                glGenerateMipmap(GL_TEXTURE_2D);
                processErrors();

                STBImage.stbi_image_free(pData);
            }

            // bind attribute pointer
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);
            processErrors();

            // activate shader and set textures uniforms.
            ourShader.use();
            ourShader.setInt("texture1", 0);
            ourShader.setInt("texture2", 1);

            // create a transform for the model and pass it to the shader.
            Matrix4f transform = new Matrix4f()
                .translate(0.5f, -0.5f, 0.0f)
                .scale(0.5f)
                .rotate((float)Math.toRadians(45.0f), 0.0f, 0.0f, 1.0f);

            ourShader.setMat4("transform", transform);

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
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, texture1);
                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, texture2);

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