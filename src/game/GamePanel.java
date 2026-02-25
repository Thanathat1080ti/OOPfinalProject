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
    
    private final int screenWidth = tileSize * maxScreenCol;
    private final int screenHeight = tileSize * maxScreenRow;

    // --- ตั้งค่าแผนที่โลก (World Settings) ---
    // สร้างโลกให้กว้างใหญ่ขึ้นเป็น 50x50 บล็อก (800x800 พิกเซล)
    public final int maxWorldCol = 50; 
    public final int maxWorldRow = 50;


    private Thread gameThread;  // สร้างตตัวจัดการลูปเกม
    
    // สร้างตัวเช็คการชน โดยส่ง GamePanel (this)
    public CollisionChecker cChecker = new CollisionChecker(this); 
    
    // Objects ตัวรับปุ่มกด
    private final KeyHandler keyH = new KeyHandler(); // สร้างตัวรับปุ่มกด
    
    // สร้าง Object ตัวละคร โดยส่ง GamePanel (this) และ KeyHandler
    public Player player = new Player(this, keyH);
    public GreenSlime[] greenSlime = new GreenSlime[10];    // สร้างอาร์เรย์สำหรับเก็บสไลม์ได้สูงสุด 10 ตัว
    public BigBad[] bosses = new BigBad[6]; // สร้าง Array สำหรับเก็บ BigBad 6 ตัว

    
    // Objects ฉาก
    public TileManager tileM = new TileManager(this); // สร้างตัวจัดการแผนที่ โดยส่ง GamePanel (this) ให้
    
    // ระบบแสดงตัวเลขดาเมจ
    private ArrayList<DamageNumber> damageNumbers = new ArrayList<>();

    // สร้าง Object จากคลาส Sound
    Sound music = new Sound();
    Sound se = new Sound(); // สำหรับ Sound Effects (เสียงสั้นๆ)


    // Constructor
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));

        this.setBackground(Color.GRAY);

        this.addKeyListener(keyH);          // เพิ่ม KeyHandler เป็นตัวรับปุ่มกด
        this.setFocusable(true); // บังคับรับให้ JPanel สามารถรับปุ่มกดได้

        setupSlime(); // เรียกเมธอดสำหรับสร้างสไลม์ทั้ง 10 ตัว
        setupBosses(); // เรียกเมธอดสำหรับสร้างบอสทั้ง 6 ตัว
        setupGame(); // <--- เพิ่มบรรทัดนี้ เพื่อให้เพลงเริ่มทำงานตอนเปิดเกม

    }


    // สร้าง Method สำหรับสั่งเล่นเพลง BGM (Background Music)
    public void playMusic(int i) {
        music.setFile(i, -20.0f);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    // เสียง Effect (เล่นแล้วจบไป)
    public void playSE(int i) {
        se.setFile(i, -5.0f);
        se.play();
    }

    // เรียกใช้ในตอนเริ่มเกม (เช่นใน Constructor หรือ setupGame)
    public void setupGame() {
        playMusic(0); // เล่นเพลงที่ดัชนี 0 (bgm.wav)
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

    // เรียกสร้างสไลม์ทั้ง 10 ตัว
    public void setupSlime() {
        for (int i = 0; i < greenSlime.length; i++) {
            spawnSlime(i);
        }
    }

    // เรียกสร้างบอสทั้ง 6 ตัว
    public void setupBosses() {
        for (int i = 0; i < bosses.length; i++) {
            spawnBoss(i);
        }
    }

    // เมธอดสำหรับสุ่มพิกัดเกิดให้บอสทีละตัว
    public void spawnBoss(int index) {
        if (bosses[index] == null) {
            bosses[index] = new BigBad(this); 
        }
        bosses[index].alive = true;
        bosses[index].life = GameConfig.BOSS_LIFE; // รีเซ็ตเลือดบอสตาม config

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


    // เมธอดสำหรับอัพเดตตำแหน่งและสถานะต่างๆ ของตัวละครและมอนสเตอร์
    public void update() {
        // ถ้ายกตัวละครเลือดหมด (GAME OVER)
        if (player.life <= 0) {
            // รอเช็คว่าผู้เล่นกดปุ่ม Enter หรือยัง
            if (keyH.enterPressed == true) {
                retry(); // เรียกเมธอดเริ่มเกมใหม่
            }
        } 
        // ถ้ายังมีชีวิตอยู่ (เล่นปกติ)
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);                     // เรียก paintComponent ของ JPanel เพื่อเคลียร์หน้าจอและเตรียมวาดใหม่
        Graphics2D g2 = (Graphics2D) g.create();     // สร้าง Graphics2D object ใหม่จาก Graphics เดิม เพื่อให้สามารถปรับแต่งได้มากขึ้น

        // คำนวณตำแหน่งของผู้เล่นให้อยู่ตรงกลางหน้าจอเสมอ
        player.screenX = getWidth() / 2 - (tileSize / 2);
        player.screenY = getHeight() / 2 - (tileSize / 2);
        
        // วาดฉากพื้นหลังและวัตถุต่างๆ
        tileM.draw(g2);
        
        // วาดสไลม์
        for (GreenSlime slime : greenSlime) {
            if (slime != null && slime.alive == true) {
                slime.draw(g2); 
                // วาดแถบเลือดของสไลม์
                drawHealthBar(g2, slime.worldX, slime.worldY, slime.life, GameConfig.SLIME_LIFE, Color.RED);
            }
        }

        // วาดบอส
        for (int i = 0; i < bosses.length; i++) {
            if (bosses[i] != null && bosses[i].alive == true) {
                bosses[i].draw(g2); 
                // วาดแถบเลือดของบอส
                drawBossHealthBar(g2, bosses[i].worldX, bosses[i].worldY, bosses[i].life, GameConfig.BOSS_LIFE, Color.ORANGE);
            }
        }
        
        // วาดตัวละครผู้เล่น
        player.draw(g2); 
        
        drawUI(g2);            // วาด UI (เลือด, EXP, Lv.)
        drawDamageNumbers(g2); // วาดตัวเลขดาเมจ
        g2.dispose();          // ทำลาย Graphics2D object หลังจากวาดเสร็จ เพื่อคืนทรัพยากร
    }


    // เมธอดสำหรับแสดงตัวเลขดาเมจ
    public void showDamageNumber(int damage, int worldX, int worldY, Color color) {
        damageNumbers.add(new DamageNumber(damage, worldX, worldY, color));
    }


    // เมธอดสำหรับวาดดาเมจ
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


    // เมธอดสำหรับวาดแถบเลือด (ใช้สำหรับทั้งสไลม์และบอส)
    private void drawHealthBar(Graphics2D g2, int worldX, int worldY, int currentLife, int maxLife, Color color) {
        // คำนวณตำแหน่งบนหน้าจอ
        int screenX = worldX - player.worldX + player.screenX + 32;
        int screenY = worldY - player.worldY + player.screenY;
        
        // ขนาดแถบเลือด
        int barWidth = 40;
        int barHeight = 6;
        int barY = screenY - 15; // วาดสูงขึ้นไปอีก
        
        drawHealthBar(g2, screenX, screenY, currentLife, maxLife, color, barWidth, barHeight, barY);
    }


    // เมธอดสำหรับวาดแถบเลือดของบอส (ขยายใหญ่ขึ้นและวาดสูงขึ้น)
    private void drawBossHealthBar(Graphics2D g2, int worldX, int worldY, int currentLife, int maxLife, Color color) {
        // คำนวณตำแหน่งบนหน้าจอ (ปรับให้ตรงกลางตัวบอส 2x2)
        int screenX = worldX - player.worldX + player.screenX + tileSize; // ตรงกลางบอส 2x2
        int screenY = worldY - player.worldY + player.screenY;
        
        // ขนาดแถบเลือด (ใหญ่กว่าปกติสำหรับบอส)
        int barWidth = 60;
        int barHeight = 8;
        int barY = screenY - 20; // วาดสูงขึ้นไปอีก
        
        drawHealthBar(g2, screenX, screenY, currentLife, maxLife, color, barWidth, barHeight, barY);
    }


    // เมธอดสำหรับวาดแถบเลือดทั่วไป (รับพารามิเตอร์ทุกอย่างมาเอง)
    private void drawHealthBar(Graphics2D g2, int screenX, int screenY, int currentLife, int maxLife, Color color, int barWidth, int barHeight, int barY) {
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