package com.example.guessmydraw.connection;

import com.example.guessmydraw.connection.messages.DrawMessage;

import java.net.InetAddress;

/**
 * Interface that contains all callback required to receive messages
 */
public interface NetworkEventCallback {
    /**
     * callback called upon arrival of a HandshakeMessage, by which the
     * opponent informs the player of the username used and his address
     */
    void onHandshakeMessageReceived(InetAddress address, String opponentsName);

    /**
     * callback called upon arrival of a DrawMessage, by which the
     * necessary information is sent to replicate the current player's
     * drawing on the opponent's device
     */
    void onDrawMessageReceived(DrawMessage msg);

    /**
     * callback called upon arrival of a AnswerMessage, by which
     * the current player can let the opponent device know
     * the next word to be drawn
     */
    void onAnswerMessageReceived(String answer);

    /**
     * callback called upon arrival of a WinMessage, by which
     * the opponent can let the current player know that
     * the word has been guessed correctly.
     * @param remainingSeconds number of seconds remaining until the timer expires
     */
    void onWinMessageReceived(float remainingSeconds);

    /**
     * callback called upon arrival of a TimerExpired, by which
     * the opponent notifies the current player that the word has
     * not been guessed before the timer runs out
     */
    void onTimerExpiredMessage();

    /**
     * callback called upon arrival of a EndingMessage, by which
     * one player warns the other from the will to stop the game
     */
    void onEndingMessageReceived();

    /**
     * callback called upon arrival of a AckMessage, by which
     * the opponent notifies the current player that the word
     * has been received correctly
     */
    void onAckMessageReceived();

    /**
     * callback called upon arrival of a StartDrawMassage, by which
     * the opponent notifies the current player that it is ready
     * to receive DrawMessages
     */
    void onStartDrawMessageReceived();
}

