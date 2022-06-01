#version 320 core
#define MAX_INST 200

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_tex_coord;
layout (location = 2) in int in_draw_tex;

out int lnk_draw_tex;

void main() {

    lnk_draw_tex = draw_tex;
}