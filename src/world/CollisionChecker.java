package world;

import entity.Entity;
import game.GamePanel;

// คลาสนี้จะทำหน้าที่ตรวจสอบการชนของ Entity กับ Tile หรือ Object ต่างๆ ในเกม
public class CollisionChecker {
    
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        // หาพิกัดขอบเขตทั้ง 4 ด้านของ Hitbox ของตัวละคร
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // แปลงพิกัดโลกให้กลายเป็น "คอลัมน์" และ "แถว" ในตารางแผนที่
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        // เช็คว่าถ้ากำลังจะเดินออกนอกจอ (ติดลบ หรือเกินขนาดโลก) ให้หยุดทันที
        if (entityLeftCol < 0 || entityRightCol >= gp.maxWorldCol ||
            entityTopRow < 0 || entityBottomRow >= gp.maxWorldRow) {
            entity.collisionOn = true; // บังคับให้สถานะเป็น "ชน"
            return; // หยุดคำนวณโค้ดด้านล่างทันที ป้องกัน Error -1
        }
        // ----------------------------------------------------------

        int tileNum1, tileNum2; // ใช้ตรวจช่องด้านหน้า 2 มุม (เช่น เดินขึ้น ก็ต้องตรวจมุมซ้ายบนและขวาบนของ Hitbox)
        
        // จำลองก้าวไปข้างหน้า 1 ก้าว ตามทิศทางที่หันอยู่ เพื่อเช็คว่าพิกัดนั้นคือกำแพงหรือไม่
        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;  // ก้าวไปข้างหน้า 1 ก้าว (ความเร็ว) ในทิศทางที่หันอยู่
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];    // ตรวจสอบช่องที่อยู่ตรงหน้ามุมซ้ายบนของ Hitbox
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];   // ตรวจสอบช่องที่อยู่ตรงหน้ามุมขวาบนของ Hitbox
                // ถ้าช่องใดช่องหนึ่งเป็นกำแพง (collision == true) ให้เปิดโหมดการชน
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {  
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;
        }



    }


}

/*

เหตุผลที่เราต้องเช็ค "มุมซ้ายและขวา" (หัวไหล่ทั้งสองข้าง) ของกล่อง Hitbox แทนที่จะเช็คแค่จุดศูนย์กลางด้านหน้าตรงๆ เป็นเพราะเรื่อง "การยืนคร่อมเลน (คร่อมบล็อก)"

ลองจินตนาการภาพ:
โลกในเกมของเราถูกแบ่งเป็นช่องตารางสี่เหลี่ยม (Grid) แต่ตัวละครของเราไม่ได้ถูกล็อกให้เดินทีละช่องแบบหมากรุก ตัวละครของเราเดินแบบอิสระ (Free movement) ด้วยความเร็ว (speed) ทีละพิกเซล ทำให้ บ่อยครั้งตัวละครจะยืนคร่อมอยู่ระหว่าง 2 ช่อง

สมมติว่าตัวละครกำลังเดินขึ้นไปด้านบน (Up) แล้วข้างหน้ามีกำแพงอยู่ทางซ้าย และมีทางเดินว่างอยู่ทางขวา:
[ กำแพง ] [ พื้นว่าง ]  <-- ตารางแผนที่ (Tiles)
  |        |
 [มุมซ้าย]-[มุมขวา]   <-- ขอบบนของกล่อง Hitbox (solidArea)
  | ตัวละคร |




  
ถ้าเราใช้คำสั่ง || (หรือ) ในการเช็ค แปลว่าแค่ "ไหล่ซ้าย" หรือ "ไหล่ขวา" ฝั่งใดฝั่งหนึ่งไปสะกิดโดนกำแพงแม้แต่พิกเซลเดียว 
ตัวละครก็จะหยุดกึกทันที ทำให้การเดินเข้าทางแคบๆ (ขนาดกว้าง 1 บล็อกพอดี) ผู้เล่นจะต้องเล็งให้ตัวละครตรงเป๊ะ 100% ถึงจะเดินเข้าไปได้ ซึ่งมันจะทำให้เกมเพลย์รู้สึก "แข็ง" และน่าหงุดหงิดมาก

แล้วเราแก้ปัญหานี้ยังไงในโค้ดของเรา?
ในไฟล์ Player.java เราได้แอบใส่เทคนิคแก้ปัญหานี้เอาไว้แล้ว นั่นคือการ "ย่อขนาดกล่อง Hitbox ให้เล็กกว่าตัวละคร" ครับ

ลองดูบรรทัดนี้ในโค้ด Player.java:
solidArea = new Rectangle(8, 16, 32, 32);

ขนาดตัวละครและบล็อกทางเดินของเราคือ 48 x 48 พิกเซล
แต่กล่อง Hitbox ของเรากว้างแค่ 32 พิกเซล และถูกดันขยับแกน X เข้ามาตรงกลาง 8 พิกเซล

ภาพจำลองเวลาเดินเข้าทางแคบ:
[ กำแพง ] [ ทางเดินกว้าง 48px ] [ กำแพง ]
           |<- ตัวละคร 48px ->|
            |<-Hitbox 32px->|
จะเห็นว่าเราจงใจปล่อยให้มี "พื้นที่ว่าง (Wiggle Room)" ที่หัวไหล่ซ้ายและขวาฝั่งละ 8 พิกเซล
แปลว่าเวลาผู้เล่นเดินเข้าซอกแคบๆ ต่อให้เดินเบี้ยวไปทางซ้ายหรือขวานิดหน่อย (ไม่เกิน 8 พิกเซล) รูปภาพตัวละครอาจจะดูเหมือนทับซ้อนกับกำแพงไปนิดนึง 
แต่กล่อง Hitbox ล่องหนข้างในยังไม่ชนกำแพง ทำให้ตัวละครสามารถ "ไหล" เข้าไปในซอกแคบๆ ได้อย่างลื่นไหลนั่นเอง



*/



