package studio.paperwing;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
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
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
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
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
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
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import studio.paperwing.Camera.MOVEMENT;

public class Main {
    // the window handle
    private static long window;

    private static final Vector2i windowSize = new Vector2i(640, 480);

    // primitive cube
    private static final float[] prim_verts = {
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
         0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
         0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
         0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
         0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
         0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    };

    private static final Vector3f cubePositions[] = {
        new Vector3f( 0.0f,  0.0f,  0.0f), 
        new Vector3f( 2.0f,  5.0f, -15.0f), 
        new Vector3f(-1.5f, -2.2f, -2.5f),  
        new Vector3f(-3.8f, -2.0f, -12.3f),  
        new Vector3f( 2.4f, -0.4f, -3.5f),  
        new Vector3f(-1.7f,  3.0f, -7.5f),  
        new Vector3f( 1.3f, -2.0f, -2.5f),  
        new Vector3f( 1.5f,  2.0f, -2.5f), 
        new Vector3f( 1.5f,  0.2f, -1.5f), 
        new Vector3f(-1.3f,  1.0f, -1.5f)  
    };

    private static final Camera camera = new Camera(
        new Vector3f(0.0f, 0.0f, -3.0f),
        new Vector3f(0.0f, 1.0f, 0.0f), 
        -90.0f, 0.0f
    );
    private static float deltaTime = 0.0f;
    private static float lastFrame = 0.0f;
    private static boolean firstMouse = true;
    private static float lastX = 0.0f;
    private static float lastY = 0.0f;

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
        window = glfwCreateWindow(windowSize.x, windowSize.y, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window.");
        }

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
            if (firstMouse) { // initialize last x and y of the mouse.
                lastX = (float)xpos;
                lastY = (float)ypos;
                firstMouse = false;
            }

            // calculate offset
            float xOffset = (float)xpos - lastX;
            float yOffset = lastY - (float)ypos;
            lastX = (float)xpos;
            lastY = (float)ypos;

            // pass offset to camera process.
            camera.processMouseMovement(xOffset, yOffset, true);
        });

        glfwSetScrollCallback(window, (windowHandle, xOffset, yOffset) -> {
            camera.processMouseScroll((float)yOffset);
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

        // enable depth testing.
        glEnable(GL_DEPTH_TEST);

        // wireframe mode
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        int VAO = 0;
        int VBO = 0;
        int texture1 = 0;
        int texture2 = 0;

        // create and compile shaders
        Shader ourShader = new Shader("/vertex_shader.vert", "/fragment_shader.frag");

        // create a model matrix. use this to describe translation, rotation, and scale of the model.
        // WARNING: rotation wants a normalized vector, but does not normalize the vector itself, unike glm.
        //          you have to normalize it yourself manually.
        Matrix4f model = new Matrix4f()
            .rotation((float)Math.toRadians(-55.0f), new Vector3f(1.0f, 0.0f, 0.0f).normalize());

        // create a view matrix that describes where the camera is. position and rotation do what they do.
        // scale changes zoom.
        Matrix4f view = new Matrix4f()
            .translation(0.0f, 0.0f, -4.0f);

        // create a projection matrix, describes how and what the projection is. we have FOV, aspect ratio,
        // near and far plane. in this case it is a perspective projection.
        
        Matrix4f projection = new Matrix4f()
            .perspective((float)Math.toRadians(45.0f), (float)windowSize.x / windowSize.y, 0.1f, 100.0f);

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

            // construct vertex position attrubute
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // construct texture coord attribute
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
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

            // activate shader and set textures uniforms.
            ourShader.use();
            ourShader.setInt("texture1", 0);
            ourShader.setInt("texture2", 1);

            ourShader.setMat4("view", view);
            ourShader.setMat4("projection", projection);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // run the rendering loop until the user has attempted to close the window
        while (!glfwWindowShouldClose(window)) {
            try {
                float currentFrame = (float)glfwGetTime();
                deltaTime = currentFrame - lastFrame;
                lastFrame = currentFrame;
                processInput();

                // clear frame buffer
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                // set the textures.
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, texture1);
                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, texture2);

                // use camera's view matrix to position it in space properly
                view = camera.getViewMatrix();
                ourShader.setMat4("view", view);

                // use camera's zoom to dictate perspective fov.
                projection = new Matrix4f()
                    .perspective(camera.getFov(), (float)windowSize.x / windowSize.y, 0.1f, 100.0f);
                ourShader.setMat4("projection", projection);

                // draw the cubes
                glBindVertexArray(VAO);
                for (int i = 0; i < cubePositions.length; i++) {
                    float angle = (float)Math.toRadians(((i + 1) * 20.0) + (glfwGetTime() * 10.0));
                    // WARNING: -tion functions reset the matrix to identity before applying whatever it does.
                    //          therefore, you should always start with a -tion function, followed by a normal
                    //          verb.
                    //          In other words, use -tion of any kind exactly once at the start, or, use
                    //          .identity at the start and only ever use normal verbs. (translate, rotate, scale)
                    model.translation(cubePositions[i]);
                    model.rotate(angle, new Vector3f(1.0f, 0.3f, 0.5f).normalize());
                    ourShader.setMat4("model", model);
                    glDrawArrays(GL_TRIANGLES, 0, 36);
                }

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

    private static void processInput() {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.processKeyboard(MOVEMENT.FORWARD, deltaTime);
        }

        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.processKeyboard(MOVEMENT.BACKWARD, deltaTime);
        }

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.processKeyboard(MOVEMENT.LEFT, deltaTime);
        }

        if (glfwGetKey(window, GLFW_KEY_D)  == GLFW_PRESS) {
            camera.processKeyboard(MOVEMENT.RIGHT, deltaTime);
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