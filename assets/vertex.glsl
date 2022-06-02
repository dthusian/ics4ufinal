#version 330 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_tex_coord;
layout (location = 2) in float in_tex_blender;

out vec2 lnk_tex_coord;
out float lnk_tex_blender;

uniform mat4 uni_transform_mat;

void main() {
    lnk_tex_blender = in_tex_blender;
    lnk_tex_coord = in_tex_coord;
    gl_Position = uni_transform_mat * vec4(in_position, 1.0f);
}