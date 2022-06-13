package dev.wateralt.mc.ics4ufinal.server.controllers;

import dev.wateralt.mc.ics4ufinal.common.network.Packet;
import dev.wateralt.mc.ics4ufinal.server.MahjongGame;

/**
 * <h2>Player Controllers API</h2>
 *
 * Each player is an implementation of the Controller implementation.
 * For example, NetworkPlayerController will be a player that is controlled
 * over the network.
 */
public interface Controller {
  /**
   * Method called to initialize the controller.
   * @param state The game state.
   * @param playerId The index of the player that this controller controls.
   */
  void initialize(MahjongGame state, int playerId);

  /**
   * Sends a packet to this controller for processing.
   * @param packet The packet to send.
   */
  void send(Packet packet);

  /**
   * Queries a controller for an incoming move.
   * @param timeout The time to wait for incoming moves, in milliseconds.
   * @return The packet representing the next move by the client.
   */
  Packet receive(int timeout);
}
