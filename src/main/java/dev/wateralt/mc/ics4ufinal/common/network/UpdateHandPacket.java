package dev.wateralt.mc.ics4ufinal.common.network;

import dev.wateralt.mc.ics4ufinal.common.MahjongHand;
import dev.wateralt.mc.ics4ufinal.common.MahjongTile;

import java.nio.ByteBuffer;

public class UpdateHandPacket extends Packet {
  public static final int ID = 202;
  MahjongHand hand;
  boolean obscured;
  int playerId;

  public UpdateHandPacket() {
    hand = new MahjongHand();
    obscured = false;
  }

  public UpdateHandPacket(MahjongHand hand, int playerId, boolean obscured) {
    this.hand = hand;
    this.obscured = obscured;
    this.playerId = playerId;
  }

  public MahjongHand getHand() {
    return hand;
  }

  public boolean isObscured() {
    return obscured;
  }

  public int getPlayerId() {
    return playerId;
  }

  @Override
  public int getId() {
    return ID;
  }

  @Override
  protected ByteBuffer serializeInternal() {
    ByteBuffer buf = ByteBuffer.allocate(hand.getLength() * 4 + 16);
    buf.putInt(playerId);
    buf.putInt(hand.getHidden().size());
    for(MahjongTile t : hand.getHidden()) {
      if(!obscured) {
        buf.putInt(t.getInternal());
      } else {
        buf.putInt(MahjongTile.NULL.getInternal());
      }
    }
    buf.putInt(hand.getHiddenKan().size());
    for(MahjongTile t : hand.getHiddenKan()) {
      buf.putInt(t.getInternal());
    }
    buf.putInt(hand.getShown().size());
    for(MahjongTile t : hand.getShown()) {
      buf.putInt(t.getInternal());
    }
    return buf;
  }

  @Override
  protected void deserializeInternal(ByteBuffer buf) {
    buf.position(0);
    hand = new MahjongHand();
    playerId = buf.getInt();
    int hiddenLength = buf.getInt();
    for(int i = 0; i < hiddenLength; i++) {
      hand.getHidden().add(new MahjongTile(buf.getInt()));
    }
    obscured = hand.getHidden().get(0).equals(MahjongTile.NULL);
    int hiddenKanLength = buf.getInt();
    for(int i = 0; i < hiddenKanLength; i++) {
      hand.getHiddenKan().add(new MahjongTile(buf.getInt()));
    }
    int shownLength = buf.getInt();
    for(int i = 0; i < shownLength; i++) {
      hand.getShown().add(new MahjongTile(buf.getInt()));
    }
  }
}
