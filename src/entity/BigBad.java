package entity;

import game.GameConfig;
import game.GamePanel;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Random;
import javax.imageio.ImageIO;

public class BigBad extends Entity {
    GamePanel gp;
    int actionLockCounter = 0;
    
    public BigBad(GamePanel gp) {
        this.gp = gp;
        speed = 1;
        direction = "down";
        life = GameConfig.BOSS_LIFE; // บอสเลือดตาม config

        // ขยาย Hitbox ให้ใหญ่ครอบคลุมพื้นที่ 2x2 บล็อก 
        // (ลบขอบออกนิดหน่อยเพื่อให้เดินตามซอกได้ลื่นไหล ไม่ติดมุมง่ายเกินไป)
        solidArea = new Rectangle(12, 24, (gp.tileSize * 2) - 24, (gp.tileSize * 2) - 32); 

        getImage();
    }

    public void getImage() {
        try {
            
            imageUp = ImageIO.read(getClass().getResourceAsStream("/res/bigbad/bigbad_up.png"));
            imageDown = ImageIO.read(getClass().getResourceAsStream("/res/bigbad/bigbad_down.png"));
            imageLeft = ImageIO.read(getClass().getResourceAsStream("/res/bigbad/bigbad_left.png"));
            imageRight = ImageIO.read(getClass().getResourceAsStream("/res/bigbad/bigbad_right.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter == 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;
            if (i <= 25) { direction = "up"; }
            else if (i > 25 && i <= 50) { direction = "down"; }
            else if (i > 50 && i <= 75) { direction = "left"; }
            else if (i > 75 && i <= 100) { direction = "right"; }
            actionLockCounter = 0;
        }
    }

    public void update() {
        setAction(); 
        collisionOn = false;
        gp.cChecker.checkTile(this); 

        // เช็คการชนกับผู้เล่น
        Rectangle slimeHitbox = new Rectangle(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
        Rectangle playerHitbox = new Rectangle(gp.player.worldX + gp.player.solidArea.x, gp.player.worldY + gp.player.solidArea.y, gp.player.solidArea.width, gp.player.solidArea.height);
        
        if (slimeHitbox.intersects(playerHitbox)) {
            gp.player.life -= GameConfig.BOSS_DAMAGE; 
            System.out.println("Attack By BigBad, Current Your HP: " + gp.player.life);
            
            // ให้บอสเด้งถอยหลัง 1 ช่องเวลาชนเรา
            String oppositeDirection = "";
            switch (direction) {
                case "up": oppositeDirection = "down"; break;
                case "down": oppositeDirection = "up"; break;
                case "left": oppositeDirection = "right"; break;
                case "right": oppositeDirection = "left"; break;
            }
            int tempSpeed = speed;
            String tempDirection = direction;
            direction = oppositeDirection;
            speed = gp.tileSize;
            collisionOn = false;
            gp.cChecker.checkTile(this);
            if (collisionOn == false) {
                switch (oppositeDirection) {
                    case "up": worldY -= gp.tileSize; break;
                    case "down": worldY += gp.tileSize; break;
                    case "left": worldX -= gp.tileSize; break;
                    case "right": worldX += gp.tileSize; break;
                }
            }
            speed = tempSpeed;
            direction = tempDirection;
        }

        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }
    }

    public void draw(Graphics g) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // ขยายรัศมีการมองเห็นของกล้องให้ครอบคลุมตัวบอส 2 บล็อก
        if (worldX + (gp.tileSize * 2) > gp.player.worldX - gp.player.screenX &&
            worldX - (gp.tileSize * 2) < gp.player.worldX + gp.player.screenX &&
            worldY + (gp.tileSize * 2) > gp.player.worldY - gp.player.screenY &&
            worldY - (gp.tileSize * 2) < gp.player.worldY + gp.player.screenY) {
            
            Image image = null;
            switch (direction) {
                case "up": image = imageUp; break;
                case "down": image = imageDown; break;
                case "left": image = imageLeft; break;
                case "right": image = imageRight; break;
            }

            // สั่งวาดรูปลงจอ โดยคูณขนาดด้วย 2 (จะได้ขนาด 96x96 หรือ 128x128 ตามที่ตั้ง SCALE ไว้)
            g.drawImage(image, screenX, screenY, gp.tileSize * 2, gp.tileSize * 2, null);
        }
    }
}