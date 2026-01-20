package core;

import entities.Tank;
import tanks.tier1.Basic;
import tanks.tier2.FlankGuard;
import tanks.tier2.Pounder;
import tanks.tier2.Sniper;
import tanks.tier2.Twin;
import tanks.tier3.*;
import tanks.tier4.*;

public final class TankFactory {
    public static Tank create(TankType type,  float x, float y, float angle) {
        return switch (type) {
            // Tier 1
            case BASIC -> new Basic(x, y, angle, EntityManager.tank, EntityManager.barrel);

            // Tier 2
            case FLANKGUARD -> new FlankGuard(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case POUNDER -> new Pounder(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case SNIPER -> new Sniper(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case TWIN -> new Twin(x, y, angle, EntityManager.tank, EntityManager.barrel);

            // Tier 3
            case ASSASSIN -> new Assassin(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case DESTROYER -> new Destroyer(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case QUADTANK -> new QuadTank(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case TRIANGLE -> new Triangle(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case TRIPLESHOT -> new TripleShot(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case TWINFLANK -> new TwinFlank(x, y, angle, EntityManager.tank, EntityManager.barrel);

            // Tier 4
            case ANNIHILATOR -> new Annihilator(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case BOOSTER -> new Booster(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case FIGHTER -> new Fighter(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case OCTOTANK -> new OctoTank(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case PENTASHOT -> new PentaShot(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case RANGER -> new Ranger(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case STREAMLINER -> new Streamliner(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case TRIPLET ->  new Triplet(x, y, angle, EntityManager.tank, EntityManager.barrel);
            case TRIPLETWIN -> new TripleTwin(x, y, angle, EntityManager.tank, EntityManager.barrel);
        };
    }
}
