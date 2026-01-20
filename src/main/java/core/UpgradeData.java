package core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UpgradeData {

    // The main upgrade map utilizing a hash map and arraylist
    public static final Map<TankType, List<TankType>> UPGRADES = new HashMap<>();

    static {
        // Tier 1
        // Basic
        UPGRADES.put(TankType.BASIC, List.of(TankType.FLANKGUARD, TankType.POUNDER, TankType.SNIPER, TankType.TWIN));

        // Tier2
        // Flank guard
        UPGRADES.put(TankType.FLANKGUARD, List.of(TankType.TRIANGLE, TankType.TWINFLANK));

        // Pounder
        UPGRADES.put(TankType.POUNDER, List.of(TankType.DESTROYER));

        // Sniper
        UPGRADES.put(TankType.SNIPER, List.of(TankType.ASSASSIN));

        // Twin
        UPGRADES.put(TankType.TWIN, List.of(TankType.QUADTANK, TankType.TRIPLESHOT, TankType.TWINFLANK));

        // Tier 3
        // Assassin
        UPGRADES.put(TankType.ASSASSIN, List.of(TankType.RANGER, TankType.STREAMLINER));

        // Destroyer
        UPGRADES.put(TankType.DESTROYER, List.of(TankType.ANNIHILATOR));

        // Quad tank
        UPGRADES.put(TankType.QUADTANK, List.of(TankType.OCTOTANK));

        // Triangle
        UPGRADES.put(TankType.TRIANGLE, List.of(TankType.BOOSTER, TankType.FIGHTER));

        // Triple shot
        UPGRADES.put(TankType.TRIPLESHOT, List.of(TankType.PENTASHOT, TankType.TRIPLET));

        // Twin flank
        UPGRADES.put(TankType.TWINFLANK, List.of(TankType.TRIPLETWIN));
    }

    private UpgradeData() {
    } // prevent instantiation
}
