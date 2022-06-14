#version 330 core

// OpenGL Beginners: Fragment shaders can't be that heavy, can they?
// Me:

in vec2 lnk_tex_coord;
in float lnk_tex_blender;

out vec4 FragColor;

uniform int uni_tex_id;
uniform sampler2D uni_tex;
uniform int uni_highlight;

#define TILE_BACK_COLOR (vec3(0.1f, 1.0f, 0.3f))
#define TILE_FRONT_COLOR (vec3(1.0f, 1.0f, 1.0f))
#define TILE_HIGHLIGHT_COLOR (vec3(1.0f, 1.0f, 0.0f))

vec3 lerpColor(float x, vec3 a, vec3 b) {
  return a + vec2(1.0f, x).yyy * (b - a);
}

void main() {
    // Perform lerping
    vec4 frontTexel = texture(uni_tex, vec2((lnk_tex_coord.x + float(uni_tex_id)) / 40.0f, lnk_tex_coord.y));

    vec3 tmpFragColor = TILE_BACK_COLOR;
    tmpFragColor = lerpColor(lnk_tex_blender, tmpFragColor, frontTexel.xyz);
    tmpFragColor = lerpColor(uni_highlight / 2.0f, tmpFragColor, TILE_HIGHLIGHT_COLOR);

    //FragColor = vec4(TILE_BACK_COLOR + vec2(1.0f, lnk_tex_blender).yyy * (frontTexel.xyz - TILE_BACK_COLOR), 1.0f);
    FragColor = vec4(tmpFragColor, 1.0f);
}
