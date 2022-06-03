package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.MahjongClientState;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.exception.NativeLibraryException;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;

import org.joml.Vector3f;

public class MahjongRenderer implements UILayer {
  MahjongClientState state;
  double mouseX = 0.0f;
  double mouseY = 0.0f;

  // Debug only
  float modelRotX = 0.0f;
  float modelRotY = 0.0f;

  // OpenGL stuff below

  int vboModel; // (x,y,z,tex_x,tex_y,draw_tex)
  int vaoModel;
  int eboModel;
  int program;
  Matrix4f matView;
  Matrix4f matProjection;
  int uniformTransformMat;


  public MahjongRenderer() {

  }

  @Override
  public void initialize(Window wnd) {
    matView = new Matrix4f();
    matProjection = new Matrix4f().perspective((float) Math.toRadians(100), wnd.getWidth()/(float)wnd.getHeight(), 0.1f, 1.0f);
    int[] cookie = new int[1];
    GL32.glGenVertexArrays(cookie);
    vaoModel = cookie[0];
    GL32.glBindVertexArray(vaoModel);
    GL32.glGenBuffers(cookie);
    vboModel = cookie[0];
    GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vboModel);
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
    eboModel = cookie[0];
    GL32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, eboModel);
    int[] model_indices = {
        0, 1, 2, 3, 2, 1,
        4, 5, 6, 7, 6, 5,
        0, 1, 4, 1, 4, 5,
        3, 2, 7, 2, 7, 6,
        1, 3, 5, 3, 5, 7,
        0, 2, 4, 2, 4, 6
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
    int tsh_fragment = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
    GL32.glShaderSource(tsh_vertex, Util.slurp(Util.ASSET_ROOT + "/vertex.glsl"));
    GL32.glCompileShader(tsh_vertex);
    GL32.glShaderSource(tsh_fragment, Util.slurp(Util.ASSET_ROOT + "/fragment.glsl"));
    GL32.glCompileShader(tsh_fragment);
    checkShaderCompile(tsh_vertex);
    checkShaderCompile(tsh_fragment);
    program = GL32.glCreateProgram();
    GL32.glAttachShader(program, tsh_vertex);
    GL32.glAttachShader(program, tsh_fragment);
    GL32.glLinkProgram(program);
    checkProgramCompile(program);
    GL32.glDeleteShader(tsh_vertex);
    GL32.glDeleteShader(tsh_fragment);
    uniformTransformMat = GL32.glGetUniformLocation(program, "uni_transform_mat");
  }

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

  @Override
  public void onMouseMove(Window wnd, double newX, double newY) {
    modelRotX += (mouseY - newY) / 5;
    modelRotY += (mouseX - newX) / 5;
    mouseX = newX;
    mouseY = newY;
  }

  public void setClientState(MahjongClientState state) {
    this.state = state;
  }

  @Override
  public String getId() {
    // Assume only 1 renderer can be active at once
    return "MahjongRenderer";
  }

  @Override
  public void render(Window wnd) {
    GL32.glEnable(GL32.GL_DEPTH_TEST);
    GL32.glDisable(GL32.GL_STENCIL_TEST);
    GL32.glViewport(0, 0, wnd.getWidth(), wnd.getHeight());
    GL32.glClearColor(0.0f, 0.8f, 0.2f, 0.0f);
    GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);

    // Perform rendering
    float[] buf = new float[16];
    Matrix4f tmt_model = new Matrix4f()
        .mul(matProjection)
        .mul(matView)
        .mul(new Matrix4f()
            .translate(new Vector3f(-0.1f, -0.2f, -0.8f))
            .rotateXYZ(new Vector3f((float) Math.toRadians(modelRotX), (float) Math.toRadians(modelRotY), 0.0f))
        )
    ;
    tmt_model.get(buf);
    GL32.glBindVertexArray(vaoModel);
    GL32.glUseProgram(program);
    GL32.glUniformMatrix4fv(uniformTransformMat, false, buf);
    GL32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, eboModel);
    GL32.glDrawElements(GL32.GL_TRIANGLES, 36, GL32.GL_UNSIGNED_INT, 0);
  }

  @Override
  public void close() {
    GL32.glDeleteProgram(program);
    GL32.glDeleteBuffers(new int[] { vboModel, eboModel });
    GL32.glDeleteVertexArrays(vaoModel);
  }
}
