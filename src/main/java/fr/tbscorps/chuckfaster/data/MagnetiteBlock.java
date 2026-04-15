package fr.tbscorps.chuckfaster.data;

import org.bukkit.Location;

public class MagnetiteBlock {

    private final Location location;
    private int diamonds;

    public MagnetiteBlock(Location location, int diamonds) {
        this.location = location;
        this.diamonds = diamonds;
    }

    public Location getLocation() {
        return location;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(int diamonds) {
        this.diamonds = diamonds;
    }

    public void addDiamond() {
        this.diamonds++;
    }

    public boolean removeDiamond() {
        if (diamonds > 0) {
            diamonds--;
            return true;
        }
        return false;
    }

    public double getSpeedBonus() {
        return diamonds * 2.5; // 2.5% par diamant
    }

    public boolean isEmpty() {
        return diamonds == 0;
    }
}