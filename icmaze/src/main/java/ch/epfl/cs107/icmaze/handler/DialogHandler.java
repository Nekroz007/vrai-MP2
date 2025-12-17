package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.play.engine.actor.Dialog;

public interface DialogHandler {
    void publish(Dialog dialog);
}