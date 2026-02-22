package entity;

import game.GamePanel;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import javax.swing.ImageIcon;
import java.awt.Image;

public class GreenSlime extends Entity {
    GamePanel gp;
    int actionLockCounter = 0; // ตัวนับเวลาสำหรับการเปลี่ยนทิศทางแบบสุ่ม
    
    
    public GreenSlime(GamePanel gp) {
        this.gp = gp;
        
        // ตั้งค่าพื้นฐานให้สไลม์
        speed = 1; // สไลม์เดินช้ากว่าผู้เล่น
        direction = "down";
        solidArea = new Rectangle(8, 16, 32, 32); // Hitbox ขนาดเดียวกับผู้เล่น
        life = 3; // สไลม์มี HP 3 หน่วย

        getImage();
    }

    public void getImage() {
        imageUp = new ImageIcon("res/slime/slime_up.png").getImage();
        imageDown = new ImageIcon("res/slime/slime_down.png").getImage();
        imageLeft = new ImageIcon("res/slime/slime_left.png").getImage();
        imageRight = new ImageIcon("res/slime/slime_right.png").getImage();
    }

    // AI สุ่มทิศทางการเดิน
    public void setAction() {
        actionLockCounter++;

        // ทุกๆ 120 เฟรม (ประมาณ 2 วินาที) สไลม์จะคิดว่าจะเดินไปทางไหนต่อ
        if (actionLockCounter == 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1; // สุ่มเลข 1-100

            if (i <= 25) { direction = "up"; }
            else if (i > 25 && i <= 50) { direction = "down"; }
            else if (i > 50 && i <= 75) { direction = "left"; }
            else if (i > 75 && i <= 100) { direction = "right"; }

            actionLockCounter = 0; // รีเซ็ตตัวนับ
        }
    }

    public void update() {
        setAction(); // ทิศทาง

        collisionOn = false;
        gp.cChecker.checkTile(this); // เช็คว่าสไลม์เดินชนกำแพงไหม


        // --- เพิ่มลอจิก: เช็คว่าสไลม์เดินชนผู้เล่นไหม ---
        Rectangle slimeHitbox = new Rectangle(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
        Rectangle playerHitbox = new Rectangle(gp.player.worldX + gp.player.solidArea.x, gp.player.worldY + gp.player.solidArea.y, gp.player.solidArea.width, gp.player.solidArea.height);
        if (slimeHitbox.intersects(playerHitbox)) {
            gp.player.life -= 1; // ลดเลือดผู้เล่น
            System.out.println("ํYou attacked by Slime!!  Your current HP: " + gp.player.life);
            
            // --- อัปเดตระบบสไลม์เด้งถอยหลัง (เช็คกำแพง) ---
            // 1. หาว่าทิศตรงข้ามกับที่สไลม์กำลังเดินอยู่คือทิศไหน
            String oppositeDirection = "";
            switch (direction) {
                case "up": oppositeDirection = "down"; break;
                case "down": oppositeDirection = "up"; break;
                case "left": oppositeDirection = "right"; break;
                case "right": oppositeDirection = "left"; break;
            }

            // 2. จำความเร็วและทิศทางปัจจุบันไว้
            int tempSpeed = speed;
            String tempDirection = direction;

            // 3. หลอกระบบให้เช็คระยะเด้งถอยหลัง 1 บล็อก
            direction = oppositeDirection;
            speed = gp.tileSize;
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // 4. ถ้าข้างหลังสไลม์ไม่ใช่กำแพง ให้เด้งถอยหลังไปได้
            if (collisionOn == false) {
                switch (oppositeDirection) {
                    case "up": worldY -= gp.tileSize; break;
                    case "down": worldY += gp.tileSize; break;
                    case "left": worldX -= gp.tileSize; break;
                    case "right": worldX += gp.tileSize; break;
                }
            }

            // 5. คืนค่ากลับเป็นปกติ
            speed = tempSpeed;
            direction = tempDirection;
        }
        // ----------------------------------------

        
        // ถ้าไม่ชนกำแพง ก็ให้เดินไปตามทิศทางที่สุ่มได้
        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }
    }

    // public void draw(Graphics g) {
    //     g.drawImage(imageDown, worldX, worldY, gp.tileSize, gp.tileSize, null);
    // }
    public void draw(Graphics g) {
        // คำนวณระยะห่างของสไลม์เทียบกับผู้เล่น
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // ถ้าสไลม์อยู่ในรัศมีหน้าจอ ค่อยวาด (กันกระตุก)
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            
            Image image = null;

            // เช็คว่า AI สไลม์กำลังหันหน้าไปทางไหน ก็เอารูปนั้นมาเตรียมไว้
            switch (direction) {
                case "up":
                    image = imageUp;
                    break;
                case "down":
                    image = imageDown;
                    break;
                case "left":
                    image = imageLeft;
                    break;
                case "right":
                    image = imageRight;
                    break;
            }

            // สั่งวาดสไลม์ลงบนจอ
            g.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }



}
