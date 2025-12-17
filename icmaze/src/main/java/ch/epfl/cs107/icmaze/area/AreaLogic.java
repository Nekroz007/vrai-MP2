
package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.signal.logic.LogicGate;

public class AreaLogic extends LogicGate {
    private boolean active = false;


    @Override
    public float getIntensity() {
        return active ? 1.0f : 0.0f;
    }
    public void setActive(boolean state){
        active = state;
    }
    public boolean isActive(){
        return active;
    }
    public static class And extends AreaLogic{
        private final AreaLogic a, b;

        public And(AreaLogic a, AreaLogic b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean isOn() {
            return a.isOn() && b.isOn();
        }
    }
}




