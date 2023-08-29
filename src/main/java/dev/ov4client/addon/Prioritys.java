package dev.ov4client.addon;

// idk how to do this better. :c

import dev.ov4client.addon.modules.combat.*;
import dev.ov4client.addon.modules.misc.AutoPearl;
import dev.ov4client.addon.modules.misc.ScaffoldPlus;

public class Prioritys {
    public static int get(Object module) {
        if (module instanceof AnchorAuraPlus) return 9;
        if (module instanceof AntiAim) return 12;
        if (module instanceof AutoCrystalPlus) return 10;
        if (module instanceof AutoHoleFill) return 7;
        if (module instanceof AutoHoleFillPlus) return 7;
        if (module instanceof PistonCrystal) return 10;
        if (module instanceof AutoMine) return 9;
        if (module instanceof AutoPearl) return 6;
        if (module instanceof AutoTrapPlus) return 5;
        if (module instanceof BedBombV4) return 8;
        if (module instanceof KillAura) return 11;
        if (module instanceof ScaffoldPlus) return 2;
        if (module instanceof SelfTrapPlus) return 1;
        if (module instanceof SpeedMine) return 9;
        if (module instanceof SurroundPlus) return 0;

        return 100;
    }
}
