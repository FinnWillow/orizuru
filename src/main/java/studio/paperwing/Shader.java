package studio.paperwing;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.system.MemoryStack.stackPush;


import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public class Shader {
    private int ID;

    public Shader(String vertexPath, String fragmentPath) {
        String vertexCode = "";
        String fragmentCode = "";
        
        // read vertex and fragment shader data from files.
        try {
            vertexCode = readResource(vertexPath);
            fragmentCode = readResource(fragmentPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // compile vertex shader.
        int vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex, vertexCode);
        glCompileShader(vertex);
        
        try {
            processShaderErrors("ERROR: Vertex shader compilation failed.", vertex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // compile fragment shader.
        int fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment, fragmentCode);
        glCompileShader(fragment);

        try {
            processShaderErrors("ERROR: Fragment shader compilation failed.", fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // shader program.
        ID = glCreateProgram();
        glAttachShader(ID, vertex);
        glAttachShader(ID, fragment);
        glLinkProgram(ID);

        try {
            processProgramErrors("ERROR: Program shader linkage failed.", ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // delete shaders since they are linked inside the program.
        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    private static String readResource(String path) throws IOException {
        try (InputStream in = Shader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Resource not found: " + path);
            }

            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void processShaderErrors(String prefix, int shader) throws Exception {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pSuccess = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, pSuccess);

            if (pSuccess.get(0) != GL_TRUE) {
                String log = glGetShaderInfoLog(shader);
                throw new Exception(prefix + "\n" + log);
            }
        }
    }

    public static void processProgramErrors(String prefix, int program) throws Exception {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pSuccess = stack.mallocInt(1);
            glGetProgramiv(program, GL_LINK_STATUS, pSuccess);

            if (pSuccess.get(0) != GL_TRUE) {
                String log = glGetProgramInfoLog(program);
                throw new Exception(prefix + "\n" + log);
            }
        }
    }

    public void use() {
        glUseProgram(ID);
    }

    public int getID() {
        return ID;
    }

    public void setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(ID, name), value == true ? 1 : 0);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(ID, name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(ID, name), value);
    }

    public void setFloat(String name, double value) {
        setFloat(name, (float) value);
    }

    public void setVec2(String name, Vector2f value) {
        glUniform2f(glGetUniformLocation(ID, name), value.x, value.y);
    }

    public void setVec3(String name, Vector3f value) {
        glUniform3f(glGetUniformLocation(ID, name), value.x, value.y, value.z);
    }

    public void setVec4(String name, Vector4f value) {
        glUniform4f(glGetUniformLocation(ID, name), value.x, value.y, value.z, value.w);
    }

    public void setMat4(String name, Matrix4f value) {
        float[] out = new float[16];
        value.get(out);
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, out);
    }
}
