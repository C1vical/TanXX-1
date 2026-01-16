import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newRectangle;

public class Tank extends Entity {
    protected float bounceStrength = 0.8f;
    protected float inputVelX = 0;
    protected float inputVelY = 0;
    protected float recoil;
    protected float barrelW;
    protected float barrelH;
    protected Color barrelColor = newColor(100, 99, 107, 255);
    protected Color barrelStrokeColor = newColor(55, 55, 55, 255);
    protected Texture barrelTexture;
    protected float reloadSpeed;
    protected float reloadTimer = 0f;

    private int[] stats = new int[8];
    // Health regen
    // Max health
    // Body damage
    // Bullet speed
    // Bullet penetration
    // Bullet damage
    // Reload speed
    // Movement speed

    private float bulletSpeed;
    private float bulletPenetration;
    private float bulletDamage;

    private int score;
    private int levelScore;
    private int level;
    private float levelProgress;
    private int[] levelXP = new int[45];
    private int skillPoints;

    public Tank(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle);
        this.texture = bodyTexture;
        this.barrelTexture = barrelTexture;
        this.color = newColor(24, 158, 140, 255);
        for (int i = 0; i < 8; i++) {
            stats[i] = 0;
        }
        updateStats();
        this.health = maxHealth;
        this.alive = true;
        this.score = 0;
        this.levelScore = 0;
        this.level = 1;
        this.skillPoints = 30;
        this.levelProgress = 0f;
        for (int i = 0; i < levelXP.length; i++) {
            levelXP[i] = 100 + i * 50;
        }
    }

    public void updateStats() {
        healthRegen = 0.1f + (0.4f * stats[0]);
        maxHealth = 50 + 2 * (level - 1) + 20 * stats[1];
        bodyDamage = (20 + 4 * stats[2]);
        bulletSpeed = (5 + 4 * stats[3]) * 20;
        bulletPenetration = 8 + 6 * stats[4];
        bulletDamage = (7 + 3 * stats[5]);
        reloadSpeed = 0.6f - (0.04f * stats[6]);
        speed = 150 + (10 * stats[7]);
    }

    public void update() {
        if (reloadTimer > 0f) {
            reloadTimer -= GameScreen.dt;
        }
        updateStats();

        regenHealth(GameScreen.dt);

        float moveX = 0;
        float moveY = 0;

        if (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP)) moveY += 1;
        if (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN)) moveY -= 1;
        if (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT))  moveX -= 1;
        if (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT)) moveX += 1;

        if (moveX != 0 && moveY != 0) {
            moveX /= (float) Math.sqrt(2);
            moveY /= (float) Math.sqrt(2);
        }

        inputVelX = moveX * speed;
        inputVelY = moveY * speed;

        velocityX -= velocityX * decay * GameScreen.dt;
        velocityY -= velocityY * decay * GameScreen.dt;

        centerX += (inputVelX + velocityX) * GameScreen.dt;
        centerY += (-inputVelY + velocityY) * GameScreen.dt;

        if (centerX < 0 && (inputVelX + velocityX) < 0) {
            centerX = 0;
            velocityX = -(inputVelX + velocityX) * bounceStrength - inputVelX;
        }

        if (centerX > GameScreen.worldW  && (inputVelX + velocityX) > 0) {
            centerX = GameScreen.worldW;
            velocityX = -(inputVelX + velocityX) * bounceStrength - inputVelX;
        }

        if (centerY < 0 && (-inputVelY + velocityY) < 0) {
            centerY = 0;
            velocityY = -(-inputVelY + velocityY) * bounceStrength + inputVelY;
        }

        if (centerY > GameScreen.worldH && (-inputVelY + velocityY) > 0) {
            centerY = GameScreen.worldH;
            velocityY = -(-inputVelY + velocityY) * bounceStrength + inputVelY;
        }

        if (Math.abs(velocityX) < 0.5f) velocityX = 0f;
        if (Math.abs(velocityY) < 0.5f) velocityY = 0f;
    }

    public void draw() {
        // Barrel
        Rectangle source = newRectangle(0, 0, barrelTexture.width(), barrelTexture.height());
        Rectangle dest = newRectangle(centerX, centerY, barrelW, barrelH);
        Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
        DrawTexturePro(barrelTexture, source, dest, origin, angle * (180f / (float) Math.PI), barrelColor);

        // Tank
        source = newRectangle(0, 0, texture.width() , texture.height());
        dest = newRectangle(centerX, centerY, size, size);
        origin = new Vector2().x(size / 2).y(size / 2);
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), color);

        if (GameScreen.hitbox) drawHitBox();
        if (health < maxHealth) drawHealthBar();
    }

    public void applyRecoil() {
        addVelocity(-recoil * (float) Math.cos(angle), -recoil * (float) Math.sin(angle));
    }

    public boolean canFire() {
        return reloadTimer <= 0f;
    }

    public void resetReload() {
        reloadTimer = reloadSpeed;
    }

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

    public void setHealthRegenPoints(int healthRegenPoints) {
        this.stats[0] = healthRegenPoints;
    }

    public void setMaxHealthPoints(int maxHealthPoints) {
        this.stats[1] = maxHealthPoints;
    }

    public void setBodyDamagePoints(int bodyDamagePoints) {
        this.stats[2] = bodyDamagePoints;
    }

    public void setBulletSpeedPoints(int bulletSpeedPoints) {
        this.stats[3] = bulletSpeedPoints;
    }

    public void setBulletPenetrationPoints(int bulletPenetrationPoints) {
        this.stats[4] = bulletPenetrationPoints;
    }

    public void setBulletDamagePoints(int bulletDamagePoints) {
        this.stats[5] = bulletDamagePoints;
    }

    public void setReloadPoints(int reloadPoints) {
        this.stats[6] = reloadPoints;
    }

    public void setMovementSpeedPoints(int movementSpeedPoints) {
        this.stats[7] = movementSpeedPoints;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public float getLevelProgress() {
        return levelProgress;
    }

    public void addScore(int amount) {
        score += amount;
        levelScore += amount;

        while (levelScore >= levelXP[level - 1]) {
            levelScore -= levelXP[level - 1];
            level++;
            
            // Skill points according to diep.io (total 33 points at level 45)
            if (level <= 28 || level % 3 == 0) {
                skillPoints++;
            }

            updateStats();
            health = maxHealth;

            if (level >= 45) {
                level = 45;
                break;
            }
        }

        levelProgress = (float) levelScore / levelXP[level - 1];
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void upgradeStat(int index) {
        if (skillPoints > 0 && stats[index] < 8) {
            stats[index]++;
            skillPoints--;
            updateStats();
        }
    }

    public int[] getStats() {
        return stats;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalScore(int level) {
        int total = 0;
        for (int i = 0; i < level - 1; i++) {
            total += levelXP[i];
        }
        return total;
    }

    public void setLevelProgress(float levelProgress) {
        this.levelProgress = levelProgress;
    }
}
