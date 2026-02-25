package entity;

import game.GameConfig;
import game.GamePanel;
import input.KeyHandler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends Entity {
    
    private final GamePanel gp;
    private final KeyHandler keyH;
    private int attackCooldown = 0; // เพิ่มตัวแปรหน่วงเวลาฟันดาบ

    // เพิ่มตัวแปรพิกัดสำหรับวาดบนหน้าจอ
    public int screenX;
    public int screenY;

    // --- เพิ่มตัวแปรสถานะ ---
    public int maxLife;
    public int level = 1;
    public int exp = 0;
    public int nextLevelExp = 50; // ต้องใช้ 50 EXP ถึงจะเลเวลอัป

    private boolean attacking = false; // ตัวแปรบอกว่ากำลังฟันดาบอยู่ไหม
    private Image swordUp, swordDown, swordLeft, swordRight; // กระเป๋าเก็บรูปดาบ
    private Image imageUp, imageDown, imageLeft, imageRight; // ตัวแปรสำหรับเก็บรูปภาพผู้เล่น

    
    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        // คำนวณหาจุดกึ่งกลางของหน้าจอ (เอาความกว้างจอหาร 2 แล้วลบด้วยครึ่งนึงของตัวละคร)
        // screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        // screenY = gp.screenHeight / 2 - (gp.tileSize / 2);
        
        // hitbox ของผู้เล่น (พื้นที่สำหรับตรวจสอบการชน)
        solidArea = new Rectangle(8, 16, 32, 32);

        setDefaultValues();
        getPlayerImage();   // โหลดรูปภาพของผู้เล่น ตอนเริ่มเกม
    }

    public final void setDefaultValues() {
        // worldX = 100;   // เกิดที่ x = 100
        // worldY = 100;   // เกิดที่ y = 100
        // เปลี่ยนจุดเกิดให้ไปเกิดกลางๆ แผนที่โลกแทน (เช่น บล็อกที่ 23x21)
        worldX = gp.tileSize * 23;  // 
        worldY = gp.tileSize * 21;  // 

        speed = 10;      // ความเร็วในการเคลื่อนที่
        direction = "down"; // ทิศทางเริ่มต้น

        // --- กำหนดเลือด ---
        maxLife = GameConfig.PLAYER_MAX_LIFE;
        life = maxLife;    // ตอนเริ่มเกมให้เลือดเท่ากับเลือดเต็ม
    }
    private void getPlayerImage() {
        try {
            // เพิ่ม /player/ เข้าไปใน path ด้วยครับ
            imageUp = ImageIO.read(getClass().getResourceAsStream("/res/player/player_up.png"));
            imageDown = ImageIO.read(getClass().getResourceAsStream("/res/player/player_down.png"));
            imageLeft = ImageIO.read(getClass().getResourceAsStream("/res/player/player_left.png"));
            imageRight = ImageIO.read(getClass().getResourceAsStream("/res/player/player_right.png"));
            
            // เพิ่ม /sword/ เข้าไปใน path ด้วยครับ
            swordUp = ImageIO.read(getClass().getResourceAsStream("/res/sword/sword_up.png"));
            swordDown = ImageIO.read(getClass().getResourceAsStream("/res/sword/sword_down.png"));
            swordLeft = ImageIO.read(getClass().getResourceAsStream("/res/sword/sword_left.png"));
            swordRight = ImageIO.read(getClass().getResourceAsStream("/res/sword/sword_right.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // ใช้ gp.getWidth() และ gp.getHeight() เพื่อดึงขนาดหน้าต่างที่ผู้เล่นกำลังดึงยืดอยู่ตลอดเวลา
        // screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        // screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        // อัพเดตตำแหน่งของผู้เล่นตามปุ่มที่ถูกกด
        // เช็คว่ากดปุ่มไหนอยู่ ก็ให้ขยับพิกัด X, Y ไปทางนั้น
        // if (keyH.upPressed) { worldY -= speed; }
        // if (keyH.downPressed) { worldY += speed; }
        // if (keyH.leftPressed) { worldX -= speed; }
        // if (keyH.rightPressed) { worldX += speed; }
    
        // ระบบลดคูลดาวน์: ถ้าค่ามากกว่า 0 จะค่อยๆ ลดลงทุกเฟรมจนเหลือ 0
        if (attackCooldown > 0) {
            attackCooldown--;
            // ถ้าคูลดาวน์ลดลงมาเหลือ 15 เฟรม (ผ่านไปแล้วครึ่งทาง) ให้ซ่อนดาบ
            if (attackCooldown <= 15) { 
                attacking = false;
            }
        }

        // เช็คว่ากดปุ่ม SpaceBar และ คูลดาวน์ฟันดาบพร้อมใช้งาน (เท่ากับ 0) หรือไม่
        if (keyH.attackPressed == true && attackCooldown == 0) {
            
            attacking = true; // เปิดสถานะ "กำลังโจมตี" เพื่อเตรียมวาดดาบ
            
            gp.playSE(1); // ใส่เสียงเหวี่ยงดาบ (วืด!)

            // 1. สร้าง "กล่องดาบ" ขนาดเท่าตัวละคร และขยับไปด้านหน้าตามทิศทางที่หันอยู่
            Rectangle swordHitbox = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
            switch (direction) {
                case "up": swordHitbox.y -= gp.tileSize; break;     // ฟันขึ้น
                case "down": swordHitbox.y += gp.tileSize; break;   // ฟันลง
                case "left": swordHitbox.x -= gp.tileSize; break;   // ฟันซ้าย
                case "right": swordHitbox.x += gp.tileSize; break;  // ฟันขวา
            }

            // // 2. ดึง Hitbox ของสไลม์มาเทียบพิกัด
            // Rectangle slimeHitbox = new Rectangle(gp.greenSlime.worldX + gp.greenSlime.solidArea.x, 
            //                                       gp.greenSlime.worldY + gp.greenSlime.solidArea.y, 
            //                                       gp.greenSlime.solidArea.width, 
            //                                       gp.greenSlime.solidArea.height);

            // // 3. ใช้คำสั่ง .intersects() เช็คว่ากล่องดาบ ทับซ้อนกับ กล่องสไลม์หรือไม่
            // if (gp.greenSlime.alive == true && swordHitbox.intersects(slimeHitbox)) {
            //     gp.greenSlime.life -= 1; // ลดเลือดสไลม์ลง 1
            //     System.out.println("Hit! สไลม์เลือดเหลือ: " + gp.greenSlime.life); // ปริ้นบอกในหน้าต่าง Console
                
            //     // ถ้าเลือดเหลือ 0 ให้สไลม์ตาย
            //     if (gp.greenSlime.life <= 0) {
            //         gp.greenSlime.alive = false; 
            //         System.out.println("สไลม์ถูกกำจัดแล้ว!");
            //         gainExp(25); // ได้ EXP 25 เมื่อฆ่าสไลม์ได้
            //     }
            // }
            

            // --- วนลูปเช็คว่าดาบไปฟันโดนสไลม์ตัวไหนใน 10 ตัวนี้บ้าง ---
            for (int i = 0; i < gp.greenSlime.length; i++) {
                if (gp.greenSlime[i] != null && gp.greenSlime[i].alive == true) {
                    
                    Rectangle slimeHitbox = new Rectangle(
                            gp.greenSlime[i].worldX + gp.greenSlime[i].solidArea.x, 
                            gp.greenSlime[i].worldY + gp.greenSlime[i].solidArea.y, 
                            gp.greenSlime[i].solidArea.width, 
                            gp.greenSlime[i].solidArea.height
                    );

                    if (swordHitbox.intersects(slimeHitbox)) {
                        gp.greenSlime[i].life -= GameConfig.PLAYER_DAMAGE; 
                        
                        // แสดงตัวเลขดาเมจ
                        gp.showDamageNumber(GameConfig.PLAYER_DAMAGE, gp.greenSlime[i].worldX, gp.greenSlime[i].worldY, Color.RED);
                        
                        // --- อัปเดตระบบกระเด็น (เช็คกำแพง) ---
                        int knockbackDistance = gp.tileSize; 
                        
                        // 1. จำทิศทางและความเร็วเดิมของสไลม์เอาไว้ก่อน
                        String tempDirection = gp.greenSlime[i].direction;
                        int tempSpeed = gp.greenSlime[i].speed;

                        // 2. หลอกระบบว่าสไลม์กำลังจะขยับไปในทิศที่โดนฟัน ด้วยความเร็วเท่ากับระยะกระเด็น
                        gp.greenSlime[i].direction = direction; // ทิศเดียวกับที่ผู้เล่นหัน
                        gp.greenSlime[i].speed = knockbackDistance;
                        gp.greenSlime[i].collisionOn = false;

                        // 3. เรียกคลาสฟิสิกส์มาเช็คว่าข้างหน้ามีกำแพงไหม
                        gp.cChecker.checkTile(gp.greenSlime[i]);

                        // 4. ถ้าไม่มีกำแพง (collisionOn == false) ถึงจะอนุญาตให้กระเด็น
                        if (gp.greenSlime[i].collisionOn == false) {
                            switch (direction) {
                                case "up": gp.greenSlime[i].worldY -= knockbackDistance; break;
                                case "down": gp.greenSlime[i].worldY += knockbackDistance; break;
                                case "left": gp.greenSlime[i].worldX -= knockbackDistance; break;
                                case "right": gp.greenSlime[i].worldX += knockbackDistance; break;
                            }
                        }

                        // 5. คืนค่าทิศทางและความเร็วเดิมให้สไลม์กลับไปเดินตามปกติ
                        gp.greenSlime[i].direction = tempDirection;
                        gp.greenSlime[i].speed = tempSpeed;
                        // ---------------------------------------------
                        
                        if (gp.greenSlime[i].life <= 0) {
                            gp.greenSlime[i].alive = false; 
                            System.out.println("Slime defeated!");
                            gainExp(25); 
                        }
                        break;
                    }
                }
            }

            // --- วนลูปเช็คว่าดาบไปฟันโดนบอส (BigBad) ตัวไหนบ้าง ---
            for (int i = 0; i < gp.bosses.length; i++) {
                if (gp.bosses[i] != null && gp.bosses[i].alive == true) {
                    
                    Rectangle bossHitbox = new Rectangle(
                            gp.bosses[i].worldX + gp.bosses[i].solidArea.x, 
                            gp.bosses[i].worldY + gp.bosses[i].solidArea.y, 
                            gp.bosses[i].solidArea.width, 
                            gp.bosses[i].solidArea.height
                    );

                    if (swordHitbox.intersects(bossHitbox)) {
                        gp.bosses[i].life -= GameConfig.PLAYER_DAMAGE; 
                        
                        // แสดงตัวเลขดาเมจ
                        gp.showDamageNumber(GameConfig.PLAYER_DAMAGE, gp.bosses[i].worldX, gp.bosses[i].worldY, Color.RED);
                        
                        // ระบบกระเด็นของบอส
                        int knockbackDistance = gp.tileSize; 
                        
                        String tempDirection = gp.bosses[i].direction;
                        int tempSpeed = gp.bosses[i].speed;

                        gp.bosses[i].direction = direction; 
                        gp.bosses[i].speed = knockbackDistance;
                        gp.bosses[i].collisionOn = false;
                        gp.cChecker.checkTile(gp.bosses[i]);

                        if (gp.bosses[i].collisionOn == false) {
                            switch (direction) {
                                case "up": gp.bosses[i].worldY -= knockbackDistance; break;
                                case "down": gp.bosses[i].worldY += knockbackDistance; break;
                                case "left": gp.bosses[i].worldX -= knockbackDistance; break;
                                case "right": gp.bosses[i].worldX += knockbackDistance; break;
                            }
                        }

                        gp.bosses[i].direction = tempDirection;
                        gp.bosses[i].speed = tempSpeed;
                        
                        if (gp.bosses[i].life <= 0) {
                            gp.bosses[i].alive = false; 
                            System.out.println("กำจัด BigBad ได้แล้ว!");
                            gainExp(100); // 🌟 ฆ่าบอสได้ EXP 100 จุกๆ ไปเลย!
                        }
                        break; // ฟันโดน 1 ตัวแล้วหยุดเช็คดาบทะลุ
                    }
                }
            }

            // ----------------------------------------------------


            // รีเซ็ตคูลดาวน์ให้รอ 30 เฟรม (ประมาณครึ่งวินาที) ถึงจะฟันครั้งต่อไปได้
            attackCooldown = 30; 
        }

        // 1. เช็คว่าผู้เล่นกดปุ่มทิศทางไหนอยู่บ้าง
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            
            if (keyH.upPressed) { direction = "up"; }
            else if (keyH.downPressed) { direction = "down"; }
            else if (keyH.leftPressed) { direction = "left"; }
            else if (keyH.rightPressed) { direction = "right"; }

            // 2. รีเซ็ตสถานะการชนเป็น false ทุกครั้งก่อนคำนวณเฟรมใหม่
            collisionOn = false;
            
            // 3. เรียกใช้ตัวเช็คการชน
            gp.cChecker.checkTile(this);

            // 4. ถ้าสถานะ collisionOn ยังเป็น false (ไม่ชนกำแพง) ถึงจะอนุญาตให้ขยับพิกัด X, Y ได้
            if (collisionOn == false) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
        }
    
    }

    public void draw(Graphics g2) {
        // g2.setColor(Color.WHITE);
        // g2.fillRect(worldX, worldY, 50, 50); // วาดผู้เล่นเป็นสี่เหลี่ยมขาว

        // วาดผู้เล่นเป็นรูปภาพแทนสี่เหลี่ยม
        // g2.drawImage(imageDown, worldX, worldY, gp.tileSize, gp.tileSize, null);
        // g2.drawImage(imageDown, screenX, screenY, gp.tileSize, gp.tileSize, null);

        Image image = null; // สร้างตัวแปรชั่วคราวสำหรับเลือกว่าจะวาดรูปไหน
        
        // เช็คว่าตอนนี้ตัวละครหันหน้าไปทางไหน ก็เอารูปนั้นมาใส่เตรียมไว้
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

        // วาดรูปภาพของผู้เล่นตามทิศทางที่หันอยู่
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);


        // --- วาดดาบเมื่อสถานะ attacking เป็น true ---
        if (attacking == true) {
            int swordScreenX = screenX;
            int swordScreenY = screenY;
            Image swordImage = null;
            // คำนวณหาพิกัดหน้าจอของดาบ และเลือกรูปดาบตามทิศทาง
            switch (direction) {
                case "up":
                    swordScreenY -= gp.tileSize;
                    swordImage = swordUp;
                    break;
                case "down":
                    swordScreenY += gp.tileSize;
                    swordImage = swordDown;
                    break;
                case "left":
                    swordScreenX -= gp.tileSize;
                    swordImage = swordLeft;
                    break;
                case "right":
                    swordScreenX += gp.tileSize;
                    swordImage = swordRight;
                    break;
            }
            g2.drawImage(swordImage, swordScreenX, swordScreenY, gp.tileSize, gp.tileSize, null);
            
        }
    }

    // เมธอดสำหรับเพิ่ม EXP เมื่อฆ่ามอนสเตอร์ได้
    public void gainExp(int amount) {
        exp += amount;
        System.out.println("ได้รับ EXP: " + amount);
        
        // เช็คว่า EXP ถึงเกณฑ์เลเวลอัปหรือยัง
        if (exp >= nextLevelExp) {
            level++; // เพิ่มเลเวล
            exp = exp - nextLevelExp; // หัก EXP ที่ใช้ไป
            nextLevelExp = nextLevelExp * 2; // เลเวลถัดไปใช้ EXP เยอะขึ้น 2 เท่า
            maxLife += 10; // เพิ่มเลือดสูงสุด
            life = maxLife; // ฮีลเลือดให้เต็ม
            System.out.println("Level Up! Current level is " + level);
        }
    }
}