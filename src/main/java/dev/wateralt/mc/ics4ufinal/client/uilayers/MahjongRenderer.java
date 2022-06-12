package dev.wateralt.mc.ics4ufinal.client.uilayers;

import dev.wateralt.mc.ics4ufinal.client.MahjongClientState;
import dev.wateralt.mc.ics4ufinal.client.Window;
import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;
import dev.wateralt.mc.ics4ufinal.common.Util;
import dev.wateralt.mc.ics4ufinal.common.exception.NativeLibraryException;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL32;

import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MahjongRenderer implements UILayer {
  MahjongClientState state;
  double mouseX = 0.0f;
  double mouseY = 0.0f;

  // OpenGL stuff below

  int vboModel; // (x,y,z,tex_x,tex_y,draw_tex)
  int vaoModel;
  int eboModel;
  int texMahjong;
  int program;
  Matrix4f matView;
  Matrix4f matProjection;
  int uniTransformMat;
  int uniTextureID;

  //

  public MahjongRenderer() {

  }

  @Override
  public void initialize(Window wnd) {
    matView = new Matrix4f()
        .translate(new Vector3f(0.0f, 0.0f, 1.0f));
    matProjection = new Matrix4f().perspective((float) Math.toRadians(100), wnd.getWidth()/(float)wnd.getHeight(), 0.1f, 5.0f);
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
        +0.04f, -0.05f, +0.03f, 1.0f, 0.0f, 0.0f,
        -0.04f, +0.05f, +0.03f, 0.0f, 1.0f, 0.0f,
        +0.04f, +0.05f, +0.03f, 1.0f, 1.0f, 0.0f,
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
    uniTransformMat = GL32.glGetUniformLocation(program, "uni_transform_mat");
    uniTextureID = GL32.glGetUniformLocation(program, "uni_tex_id");

    // Load texture
    int[] width = new int[1];
    int[] height = new int[1];
    ByteBuffer buf = STBImage.stbi_load(Util.ASSET_ROOT + "/tiles.png", width, height, cookie, 3);
    if(buf == null) throw new NativeLibraryException("Failed to access image");
    GL32.glGenTextures(cookie);
    texMahjong = cookie[0];
    GL32.glBindTexture(GL32.GL_TEXTURE_2D, texMahjong);
    GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_S, GL32.GL_REPEAT);
    GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_T, GL32.GL_REPEAT);
    GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR);
    GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR);
    GL32.glTexImage2D(GL32.GL_TEXTURE_2D, 0, GL32.GL_RGB, width[0], height[0], 0, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, buf);
    STBImage.stbi_image_free(buf);

    GLFW.glfwSetInputMode(wnd.getGlfw(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
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
  public boolean onMouseMove(Window wnd, double newX, double newY) {
    matView = new Matrix4f()
        .rotateXYZ((float) Math.toRadians(newY), -(float) Math.toRadians(newX), 0.0f)
        .translate(new Vector3f(0.0f, 0.0f, -1.2f));
    mouseX = newX;
    mouseY = newY;
    return false;
  }

  public void setClientState(MahjongClientState state) {
    this.state = state;
  }

  @Override
  public String getId() {
    // Assume only 1 renderer can be active at once
    return "MahjongRenderer";
  }

  private void renderHand(int i, MahjongHand hand) {
    int tileIdx = 0;

    float[] buf = new float[16];
    for(MahjongTile tile : hand.getHidden()) {
      Matrix4f tmtModel = new Matrix4f()
          .mul(matProjection)
          .mul(matView)
          .mul(new Matrix4f()
              .rotateY((float) Math.toRadians(i * 90.0f + 180.0f))
              .translate(new Vector3f(0.08f * tileIdx - hand.getLength() * 0.08f / 2, 0.3f, -0.8f))
          );
      tmtModel.get(buf);
      GL32.glBindVertexArray(vaoModel);
      GL32.glUseProgram(program);
      GL32.glUniformMatrix4fv(uniTransformMat, false, buf);
      GL32.glUniform1i(uniTextureID, tile.getInternal());
      GL32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, eboModel);
      GL32.glBindTexture(GL32.GL_TEXTURE_2D, texMahjong);
      GL32.glDrawElements(GL32.GL_TRIANGLES, 36, GL32.GL_UNSIGNED_INT, 0);
      tileIdx++;
    }
  }

  @Override
  public void render(Window wnd) {
    GL32.glEnable(GL32.GL_DEPTH_TEST);
    GL32.glDisable(GL32.GL_STENCIL_TEST);
    GL32.glViewport(0, 0, wnd.getWidth(), wnd.getHeight());
    GL32.glClearColor(0.0f, 0.8f, 0.2f, 0.0f);
    GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);

    for(int i = 0; i < 4; i++) {
      renderHand(i, state.getPlayerHands()[i]);
    }
  }

  @Override
  public void close() {
    GL32.glDeleteProgram(program);
    GL32.glDeleteBuffers(new int[] { vboModel, eboModel });
    GL32.glDeleteTextures(texMahjong);
    GL32.glDeleteVertexArrays(vaoModel);
  }
}
