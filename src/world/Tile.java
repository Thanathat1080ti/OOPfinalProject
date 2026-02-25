package world;

import java.awt.Image;

// คลาสนี้จะเป็นตัวแทนของ "บล็อก" หรือ "กระเบื้อง" ในเกม
public class Tile {
    
    // ตัวแปรบอกว่าบล็อกนี้ "เดินชน" ได้ไหม (เช่น ถ้านี่คือกำแพง จะมีค่าเป็น true)
    public boolean collision = false;

    public Image image; // รูปภาพของบล็อกนี้ (เช่น รูปหญ้า รูปน้ำ รูปกำแพง ฯลฯ) 

}
