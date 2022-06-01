package dev.wateralt.mc.ics4ufinal;

import dev.wateralt.mc.ics4ufinal.pod.MahjongTile;
import org.lwjgl.opengl.GL32;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MahjongRenderer {
  MahjongClientState state;

  // OpenGL stuff below

  // rq_* render queue objects
  // gb_* opengl buffer objects
  // sh_* shader program objects
  // uni_* uniform objects

  ArrayList<Float> rq_PosRot; // 6 e/t (x,y,z,rx,ry,rz)
  ArrayList<Integer> rq_TileTex; // 1 e/t

  int gb_model;
  int gb_vao;
  int sh_drawTex;
  int sh_drawColor;

  //TODO stub
  public MahjongRenderer() throws IOException {
    rq_PosRot = new ArrayList<>();
    rq_TileTex = new ArrayList<>();

    int[] cookie = new int[1];
    GL32.glGenBuffers(cookie);
    gb_model = cookie[0];
    GL32.glGenVertexArrays(cookie);
    gb_vao = cookie[0];
    int tsh_vertex = GL32.glCreateShader(GL32.GL_VERTEX_SHADER);
    int tsh_drawTexFragment = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
    int tsh_drawColorFragment = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
    GL32.glShaderSource(tsh_vertex, Util.slurp(Util.ASSET_ROOT + "/vertex.glsl"));
    GL32.glShaderSource(tsh_drawTexFragment, Util.slurp(Util.ASSET_ROOT + "/frag_drawtex.glsl"));
    GL32.glShaderSource(tsh_drawColorFragment, Util.slurp(Util.ASSET_ROOT + "/frag_drawcolor.glsl"));
  }

  private void addTileToRenderQueue(MahjongTile tile, float[] position, float[] rotation) {
    // I dont trust JVM to do loop unrolling
    rq_PosRot.add(position[0]);
    rq_PosRot.add(position[1]);
    rq_PosRot.add(position[2]);
    rq_PosRot.add(rotation[0]);
    rq_PosRot.add(rotation[1]);
    rq_PosRot.add(rotation[2]);
    //
    int suitIdx;
    if(tile.getSuit() == 'm') suitIdx = 0;
    else if(tile.getSuit() == 'p') suitIdx = 1;
    else if(tile.getSuit() == 's') suitIdx = 2;
    else if(tile.getSuit() == 'z') suitIdx = 3;
    else throw new IllegalStateException("Tile with invalid suit");
    rq_TileTex.add(suitIdx * 9 + tile.getNumber());
  }

  /*
  Normal rendering:

  init: setClientState()

  draw: prepareClientState() -> drawGL() -> drawVG()
  */
  public void setClientState(MahjongClientState state) {
    this.state = state;
  }

  public void prepareClientState() { // pass in mouse pos somehow
    rq_PosRot = new ArrayList<>();
    rq_TileTex = new ArrayList<>();

    //TODO actually read from state
    for(int i = 0; i < 100; i++) {
      addTileToRenderQueue(new MahjongTile(Util.RANDOM.nextInt(1, 9), 'm'),
          new float[] { Util.RANDOM.nextFloat() - 0.5f, Util.RANDOM.nextFloat() - 0.5f, Util.RANDOM.nextFloat() - 0.5f },
          new float[] { 0.0f, 0.0f, 0.0f });
    }
  }

  public void drawGL() {
    GL32.glEnable(GL32.GL_DEPTH_TEST);
    GL32.glDisable(GL32.GL_STENCIL_TEST);
    GL32.glClearColor(0.0f, 0.8f, 0.2f, 0.0f);
    GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);

    // Assign render queues to uniform arrays

  }

  public void drawVG(long nanovg) { }
}
