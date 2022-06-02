#version 330 core

in vec2 lnk_tex_coord;
in float lnk_tex_blender;

out vec4 FragColor;

#define TILE_BACK_COLOR (vec4(0.1f, 1.0f, 0.3f, 1.0f))
// Placeholder, will use texture later
#define TILE_FRONT_COLOR (vec4(1.0f, 1.0f, 1.0f, 1.0f))

void main() {
    // Perform lerping
    //FragColor = vec4(1.0f, 0.0f, 0.0f, 1.0f);
    FragColor = TILE_BACK_COLOR + vec4(lnk_tex_blender).xxxx * (TILE_FRONT_COLOR - TILE_BACK_COLOR);
}
