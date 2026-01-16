import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newRectangle;

// Tank class represents the player or any AI-controlled tanks
public class Tank extends Entity {
    // Movement and physics properties
    protected float bounceStrength = 0.8f;
    protected float inputVelX = 0;
    protected float inputVelY = 0;
    protected float recoil;
    protected float baseSize = 50f;
    protected float sizeMultiplier = 1.0f;

    // Barrel and weapon properties
    protected float barrelW;
    protected float barrelH;
    protected Color barrelColor = newColor(100, 99, 107, 255);
    protected Color barrelStrokeColor = newColor(55, 55, 55, 255);
    protected Texture barrelTexture;
    protected float reloadSpeed;
    protected float reloadTimer = 0f;

    // Stat levels (0-8 for each stat)
    private int[] stats = new int[8];
    // stats[0]: Health regen
    // stats[1]: Max health
    // stats[2]: Body damage
    // stats[3]: Bullet speed
    // stats[4]: Bullet penetration
    // stats[5]: Bullet damage
    // stats[6]: Reload speed
    // stats[7]: Movement speed

    // Derived combat stats
    private float bulletSpeed;
    private float bulletPenetration;
    private float bulletDamage;
    private float speed; // Kept for compatibility but movement uses acceleration now

    // Progression stats
    private int score;
    private int levelScore;
    private int level;
    private float levelProgress;
    private final int[] levelXP = {4,9,15,22,28,35,44,54,64,75,87,101,117,132,161,161,192,215,251,259,299,322,388,398,450,496,546,600,659,723,791,839,889,942,999,1059,1093,1190,1261,1337,1417,1502,1593,1687, 0};
    private int skillPoints;
    private boolean levelUp = true;
    private boolean upgradeSkill = false;

    // Constructor
    public Tank(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle);
        this.texture = bodyTexture;
        this.barrelTexture = barrelTexture;
        this.color = newColor(24, 158, 140, 255);
        for (int i = 0; i < 8; i++) {
            stats[i] = 0;
        }
        this.alive = true;
        this.level = 1;
        this.score = 0;
        this.levelScore = 0;
        this.skillPoints = 0;
        this.levelProgress = 0f;
        updateStats();
        this.health = maxHealth;
    }

    // Recalculates actual stats based on stat levels and player level
    public void updateStats() {
        // Some formulas are taken from the actual Diep.io game, others are created to fit ours
        healthRegen = 0.1f + (0.4f * stats[0]);
        maxHealth = 50 + 2 * (level - 1) + 20 * stats[1];
        bodyDamage = (20 + 4 * stats[2]);
        bulletSpeed = 200 + 20 * stats[3];
        bulletPenetration = 8 + 6 * stats[4];
        bulletDamage = (7 + 3 * stats[5]);
        reloadSpeed = 0.6f - (0.04f * stats[6]);
        speed = 150 + (10 * stats[7]);

        if (levelUp) {
            health = maxHealth;
            levelUp = false;
        }

        // Tank size scaling: sizeFactor = 1.01 ^ (lvl - 1)
        float sizeFactor = (float) Math.pow(1.01, level - 1);
        size = baseSize * sizeFactor * sizeMultiplier;

        // Update barrel dimensions and recoil based on the new size
        updateDimensions();
    }

    // Update barrel dimensions and recoil - can be overridden by subclasses
    protected void updateDimensions() {
        barrelW = size;
        barrelH = size / 2;
        recoil = barrelH * 1.8f;
    }

    // Main update loop for the tank
    @Override

    public void update() {
        // Handle reload timer
        if (reloadTimer > 0f) {
            reloadTimer -= EntityManager.dt;
        }

        // Check if damaged
        if (timeSinceLastHit > 0.02) isDamage = false;

        if (!alive) timeSinceDeath += EntityManager.dt;

        updateStats();

        // Movement input handling
        handleInput();

        // Passive regeneration
        regenHealth(EntityManager.dt);
    }

    // Handle input
    public void handleInput() {
        float moveX = 0;
        float moveY = 0;

        if (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP)) moveY += 1;
        if (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN)) moveY -= 1;
        if (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT))  moveX -= 1;
        if (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT)) moveX += 1;

        // Diagonal normalization
        if (moveX != 0 && moveY != 0) {
            moveX /= (float) Math.sqrt(2);
            moveY /= (float) Math.sqrt(2);
        }

        inputVelX = moveX * speed;
        inputVelY = moveY * speed;

        velocityX -= velocityX * decay * EntityManager.dt;
        velocityY -= velocityY * decay * EntityManager.dt;

        centerX += (inputVelX + velocityX) * EntityManager.dt;
        centerY += (-inputVelY + velocityY) * EntityManager.dt;

        // World boundary collisions
        checkBounds();

        // Stop jitter
        if (Math.abs(velocityX) < 0.5f) velocityX = 0f;
        if (Math.abs(velocityY) < 0.5f) velocityY = 0f;
    }

    public void checkBounds() {
        if (centerX < 0 && (inputVelX + velocityX) < 0) {
            centerX = 0;
            velocityX = -(inputVelX + velocityX) * bounceStrength - inputVelX;
        }

        if (centerX > EntityManager.worldW  && (inputVelX + velocityX) > 0) {
            centerX = EntityManager.worldW;
            velocityX = -(inputVelX + velocityX) * bounceStrength - inputVelX;
        }

        if (centerY < 0 && (-inputVelY + velocityY) < 0) {
            centerY = 0;
            velocityY = -(-inputVelY + velocityY) * bounceStrength + inputVelY;
        }

        if (centerY > EntityManager.worldH && (-inputVelY + velocityY) > 0) {
            centerY = EntityManager.worldH;
            velocityY = -(-inputVelY + velocityY) * bounceStrength + inputVelY;
        }
    }

    // Draw the tank and its barrel
    @Override
    public void draw() {
        // Barrel
        Rectangle source = newRectangle(0, 0, barrelTexture.width(), barrelTexture.height());
        Rectangle dest = newRectangle(centerX, centerY, barrelW, barrelH);
        Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
        DrawTexturePro(barrelTexture, source, dest, origin, angle * (180f / (float) Math.PI), barrelColor);

        Color currentColor = color;
        if (isDamage) {
            currentColor = RAYWHITE;
        } else if (!alive) {
            currentColor = RED;
        }

        // Tank body
        source = newRectangle(0, 0, texture.width() , texture.height());
        dest = newRectangle(centerX, centerY, size, size);
        origin = new Vector2().x(size / 2).y(size / 2);
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), currentColor);

        if (EntityManager.hitbox) drawHitBox();
        if (health < maxHealth) drawHealthBar();
    }

    // Apply knockback force in the opposite direction of firing
    public void applyRecoil() {
        addVelocity(-recoil * (float) Math.cos(angle), -recoil * (float) Math.sin(angle));
    }

    // Check if the weapon is ready to fire
    public boolean canFire() {
        return reloadTimer <= 0f;
    }

    // Start the reload timer
    public void resetReload() {
        reloadTimer = reloadSpeed;
    }

    // Draw circular hitbox
    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), size / 2, hitboxColor);
    }

    public float getBulletSize() {
        return barrelH;
    }

    public float getBarrelW() { return barrelW; }

    public float getBulletSpeed() { return bulletSpeed; }

    public float getBulletDamage() { return bulletDamage; }

    public float getBulletPenetration() { return bulletPenetration; }

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    public float getLevelProgress() {
        return levelProgress;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public int[] getStats() {
        return stats;
    }

    public void addScore(int amount) {
        score += amount;
        levelScore += amount;
        if (level < 45) {
            levelProgress = (float) levelScore / levelXP[level - 1];
            while (levelScore >= levelXP[level - 1]) {
                levelUp();
                if (level == 45) {
                    levelProgress = 1f;
                    break;
                }
            }
        }
    }

    public void levelUp() {
        levelUp = true;
        levelScore -= levelXP[level - 1];
        level++;
        levelProgress = (float) levelScore / levelXP[level - 1];

        // Skill points according to diep.io (total 33 points at level 45)
        if (level <= 28 || level % 3 == 0) {
            skillPoints++;
            upgradeSkill = true;
        }
    }

    public void upgradeStat(int index) {
        if (skillPoints > 0 && stats[index] < 8) {
            stats[index]++;
            skillPoints--;
            updateStats();
        }
    }

    public int getTotalScore(int level) {
        int total = 0;
        for (int i = 0; i < level - 1; i++) {
            total += levelXP[i];
        }
        return total;
    }

    public boolean isUpgradeSkill() {
        return upgradeSkill;
    }

    public void setUpgradeSkill(boolean upgradeSkill) {
        this.upgradeSkill = upgradeSkill;
    }
}
