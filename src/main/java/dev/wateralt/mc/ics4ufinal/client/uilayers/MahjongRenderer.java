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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h2>Mahjong Renderer</h2>
 *
 * The main substance of the 3D rendering. Ms. Wong - if you're not familiar with OpenGL,
 * this part will be most difficult to understand. I'll try to add internal comments as much as possible.
 */
public class MahjongRenderer implements UILayer {
  private static final Vector3f cameraPos = new Vector3f(0.0f, 0.3f, -1.2f);

  // State
  MahjongClientState state;

  // Last mouse positions
  double mouseX = 0.0f;
  double mouseY = 0.0f;
  int hoveredTileIdx = -1;

  // OpenGL stuff below

  int vboModel; // (x,y,z,tex_x,tex_y,draw_tex)
  int vaoModel;
  int eboModel;
  int texMahjong;
  int program;
  int raycastShaders;
  Matrix4f matView;
  Matrix4f matProjection;

  int framebuffer;
  int framebufferColor;
  int framebufferDepth;
  ByteBuffer imageBuffer;
  int frameCounter;

  /**
   * Normal constructor.
   */
  public MahjongRenderer() {

  }

  /**
   *
   * @param wnd The window that the layer will be added to.
   */
  @Override
  public void initialize(Window wnd) {
    matView = new Matrix4f()
        .translate(cameraPos);
    matProjection = new Matrix4f().perspective((float) Math.toRadians(75), wnd.getWidth()/(float)wnd.getHeight(), 0.1f, 5.0f);
    int[] cookie = new int[1];

    // Model loading
    // Hardcoded vertices because easy
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
    int tsh_vertex = shaderCompile(GL32.GL_VERTEX_SHADER, Util.ASSET_ROOT + "/vertex.glsl");
    int tsh_fragment = shaderCompile(GL32.GL_FRAGMENT_SHADER, Util.ASSET_ROOT + "/fragment.glsl");
    int tsh_raycast = shaderCompile(GL32.GL_FRAGMENT_SHADER, Util.ASSET_ROOT + "/raycast.glsl");
    program = GL32.glCreateProgram();
    GL32.glAttachShader(program, tsh_vertex);
    GL32.glAttachShader(program, tsh_fragment);
    GL32.glLinkProgram(program);
    checkProgramCompile(program);
    raycastShaders = GL32.glCreateProgram();
    GL32.glAttachShader(raycastShaders, tsh_vertex);
    GL32.glAttachShader(raycastShaders, tsh_raycast);
    GL32.glLinkProgram(raycastShaders);
    checkProgramCompile(raycastShaders);
    GL32.glDeleteShader(tsh_vertex);
    GL32.glDeleteShader(tsh_fragment);
    GL32.glDeleteShader(tsh_raycast);

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

    // Framebuffer creation
    GL32.glGenFramebuffers(cookie);
    framebuffer = cookie[0];
    GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, framebuffer);
    GL32.glGenTextures(cookie);
    framebufferColor = cookie[0];
    GL32.glGenTextures(cookie);
    framebufferDepth = cookie[0];
    GL32.glBindTexture(GL32.GL_TEXTURE_2D, framebufferColor);
    GL32.glTexImage2D(GL32.GL_TEXTURE_2D, 0, GL32.GL_RGB, wnd.getWidth(), wnd.getHeight(), 0, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, 0);
    GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_S, GL32.GL_REPEAT);
    GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_T, GL32.GL_REPEAT);
    GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MIN_FILTER, GL32.GL_LINEAR);
    GL32.glTexParameteri(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_MAG_FILTER, GL32.GL_LINEAR);
    GL32.glFramebufferTexture2D(GL32.GL_FRAMEBUFFER, GL32.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D, framebufferColor, 0);
    GL32.glBindTexture(GL32.GL_TEXTURE_2D, framebufferDepth);
    GL32.glTexImage2D(GL32.GL_TEXTURE_2D, 0, GL32.GL_DEPTH24_STENCIL8, wnd.getWidth(), wnd.getHeight(), 0, GL32.GL_DEPTH_STENCIL, GL32.GL_UNSIGNED_INT_24_8, 0);
    GL32.glFramebufferTexture2D(GL32.GL_FRAMEBUFFER, GL32.GL_DEPTH_STENCIL_ATTACHMENT, GL32.GL_TEXTURE_2D, framebufferDepth, 0);
    GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
    imageBuffer = ByteBuffer.allocateDirect(wnd.getWidth() * wnd.getHeight() * 3);
    frameCounter = 0;
  }

  private static int shaderCompile(int type, String path) {
    int shader = GL32.glCreateShader(type);
    GL32.glShaderSource(shader, Util.slurp(path));
    GL32.glCompileShader(shader);
    int[] cookie = new int[1];
    GL32.glGetShaderiv(shader, GL32.GL_COMPILE_STATUS, cookie);
    if(cookie[0] == 0) {
      throw new NativeLibraryException("Shader compilation failed: " + GL32.glGetShaderInfoLog(shader));
    }
    return shader;
  }

  private static void checkProgramCompile(int program) {
    int[] cookie = new int[1];
    GL32.glGetProgramiv(program, GL32.GL_LINK_STATUS, cookie);
    if(cookie[0] == 0) {
      throw new NativeLibraryException("Program compilation failed: " + GL32.glGetProgramInfoLog(program));
    }
  }

  /**
   *
   * @param wnd The window the event is fired for.
   * @param newX The x-position (in pixels from the left) that the mouse is now at.
   * @param newY The y-position (in pixels from the top) that the mouse is now
   * @return
   */
  @Override
  public boolean onMouseMove(Window wnd, double newX, double newY) {
    moveCamera(newX - wnd.getWidth() / 2.0, newY - wnd.getHeight() / 2.0, -1);
    mouseX = newX;
    mouseY = newY;
    return false;
  }

  /**
   * Sets the client state that should be rendered.
   * @param state State to be rendered.
   */
  public void setClientState(MahjongClientState state) {
    this.state = state;
  }

  /**
   *
   * @return
   */
  @Override
  public String getId() {
    // Assume only 1 renderer can be active at once
    return "MahjongRenderer";
  }

  private void moveCamera(double xr, double yr, int playerIdOverride) {
    matView = new Matrix4f()
        .rotateXYZ((float) Math.toRadians(yr), -(float) Math.toRadians(xr), 0.0f)
        .translate(cameraPos)
        .rotateY((float) Math.toRadians(-90.0f * (playerIdOverride != -1 ? playerIdOverride : (state != null ? state.getMyPlayerId() : 0.0f))));
  }

  private void renderTile(float[] transformMat, int texId, boolean highlight, int tileIdx, boolean raycast) {
    GL32.glBindVertexArray(vaoModel);
    if(!raycast) {
      GL32.glUseProgram(program);
      GL32.glUniformMatrix4fv(GL32.glGetUniformLocation(program, "uni_transform_mat"), false, transformMat);
      GL32.glUniform1i(GL32.glGetUniformLocation(program, "uni_tex_id"), texId);
      GL32.glUniform1i(GL32.glGetUniformLocation(program, "uni_highlight"), highlight ? 1 : 0);
    } else {
      GL32.glUseProgram(raycastShaders);
      GL32.glUniformMatrix4fv(GL32.glGetUniformLocation(raycastShaders, "uni_transform_mat"), false, transformMat);
      GL32.glUniform1i(GL32.glGetUniformLocation(raycastShaders, "uni_tile_idx"), tileIdx);
    }
    GL32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, eboModel);
    GL32.glBindTexture(GL32.GL_TEXTURE_2D, texMahjong);
    GL32.glDrawElements(GL32.GL_TRIANGLES, 36, GL32.GL_UNSIGNED_INT, 0);
  }

  private void renderHand(int i, MahjongHand hand, boolean raycast, boolean[] highlightHand) {
    int tileIdx = 0;
    float[] buf = new float[16];

    for(MahjongTile tile : hand.getHidden()) {
      Matrix4f tmtModel = new Matrix4f()
          .mul(matProjection)
          .mul(matView)
          .mul(new Matrix4f()
              .rotateY((float) Math.toRadians(i * 90.0f + 180.0f))
              .translate(new Vector3f(0.08f * tileIdx - hand.getHidden().size() * 0.08f / 2, 0.3f, -0.8f))
          );
      tmtModel.get(buf);
      renderTile(buf, tile.getInternal(), tileIdx == hoveredTileIdx && highlightHand[tileIdx], tileIdx, raycast);
      tileIdx++;
    }
    tileIdx = 0;
    for(MahjongTile tile : hand.getHiddenKan()) {
      Matrix4f tmtModel = new Matrix4f()
          .mul(matProjection)
          .mul(matView)
          .mul(new Matrix4f()
              .rotateY((float) Math.toRadians(i * 90.0f + 180.0f))
              .translate(new Vector3f(- hand.getHidden().size() * 0.08f / 2 - 0.2f - 0.08f * tileIdx, 0.3f, -0.8f))
              .rotateX((float) Math.toRadians((tileIdx % 4 == 1 || tileIdx % 4 == 2) ? 270.0f : 90.0f))
          );
      tmtModel.get(buf);
      renderTile(buf, tile.getInternal(), false, tileIdx, false);
      tileIdx++;
    }
    for(MahjongTile tile : hand.getShown()) {
      Matrix4f tmtModel = new Matrix4f()
          .mul(matProjection)
          .mul(matView)
          .mul(new Matrix4f()
              .rotateY((float) Math.toRadians(i * 90.0f + 180.0f))
              .translate(new Vector3f(- hand.getHidden().size() * 0.08f / 2 - 0.2f - 0.08f * tileIdx, 0.3f, -0.8f))
              .rotateX((float) Math.toRadians(270.0f))
          );
      tmtModel.get(buf);
      renderTile(buf, tile.getInternal(), false, tileIdx, false);
      tileIdx++;
    }
  }

  public void renderDiscard(int i, ArrayList<MahjongTile> discard) {
    int tileIdx = 0;
    float[] buf = new float[16];
    for(MahjongTile tile : discard) {
      Matrix4f tmtModel = new Matrix4f()
          .mul(matProjection)
          .mul(matView)
          .mul(new Matrix4f()
              .rotateY((float) Math.toRadians(i * 90.0f + 180.0f))
              .translate(new Vector3f(0.08f * (tileIdx % 6) - (0.08f * 6) / 2, 0.3f - 0.02f, -0.3f - 0.10f * (float)Math.floor(tileIdx / 6.0f)))
              .rotateX((float) Math.toRadians(-90.0f))
          );
      tmtModel.get(buf);
      renderTile(buf, tile.getInternal(), false, tileIdx, false);
      tileIdx++;
    }
  }

  /**
   *
   * @param wnd The window to render to.
   */
  @Override
  public void render(Window wnd) {
    GL32.glEnable(GL32.GL_DEPTH_TEST);
    GL32.glDisable(GL32.GL_STENCIL_TEST);
    GL32.glViewport(0, 0, wnd.getWidth(), wnd.getHeight());
    GL32.glClearColor(0.0f, 0.8f, 0.2f, 0.0f);
    GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
    GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);

    if(state == null) {
    } else if(state.getWinningHand() != null) {
      moveCamera(0.0, 0.0, 0);
      renderHand(0, state.getWinningHand(), false, new boolean[state.getWinningHand().getLength()]);
    } else {
      for(int i = 0; i < 4; i++) {
        synchronized (state) {
          boolean[] highlight = new boolean[state.getMyHand().getLength()];
          if(i == state.getMyPlayerId()) {
            if(state.getPlayerAction() == MahjongClientState.PlayerAction.DISCARD_TILE) {
              if(hoveredTileIdx > -1 && hoveredTileIdx < state.getMyHand().getLength()) highlight[hoveredTileIdx] = true;
            } else if(state.getPlayerAction() == MahjongClientState.PlayerAction.SELECT_CHI) {
              if(hoveredTileIdx != -1 && hoveredTileIdx < state.getMyHand().getLength()) {
                MahjongTile[] chi = state.getCallOptions().getChiList().get(state.getMyHand().getHidden().get(hoveredTileIdx));
                if(chi != null) for(int j = 0; j < 3; j++) highlight[state.getMyHand().getHidden().indexOf(chi[j])] = true;
              }
            }
          }
          renderHand(i, state.getPlayerHands()[i], false, highlight);
          renderDiscard(i, state.getPlayerDiscardPiles().get(i));
        }
      }

      if(frameCounter % 10 == 0) {
        GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, framebuffer);
        GL32.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
        synchronized (state) {
          renderHand(state.getMyPlayerId(), state.getPlayerHands()[state.getMyPlayerId()], true, new boolean[state.getMyHand().getLength()]);
        }
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, framebufferColor);
        GL32.glGetTexImage(GL32.GL_TEXTURE_2D, 0, GL32.GL_RGB, GL32.GL_UNSIGNED_BYTE, imageBuffer);
        long b = Byte.toUnsignedLong(imageBuffer.get((wnd.getWidth() / 2 + (wnd.getHeight() / 2) * wnd.getWidth()) * 3 + 1));
        if(b >= 255) {
          hoveredTileIdx = -1;
        } else {
          hoveredTileIdx = (int) (b / 4);
        }
      }
      frameCounter++;
      GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
    }
  }

  /**
   *
   */
  @Override
  public void close() {
    GL32.glDeleteProgram(program);
    GL32.glDeleteBuffers(new int[] { vboModel, eboModel });
    GL32.glDeleteTextures(texMahjong);
    GL32.glDeleteVertexArrays(vaoModel);

    GL32.glDeleteProgram(raycastShaders);
    GL32.glDeleteFramebuffers(framebuffer);
    GL32.glDeleteTextures(new int[] { framebufferColor, framebufferDepth });
  }

  public int getHoveredTileIndex() {
    return hoveredTileIdx;
  }

}
