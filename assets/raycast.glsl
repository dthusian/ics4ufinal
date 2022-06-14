#version 330 core

out vec4 FragColor;

uniform int uni_tile_idx;

void main() {
    FragColor = vec4(0.0f, uni_tile_idx / 64.0f, 0.0f, 1.0f);
}