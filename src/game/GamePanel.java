package game;

import entity.BigBad;
import entity.GreenSlime;
import entity.Player;
import input.KeyHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import ui.DamageNumber;
import world.CollisionChecker;
import world.TileManager;

public class GamePanel extends JPanel implements Runnable {
    
    // --- ตั้งค่าหน้าจอ (Screen Settings) ---
    public final int tileSize = GameConfig.ORIGINAL_TILE_SIZE * GameConfig.SCALE;
    private final int maxScreenCol = GameConfig.MAX_SCREEN_COL;
    private final int maxScreenRow = GameConfig.MAX_SCREEN_ROW;
    
    private final int screenWidth = tileSize * maxScreenCol; // 768 พิกเซล
    private final int screenHeight = tileSize * maxScreenRow; // 576 พิกเซล

    // --- ตั้งค่าแผนที่โลก (World Settings) ---
    // สร้างโลกให้กว้างใหญ่ 50x50 บล็อก
    public final int maxWorldCol = 50; 
    public final int maxWorldRow = 50;

    // int playerX = 100;
    // int playerY = 100;
    // int playerSpeed = 4;

    // Objects พื้นฐานของเกม
    private final KeyHandler keyH = new KeyHandler(); // สร้างตัวรับปุ่มกด
    private Thread gameThread;  // สร้างตตัวจัดการลูปเกม

    // สร้างตัวเช็คการชน โดยส่ง GamePanel (this)
    public CollisionChecker cChecker = new CollisionChecker(this); 

    // สร้าง Object ตัวละคร โดยส่ง GamePanel (this) และ KeyHandler
    public Player player = new Player(this, keyH);
    public GreenSlime[] greenSlime = new GreenSlime[10];    // สร้างอาร์เรย์สำหรับเก็บสไลม์ได้สูงสุด 10 ตัว
    public BigBad[] bosses = new BigBad[6]; // สร้าง Array สำหรับเก็บ BigBad 6 ตัว


    // ระบบแสดงตัวเลขดาเมจ
    private ArrayList<DamageNumber> damageNumbers = new ArrayList<>();

    // Objects ฉาก
    public TileManager tileM = new TileManager(this); // สร้างตัวจัดการแผนที่ โดยส่ง GamePanel (this) ให้


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));

        this.setBackground(Color.GRAY);

        this.addKeyListener(keyH); // เพิ่ม KeyHandler เป็นตัวรับปุ่มกด
        this.setFocusable(true); // บังคับรับให้ JPanel สามารถรับปุ่มกดได้

        // กำหนดตำแหน่งเริ่มต้นของสไลม์
        // greenSlime.worldX = tileSize * 5; // เกิดที่คอลัมน์ 5
        // greenSlime.worldY = tileSize * 5; // เกิดที่แถว 5
        setupSlime(); // เรียกเมธอดสำหรับสร้างสไลม์ทั้ง 10 ตัว
        setupBosses(); // เรียกเมธอดสำหรับสร้างบอสทั้ง 6 ตัว

    }

    // เมธอดสำหรับเริ่มเกมใหม่
    public void retry() {
        player.setDefaultValues(); // รีเซ็ตตำแหน่งและเลือดของผู้เล่น (เรียกใช้เมธอดเดิมที่มีอยู่แล้ว)
        setupSlime();           // สุ่มเกิดสไลม์ใหม่ทั้งหมด
        setupBosses();          // สุ่มเกิดบอสใหม่ทั้งหมด
    }

    // ลูปเกมหลัก
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Game Loop
    public void run() {
        // เรากำหนด FPS = 60 ใน GameConfig
        double drawInterval = 1000000000.0 / GameConfig.FPS;
        
        // AI Help me
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update(); // อัพเดตตำแหน่งและสถานะต่างๆ
                repaint(); // เรียก paintComponent เพื่อวาดใหม่
                delta--;
            }
        }
    }

    // เมธอดสำหรับเริ่มเกม เรียกสร้างสไลม์ทั้ง 10 ตัว
    public void setupSlime() {
        for (int i = 0; i < greenSlime.length; i++) {
            spawnSlime(i);
        }
    }

    public void setupBosses() {
        for (int i = 0; i < bosses.length; i++) {
            spawnBoss(i);
        }
    }

    public void spawnBoss(int index) {
        if (bosses[index] == null) {
            bosses[index] = new BigBad(this); 
        }
        bosses[index].alive = true;
        bosses[index].life = 15; // รีเซ็ตเลือดบอส

        boolean validPosition = false;
        java.util.Random random = new java.util.Random();

        while (!validPosition) {
            // สุ่มพิกัด (ลบ 1 ไว้เพื่อไม่ให้บอสขนาด 2x2 ไปเกิดชิดขอบโลกเกินไปจน error)
            int randomCol = random.nextInt(maxWorldCol - 1);
            int randomRow = random.nextInt(maxWorldRow - 1);
            
            // เช็คว่าพื้นที่ 2x2 บล็อก (4 ช่อง) ต้องเป็นพื้นที่ว่างทั้งหมด
            int t1 = tileM.mapTileNum[randomCol][randomRow];
            int t2 = tileM.mapTileNum[randomCol + 1][randomRow];
            int t3 = tileM.mapTileNum[randomCol][randomRow + 1];
            int t4 = tileM.mapTileNum[randomCol + 1][randomRow + 1];

            if (tileM.tile[t1].collision == false && tileM.tile[t2].collision == false &&
                tileM.tile[t3].collision == false && tileM.tile[t4].collision == false) {
                
                bosses[index].worldX = randomCol * tileSize;
                bosses[index].worldY = randomRow * tileSize;
                validPosition = true; 
            }
        }
    }



    // // เมธอดสุ่มพิกัดเกิดให้สไลม์ทีละตัว
    // public void spawnSlime(int index) {
    //     greenSlime[index] = new GreenSlime(this); // สร้างสไลม์ตัวใหม่
    //     boolean validPosition = false;
    //     java.util.Random random = new java.util.Random();

    //     while (!validPosition) {
    //         // สุ่มคอลัมน์และแถวในขอบเขตแผนที่โลก (50x50)
    //         int randomCol = random.nextInt(maxWorldCol);
    //         int randomRow = random.nextInt(maxWorldRow);
            
    //         // ดึงข้อมูลว่าช่องที่สุ่มได้ คือบล็อกหมายเลขอะไร
    //         int tileNum = tileM.mapTileNum[randomCol][randomRow];

    //         // เช็คว่าบล็อกนั้น "ไม่มีการชน (collision == false)" เช่น เป็นพื้นหญ้า
    //         if (tileM.tile[tileNum].collision == false) {
    //             // ถ้าเป็นที่ว่าง ก็จับสไลม์ไปวางตรงนั้นเลย
    //             greenSlime[index].worldX = randomCol * tileSize;
    //             greenSlime[index].worldY = randomRow * tileSize;
    //             validPosition = true; // ออกจากลูปสุ่ม
    //         }
    //     }
    // }

    // เมธอดสุ่มพิกัดเกิดให้สไลม์ทีละตัว
    public void spawnSlime(int index) {
        
        // --- 🌟 จุดที่แก้ไข: ใช้เทคนิค Object Pooling รียูสสไลม์ ---
        // เช็คว่าถ้ายังไม่มีสไลม์ในช่องนี้ ถึงจะสร้างใหม่ (โหลดรูปแค่ครั้งเดียวตอนเริ่มเกม)
        if (greenSlime[index] == null) {
            greenSlime[index] = new GreenSlime(this); 
        }
        
        // ชุบชีวิตและรีเซ็ตค่าสถานะให้พร้อมสู้ใหม่
        greenSlime[index].alive = true;
        greenSlime[index].life = GameConfig.SLIME_LIFE; 
        // ----------------------------------------------------

        boolean validPosition = false;
        java.util.Random random = new java.util.Random();

        while (!validPosition) {
            // สุ่มคอลัมน์และแถวในขอบเขตแผนที่โลก (50x50)
            int randomCol = random.nextInt(maxWorldCol);
            int randomRow = random.nextInt(maxWorldRow);
            
            // ดึงข้อมูลว่าช่องที่สุ่มได้ คือบล็อกหมายเลขอะไร
            int tileNum = tileM.mapTileNum[randomCol][randomRow];

            // เช็คว่าบล็อกนั้น "ไม่มีการชน (collision == false)" เช่น เป็นพื้นหญ้า
            if (tileM.tile[tileNum].collision == false) {
                // จับสไลม์ตัวเดิมไปวางตรงพิกัดใหม่
                greenSlime[index].worldX = randomCol * tileSize;
                greenSlime[index].worldY = randomRow * tileSize;
                validPosition = true; // ออกจากลูปสุ่ม
            }
        }
    }

    // เมธอดสำหรับคำนวณตำแหน่งใหม่
    public void update() {
        // --- ถ้ายกตัวละครเลือดหมด (GAME OVER) ---
        if (player.life <= 0) {
            // รอเช็คว่าผู้เล่นกดปุ่ม Enter หรือยัง
            if (keyH.enterPressed == true) {
                retry(); // เรียกเมธอดเริ่มเกมใหม่
            }
        } 
        // --- ถ้ายังมีชีวิตอยู่ (เล่นปกติ) ---
        else {
            player.update(); 
            
            // อัพเดตตัวเลขดาเมจ
            for (int i = damageNumbers.size() - 1; i >= 0; i--) {
                DamageNumber damage = damageNumbers.get(i);
                damage.update();
                
                if (damage.isExpired()) {
                    damageNumbers.remove(i);
                }
            }
            
            for (int i = 0; i < greenSlime.length; i++) {
                if (greenSlime[i] != null) {
                    if (greenSlime[i].alive == true) {
                        greenSlime[i].update(); 
                    } else {
                        spawnSlime(i);
                    }
                }
            }

            for (int i = 0; i < bosses.length; i++) {
                if (bosses[i] != null) {
                    if (bosses[i].alive == true) {
                        bosses[i].update(); 
                    } else {
                        spawnBoss(i); // ถ้าบอสตาย ให้เกิดใหม่
                    }
                }
            }
        }
    }

    
    // เมธอดสำหรับวาด UI (เช่น เลือด, EXP, Lv.)
    public void drawUI(Graphics g) {
        // วาดตัวอักษรบอกสถานะมุมซ้ายบน
        g.setFont(new Font("Arial", Font.BOLD, 24)); // ตั้งค่าฟอนต์ (ชื่อฟอนต์, สไตล์, ขนาด)
        g.setColor(Color.BLACK);
        g.drawString("Lv. " + player.level, 20, 40);
        g.drawString("HP: " + player.life + "/" + player.maxLife, 20, 70);
        g.drawString("EXP: " + player.exp + "/" + player.nextLevelExp, 20, 100);

        // เช็คว่าถ้าผู้เล่นเลือดหมด ให้ขึ้นหน้าจอ Game Over
        if (player.life <= 0) {
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.setColor(Color.RED);
            // คาดคะเนตำแหน่งกึ่งกลางจอ
            g.drawString("GAME OVER", getWidth() / 2 - 250, getHeight() / 2);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.WHITE);
            g.drawString("Press ENTER to Respawn", getWidth() / 2 - 250, getHeight() / 2 + 100);
        }



    }

    // @Override
    // protected void paintComponent(Graphics g) {
    //     super.paintComponent(g);
        
    //     // g.setColor(Color.YELLOW);
    //     // g.fillRect(playerX, playerY, 50, 50);   // ตัวอย่างการวาดสี่เหลี่ยมสีเหลือง

    //     tileM.draw(g); // เรียกเมธอด draw ของ TileManager เพื่อวาดแผนที่
    //     player.draw(g); // เรียกเมธอด draw ของ Player เพื่อวาดตัวละคร
    //     // ถ้าสไลม์ยังมีชีวิตอยู่ ถึงจะยอมวาดรูปมันลงจอ
    //     // if (greenSlime.alive == true) {
    //     //     greenSlime.draw(g); 
    //     // }
    //     // วนลูปวาดสไลม์ทั้ง 10 ตัว
    //     for (int i = 0; i < greenSlime.length; i++) {
    //         if (greenSlime[i] != null && greenSlime[i].alive == true) {
    //             greenSlime[i].draw(g); 
    //         }
    //     }

    //     drawUI(g); // วาด UI (เลือด, EXP, Lv.)
    //     g.dispose(); // ปล่อยทรัพยากรกราฟิกที่ไม่ใช้แล้ว (คืนความจำ)
    // }

@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // --- สิ่งที่ต้องเพิ่ม: อัปเดตจุดกึ่งกลางกล้อง ณ วินาทีที่วาดภาพ ทันที! ---
        player.screenX = getWidth() / 2 - (tileSize / 2);
        player.screenY = getHeight() / 2 - (tileSize / 2);
        // -----------------------------------------------------------

        // คำสั่งวาดภาพด้านล่างนี้ จะใช้ screenX และ screenY ที่ถูกต้องเสมอครับ!
        tileM.draw(g2);
        
        for (GreenSlime slime : greenSlime) {
            if (slime != null && slime.alive == true) {
                slime.draw(g2); 
                // วาดแถบเลือดของสไลม์
                drawHealthBar(g2, slime.worldX, slime.worldY, slime.life, GameConfig.SLIME_LIFE, Color.RED);
            }
        }

        // (ใส่ต่อท้ายลูป for ของ greenSlime ใน paintComponent)
        for (int i = 0; i < bosses.length; i++) {
            if (bosses[i] != null && bosses[i].alive == true) {
                bosses[i].draw(g2); 
                // วาดแถบเลือดของบอส
                drawHealthBar(g2, bosses[i].worldX, bosses[i].worldY, bosses[i].life, GameConfig.BOSS_LIFE, Color.ORANGE);
            }
        }
        
        player.draw(g2); 
        drawUI(g2);
        drawDamageNumbers(g2); // วาดตัวเลขดาเมจ
        
        g2.dispose();
}

    /**
     * แสดงตัวเลขดาเมจ
     */
    public void showDamageNumber(int damage, int worldX, int worldY, Color color) {
        damageNumbers.add(new DamageNumber(damage, worldX, worldY, color));
    }

    /**
     * วาดตัวเลขดาเมจทั้งหมด
     * @param g2 Graphics object
     */
    private void drawDamageNumbers(Graphics2D g2) {
        for (DamageNumber damage : damageNumbers) {
            // คำนวณตำแหน่งบนหน้าจอ
            int screenX = damage.worldX - player.worldX + player.screenX;
            int screenY = damage.worldY - player.worldY + player.screenY;
            
            // วาดตัวเลขดาเมจ
            g2.setColor(damage.color);
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            
            String text = String.valueOf(damage.value);
            g2.drawString(text, screenX, screenY);
        }
    }

    /**
     * วาดแถบเลือด
     */
    private void drawHealthBar(Graphics2D g2, int worldX, int worldY, int currentLife, int maxLife, Color color) {
        // คำนวณตำแหน่งบนหน้าจอ
        int screenX = worldX - player.worldX + player.screenX + 32;
        int screenY = worldY - player.worldY + player.screenY;
        
        // ขนาดแถบเลือด
        int barWidth = 40;
        int barHeight = 6;
        int barY = screenY - 15; // วาดสูงขึ้นไปอีก
        
        // วาดกรอบแถบเลือด (สีดำ)
        g2.setColor(Color.BLACK);
        g2.fillRect(screenX - barWidth/2, barY, barWidth, barHeight);
        
        // วาดแถบเลือด (สีตามพารามิเตอร์)
        int fillWidth = (int)((double)currentLife / maxLife * barWidth);
        g2.setColor(color);
        g2.fillRect(screenX - barWidth/2, barY, fillWidth, barHeight);
        
        // วาดขอบ (สีขาว)
        g2.setColor(Color.WHITE);
        g2.drawRect(screenX - barWidth/2, barY, barWidth, barHeight);
    }
}