package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.exception.NativeLibraryException;
import dev.wateralt.mc.ics4ufinal.pod.MahjongTile;
import dev.wateralt.mc.ics4ufinal.pod.Tuple2;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import org.joml.Vector3f;

public class MahjongRenderer {
  private static final int MAX_INST = 200;

  MahjongClientState state;

  // OpenGL stuff below

  // gb_* opengl buffer objects
  // gg_* general opengl object
  // sh_* shader program objects
  // mt_* matrix objects

  int gb_model; // (x,y,z,tex_x,tex_y,draw_tex)
  int gb_vao;
  int gb_model_idx;
  int sh_drawTex;
  int sh_drawColor;
  Matrix4f mt_view;
  Matrix4f mt_projection;
  int gg_uniformTransformMatTex;
  int gg_uniformTransformMatColor;

  public MahjongRenderer(float width, float height) throws IOException {
    mt_view = new Matrix4f();
    mt_projection = new Matrix4f().perspective((float) Math.toRadians(100), width/height, 0.1f, 1.0f);

    int[] cookie = new int[1];
    GL32.glGenVertexArrays(cookie);
    gb_vao = cookie[0];
    GL32.glBindVertexArray(gb_vao);
    GL32.glGenBuffers(cookie);
    gb_model = cookie[0];
    GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, gb_model);
    float[] model_data = {
        -0.04f, -0.05f, -0.03f, 0.0f, 0.0f, 1.0f,
        +0.04f, -0.05f, -0.03f, 1.0f, 0.0f, 1.0f,
        -0.04f, +0.05f, -0.03f, 0.0f, 1.0f, 1.0f,
        +0.04f, +0.05f, -0.03f, 1.0f, 1.0f, 1.0f,
        -0.04f, -0.05f, +0.03f, 0.0f, 0.0f, 0.0f,
        +0.04f, -0.05f, +0.03f, 0.0f, 0.0f, 0.0f,
        -0.04f, +0.05f, +0.03f, 0.0f, 0.0f, 0.0f,
        +0.04f, +0.05f, +0.03f, 0.0f, 0.0f, 0.0f,
    };
    GL32.glBufferData(GL32.GL_ARRAY_BUFFER, model_data, GL32.GL_STATIC_DRAW);
    GL32.glGenBuffers(cookie);
    gb_model_idx = cookie[0];
    GL32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, gb_model_idx);
    int[] model_indices = {
        0, 1, 2, 3, 2, 1,
        //2, 1, 0, 1, 2, 3,
        //6, 5, 4, 5, 6, 7,
        //4, 0, 1, 1, 5, 4
    };
    GL32.glBufferData(GL32.GL_ELEMENT_ARRAY_BUFFER, model_indices, GL32.GL_STATIC_DRAW);
    GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 6 * 4, 0);
    GL32.glEnableVertexAttribArray(0);
    GL32.glVertexAttribPointer(1, 2, GL32.GL_FLOAT, false, 6 * 4, 3 * 4);
    GL32.glEnableVertexAttribArray(1);
    GL32.glVertexAttribPointer(2, 1, GL32.GL_FLOAT, false, 6 * 4, 5 * 4);
    GL32.glEnableVertexAttribArray(2);
    GL32.glBindVertexArray(0);

    // Shader compilation
    int tsh_vertex = GL32.glCreateShader(GL32.GL_VERTEX_SHADER);
    int tsh_drawTexFragment = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
    int tsh_drawColorFragment = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
    GL32.glShaderSource(tsh_vertex, Util.slurp(Util.ASSET_ROOT + "/vertex.glsl"));
    GL32.glCompileShader(tsh_vertex);
    GL32.glShaderSource(tsh_drawTexFragment, Util.slurp(Util.ASSET_ROOT + "/frag_drawtex.glsl"));
    GL32.glCompileShader(tsh_drawTexFragment);
    GL32.glShaderSource(tsh_drawColorFragment, Util.slurp(Util.ASSET_ROOT + "/frag_drawcolor.glsl"));
    GL32.glCompileShader(tsh_drawColorFragment);
    checkShaderCompile(tsh_vertex);
    checkShaderCompile(tsh_drawTexFragment);
    checkShaderCompile(tsh_drawColorFragment);
    sh_drawColor = GL32.glCreateProgram();
    sh_drawTex = GL32.glCreateProgram();
    GL32.glAttachShader(sh_drawColor, tsh_vertex);
    GL32.glAttachShader(sh_drawColor, tsh_drawColorFragment);
    GL32.glAttachShader(sh_drawTex, tsh_vertex);
    GL32.glAttachShader(sh_drawTex, tsh_drawTexFragment);
    GL32.glLinkProgram(sh_drawColor);
    GL32.glLinkProgram(sh_drawTex);
    checkProgramCompile(sh_drawColor);
    checkProgramCompile(sh_drawTex);
    GL32.glDeleteShader(tsh_vertex);
    GL32.glDeleteShader(tsh_drawTexFragment);
    GL32.glDeleteShader(tsh_drawColorFragment);
    gg_uniformTransformMatTex = GL32.glGetUniformLocation(sh_drawTex, "uni_transform_mat");
    gg_uniformTransformMatColor = GL32.glGetUniformLocation(sh_drawColor, "uni_transform_mat");
  }

  /*private void addTileToRenderQueue(MahjongTile tile, Vector3f position, Vector3f rotation) {
    int suitIdx;
    if(tile.getSuit() == 'm') suitIdx = 0;
    else if(tile.getSuit() == 'p') suitIdx = 1;
    else if(tile.getSuit() == 's') suitIdx = 2;
    else if(tile.getSuit() == 'z') suitIdx = 3;
    else throw new IllegalStateException("Tile with invalid suit");
    renderQueue.add(new Tuple2<>(
        new Tuple2<>(position, rotation),
        suitIdx * 9 + tile.getNumber()
    ));
  }*/

  private static void checkShaderCompile(int shader) {
    int[] cookie = new int[1];
    GL32.glGetShaderiv(shader, GL32.GL_COMPILE_STATUS, cookie);
    if(cookie[0] == 0) {
      throw new NativeLibraryException("Shader compilation failed: " + GL32.glGetShaderInfoLog(shader));
    }
  }

  private static void checkProgramCompile(int program) {
    int[] cookie = new int[1];
    GL32.glGetProgramiv(program, GL32.GL_LINK_STATUS, cookie);
    if(cookie[0] == 0) {
      throw new NativeLibraryException("Program compilation failed: " + GL32.glGetProgramInfoLog(program));
    }
  }

  public void rotateView(double x, double y) {
    mt_view.rotateX((float) Math.toRadians(x));
    mt_view.rotateY((float) Math.toRadians(y));
  }

  /*
  Normal rendering:

  init: setClientState()

  draw: prepareClientState() -> drawGL() -> drawVG()
  */
  public void setClientState(MahjongClientState state) {
    this.state = state;
  }

  public void drawGL() {
    //GL32.glEnable(GL32.GL_DEPTH_TEST);
    GL32.glDisable(GL32.GL_STENCIL_TEST);
    GL32.glClearColor(0.0f, 0.8f, 0.2f, 0.0f);
    GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);

    // Perform rendering
    float[] buf = new float[16];
    Matrix4f tmt_model = new Matrix4f()
        .mul(mt_projection)
        .mul(mt_view)
        .mul(new Matrix4f()
            .translate(new Vector3f(-0.1f, -0.2f, -0.8f))
            .rotateXYZ(new Vector3f(0.0f, 0.0f, 0.0f))
        )
    ;
    tmt_model.get(buf);
    GL32.glBindVertexArray(gb_vao);
    GL32.glUseProgram(sh_drawColor);
    GL32.glUniformMatrix4fv(gg_uniformTransformMatTex, false, buf);
    GL32.glUniformMatrix4fv(gg_uniformTransformMatColor, false, buf);
    GL32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, gb_model_idx);
    GL32.glDrawElements(GL32.GL_TRIANGLES, 6, GL32.GL_UNSIGNED_INT, 0);
  }

  public void drawVG(long nanovg) { }
}
