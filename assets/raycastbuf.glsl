#version 330 core

out vec4 FragColor;

uniform int uni_tex_id;

void main() {
    FragColor = vec4(0.0f, uni_tex_id / 50.0f, 0.0f, 1.0f);
}