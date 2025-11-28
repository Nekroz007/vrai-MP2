package ch.epfl.cs107.icmaze.actor.util;

public final class Cooldown {
  private float t = 0f;
  private float cd;

  public Cooldown(float cd) {
    this.cd = cd;
  }
  public boolean ready(float dt) {
    t += dt;
    if (t >= cd) { t = 0f; return true; }
    return false;
  }
  public void reset() {
    t = 0f;
  }
}
