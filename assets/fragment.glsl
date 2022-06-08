#version 330 core

// OpenGL Beginners: Fragment shaders can't be that heavy, can they?
// Me:

in vec2 lnk_tex_coord;
in float lnk_tex_blender;

out vec4 FragColor;

uniform int uni_tex_id;
uniform sampler2D uni_tex;

#define TILE_BACK_COLOR (vec3(0.1f, 1.0f, 0.3f))
#define TILE_FRONT_COLOR (vec3(1.0f, 1.0f, 1.0f))

vec3 lerpColor(float x, vec3 a, vec3 b) {
  return a + vec2(1.0f, x).yyy * (b - a);
}

void main() {
    // Perform lerping
    //vec4 frontTexel = texture(uni_tex, lnk_tex_coord);
    vec4 frontTexel = texture(uni_tex, vec2((lnk_tex_coord.x + float(uni_tex_id)) / 40.0f, lnk_tex_coord.y));
    //vec2 proximityToEdge = vec2(0.5f, 0.5f) - abs(lnk_tex_coord - vec2(0.5f, 0.5f));
    //float proximityToEdge2 = min(pow(proximityToEdge.x, 2), pow(proximityToEdge.y, 2));

    vec3 tmpFragColor = TILE_BACK_COLOR;
    tmpFragColor = lerpColor(1 - lnk_tex_blender, frontTexel.xyz, tmpFragColor);
    //tmpFragColor = lerpColor(pow(proximityToEdge2, 0.1f), vec3(0.0f, 0.0f, 0.0f), tmpFragColor);

    //FragColor = vec4(TILE_BACK_COLOR + vec2(1.0f, lnk_tex_blender).yyy * (frontTexel.xyz - TILE_BACK_COLOR), 1.0f);
    FragColor = vec4(tmpFragColor, 1.0f);
}
