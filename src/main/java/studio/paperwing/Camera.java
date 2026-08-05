package studio.paperwing;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    public enum MOVEMENT {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    // camera attributes
    private Vector3f position = new Vector3f();
    private Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    private Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f);
    private Vector3f worldUp = new Vector3f(0.0f, 1.0f, 0.0f);

    // euler angles
    private float yaw = -90.0f;
    private float pitch = 0.0f;

    // camera options
    private float movementSpeed = 2.5f;
    private float mouseSensitivity = 0.1f;
    private float zoom = 45.0f;
    private boolean constrainPitch = true;

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.position = position;
        this.worldUp = up;
        this.yaw = yaw;
        this.pitch = pitch;
        updateCameraVectors();
    }

    public Camera (float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        this(new Vector3f(posX, posY, posZ), new Vector3f(upX, upY, upZ), yaw, pitch);
    }

    public Camera() {
        this(new Vector3f(), new Vector3f(0.0f, 0.0f, -1.0f), -90.0f, 0.0f);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, position.add(front, new Vector3f()), up);
    }

    public void processKeyboard(Camera.MOVEMENT direction, float delta) {
        float velocity = movementSpeed * delta;

        if (direction.equals(MOVEMENT.FORWARD)) {
            position.fma(velocity, this.front);
        }

        if (direction.equals(MOVEMENT.BACKWARD)) {
            position.fma(-velocity, this.front);
        }

        if (direction.equals(MOVEMENT.LEFT)) {
            position.fma(-velocity, this.right);
        }

        if (direction.equals(MOVEMENT.RIGHT)) {
            position.fma(velocity, this.right);
        }
    }

    public void processMouseMovement(float xOffset, float yOffset, boolean constrainPitch) {
        this.constrainPitch = constrainPitch;
        xOffset *= this.mouseSensitivity;
        yOffset *= this.mouseSensitivity;

        this.yaw += xOffset;
        this.pitch += yOffset;

        if (this.constrainPitch) {
            this.pitch = Math.min(this.pitch, 89.0f);
            this.pitch = Math.max(this.pitch, -89.0f);
        }

        updateCameraVectors();
    }

    public void processMouseScroll(float yOffset) {
        this.zoom -= yOffset;

        this.zoom = Math.max(this.zoom, 1.0f);
        this.zoom = Math.min(this.zoom, 120.0f);
    }

    private void updateCameraVectors() {
        Vector3f front = new Vector3f();
        front.x = (float)(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.y = (float)(Math.sin(Math.toRadians(pitch)));
        front.z = (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));

        this.front = front.normalize();
        this.front.cross(worldUp, right).normalize(right);
        this.right.cross(this.front, up).normalize(up);
    }

    public float getZoom() {
        return zoom;
    }

    public float getFov() {
        return (float)Math.toRadians(zoom);
    }

    public Vector3f getFront() {
        return front;
    }

    public float getMouseSensitivity() {
        return mouseSensitivity;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public float getPitch() {
        return pitch;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRight() {
        return right;
    }

    public Vector3f getUp() {
        return up;
    }

    public Vector3f getWorldUp() {
        return worldUp;
    }

    public float getYaw() {
        return yaw;
    }
}
