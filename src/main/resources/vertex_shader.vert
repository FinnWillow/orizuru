#version 330 core

// the position variable with attribute position 0.
layout(location = 0) in vec3 aPos;

// the color variable with attribute position 1.
layout(location = 1) in vec3 aColor;

// the texture coordinate with attribute position 2.
layout(location = 2) in vec2 aTexCoord;

out vec3 ourColor;
out vec2 TexCoord;

void main() {
    // see how we directly give a vec3 to a vec4 constructor.
    gl_Position = vec4(aPos, 1.0);

    // set ourColor to the input color we got from the vertex data.
    ourColor = aColor;

    // pass the texture coordinate directly to the fragment
    TexCoord = aTexCoord;
}