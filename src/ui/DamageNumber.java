package ui;

import java.awt.Color;

/**
 * คลาสสำหรับแสดงตัวเลขดาเมจแบบง่ายๆ
 */
public class DamageNumber {
    public int value;
    public int worldX, worldY;
    public int screenX, screenY;
    public Color color;
    public int timer = 60; // แสดง 60 เฟรม (1 วินาที)
    public float velocityY = -2.0f; // ความเร็วลอยขึ้น
    
    public DamageNumber(int value, int worldX, int worldY, Color color) {
        this.value = value;
        this.worldX = worldX;
        this.worldY = worldY;
        this.color = color;
    }
    
    public void update() {
        timer--;
        worldY += velocityY; // ลอยขึ้นเรื่อยๆ
        velocityY *= 0.95f; // ลดความเร็วลงเรื่อยๆ
    }
    
    public boolean isExpired() {
        return timer <= 0;
    }
}
