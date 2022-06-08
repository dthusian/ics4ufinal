#version 330 core

in vec2 lnk_tex_coord;
in float lnk_tex_blender;

out vec4 FragColor;

uniform int uni_tex_id;
uniform sampler2D uni_tex;

#define TILE_BACK_COLOR (vec3(0.1f, 1.0f, 0.3f))
#define TILE_FRONT_COLOR (vec3(1.0f, 1.0f, 1.0f))

void main() {
    // Perform lerping
    //vec4 frontTexel = texture(uni_tex, lnk_tex_coord);
    vec4 frontTexel = texture(uni_tex, vec2((lnk_tex_coord.x + float(uni_tex_id)) / 40.0f, lnk_tex_coord.y));
    FragColor = vec4(TILE_BACK_COLOR + vec2(1.0f, lnk_tex_blender).yyy * (frontTexel.xyz - TILE_BACK_COLOR), 1.0f);
}
