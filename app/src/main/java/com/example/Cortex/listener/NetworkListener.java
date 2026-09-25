package com.example.Cortex.listener;

import java.util.List;

/**
 * Callbacks for LAN multiplayer network events.
 * Both the host UI and the client UI subscribe to this interface.
 */
public interface NetworkListener {

    /**
     * Called on the client when the TCP connection to the host succeeds.
     * The host doesn't receive this (it never "connects" to itself).
     */
    void onConnected();

    /**
     * Called whenever the list of players in the room changes.
     * Fired on the host when someone joins or leaves, and on clients
     * when the host broadcasts a new list.
     *
     * @param players The current player names
     */
    void onPlayerListUpdated(List<String> players);

    /**
     * Called when the host has started the game.
     * Clients receive this to transition to the quiz screen.
     */
    void onGameStarting();

    /**
     * Called when the connection has ended — either intentionally
     * (host closed, player left) or unintentionally (network drop).
     *
     * @param reason A short description of why the connection ended
     */
    void onDisconnected(String reason);

    /**
     * Called when a network error occurred while setting up
     * or running the connection.
     *
     * @param error The error message
     */
    void onError(String error);
}