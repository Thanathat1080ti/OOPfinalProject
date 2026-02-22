package entity;

import java.awt.Image;
import java.awt.Rectangle;


public class Entity {

    // ตัวแปรสำหรับตำแหน่งและความเร็วของเอนทิตี้
    public int worldX, worldY; // ตำแหน่งในโลก
    public int speed; // ความเร็วในการเคลื่อนที่


    public String direction; // ทิศทางที่เอนทิตี้กำลังเคลื่อนที่ (เช่น "up", "down", "left", "right")
    public Rectangle solidArea; // พื้นที่สำหรับตรวจสอบการชน (Hitbox)
    public boolean collisionOn = false; // ตัวแปรบอกว่ามีการชนเกิดขึ้นหรือไม่


    // --- เพิ่มตัวแปรสำหรับเก็บรูปภาพให้ครบ 4 ทิศทาง ---
    public Image imageUp;
    public Image imageDown; 
    public Image imageLeft;
    public Image imageRight;


    public boolean alive = true;    // เกิดมาแล้วมีชีวิต
    public int life;                // HP


}
