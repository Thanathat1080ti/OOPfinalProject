package world;

import game.GamePanel;
import java.awt.Graphics;
import java.io.File;
import java.util.Scanner;
import javax.swing.ImageIcon;

public class TileManager {
    GamePanel gp;
    public Tile[] tile; // อาร์เรย์ของ Tile ที่จะเก็บข้อมูลของแต่ละประเภทบล็อก
    public int mapTileNum[][]; // อาร์เรย์ 2 มิติ สำหรับเก็บพิกัดของบล็อกในแผนที่

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10]; // สมมติว่าเรามีบล็อกได้สูงสุด 10 ประเภท
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow]; // ขนาดของแผนที่ตามจำนวนคอลัมน์และแถวในโลก

        getTileType();
        loadMap("OOPfinalProject/res/map.txt"); // โหลดแผนที่จากไฟล์
    }

    // กำหนดว่าตัวเลขไหน คือบล็อคอะไร
    public void getTileType() {
        tile[0] = new Tile(); // เบอร์ 0: หญ้า (เดินผ่านได้)
        tile[0].image = new ImageIcon("OOPfinalProject/res/grass.png").getImage();
        
        tile[1] = new Tile(); // เบอร์ 1: กำแพง (เดินชน)
        tile[1].image = new ImageIcon("OOPfinalProject/res/wall.png").getImage();
        tile[1].collision = true; // กำหนดให้บล็อกที่ 1 มี collision = true
        
        tile[2] = new Tile(); // เบอร์ 2: น้ำ (เดินชน)
        tile[2].image = new ImageIcon("OOPfinalProject/res/water.png").getImage();
        tile[2].collision = true; // กำหนดให้บล็อกที่ 2 มี collision = true

    }

    public void loadMap(String filePath) {
        try {
            File mapFile = new File(filePath);
            Scanner scanner = new Scanner(mapFile); 
            int col = 0;
            int row = 0;

            // --- เปลี่ยนจาก maxScreenCol เป็น maxWorldCol ---
            while (col < gp.maxWorldCol && row < gp.maxWorldRow && scanner.hasNextInt()) {
                int num = scanner.nextInt(); 
                mapTileNum[col][row] = num;  
                
                col++; 
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            scanner.close(); 
        } catch (Exception e) {
            System.out.println("Error Loading Map: " + e.getMessage());
        }
    }

    // ฟังก์ชันสำหรับวาดแผนที่ลงหน้าจอ
    // public void draw(Graphics g) {
    //     int col = 0;
    //     int row = 0;
    //     int x = 0;
    //     int y = 0;

    //     // ลูปวาดบล็อกทีละช่องจากซ้ายไปขวา บนลงล่าง จนเต็มหน้าจอ
    //     while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
    //         int tileNum = mapTileNum[col][row]; // ดึงตัวเลขจาก Array ว่าช่องนี้คือบล็อกเบอร์อะไร

    //         // เลือกระบายสีตามตัวเลขแผนที่ (เพื่อให้เห็นภาพง่ายๆ ก่อนเปลี่ยนเป็นรูปภาพจริง)
    //         // if (tileNum == 0) g.setColor(new Color(34, 139, 34)); // สีเขียว (หญ้า)
    //         // if (tileNum == 1) g.setColor(Color.GRAY);             // สีเทา (กำแพง)
    //         // if (tileNum == 2) g.setColor(Color.BLUE);             // สีน้ำเงิน (น้ำ)

    //         // วาดสี่เหลี่ยมขนาดเท่า tileSize
    //         // g.fillRect(x, y, gp.tileSize, gp.tileSize);
            
    //         // วาดรูปภาพของบล็อกตามตัวเลขแผนที่ แทน การระบายสีแบบเดิม
    //         g.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);

            
    //         // ขยับพิกัด X ไปวาดบล็อกถัดไปทางขวา
    //         col++;
    //         x += gp.tileSize;

    //         // ถ้าระบายสีสุดขอบจอฝั่งขวาแล้ว ให้ปัดตกขึ้นบรรทัดใหม่
    //         if (col == gp.maxScreenCol) {
    //             col = 0;          // รีเซ็ตคอลัมน์กลับมาซ้ายสุด
    //             x = 0;            // รีเซ็ตพิกัด X กลับมาซ้ายสุด
    //             row++;            // เลื่อนลง 1 แถว
    //             y += gp.tileSize; // ขยับพิกัด Y ลงมา 1 บล็อก
    //         }
    //     }
    // }
    public void draw(Graphics g) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow]; 

            // 1. หาว่าบล็อกนี้อยู่พิกัดไหนบนโลก
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            
            // 2. คำนวณหาพิกัดบนหน้าจอ (เอาระยะห่างจากผู้เล่น + พิกัดหน้าจอของผู้เล่น)
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            // 3. วาดเฉพาะบล็อกที่มองเห็นบนหน้าจอเท่านั้น (เพื่อลดภาระเครื่อง ไม่ต้องวาดทั้งแมพ 50x50)
            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                
                g.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }

            worldCol++;
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }






}
