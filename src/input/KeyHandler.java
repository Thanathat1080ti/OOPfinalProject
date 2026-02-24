package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean attackPressed; // ตัวแปรสำหรับปุ่มโจมตี (SpaceBar)
    // เพิ่มตัวแปรปุ่ม Enter
    public boolean enterPressed;

    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();  // รับค่าจากปุ่มที่ถูกกด
        // ตรวจสอบว่าปุ่มที่ถูกกดคือปุ่มไหน และตั้งค่าตัวแปรให้เป็น true
        if (code == KeyEvent.VK_W) { upPressed = true; }
        if (code == KeyEvent.VK_S) { downPressed = true; }
        if (code == KeyEvent.VK_A) { leftPressed = true; }
        if (code == KeyEvent.VK_D) { rightPressed = true; }

        // เพิ่มปุ่มโจมตี (SpaceBar)
        if (code == KeyEvent.VK_SPACE) {attackPressed = true; }

        // ดักจับปุ่ม Enter
        if (code == KeyEvent.VK_ENTER) { enterPressed = true; }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();  // รับค่าจากปุ่มที่ถูกปล่อย
        // ตรวจสอบว่าปุ่มที่ถูกปล่อยคือปุ่มไหน และตั้งค่าตัวแปรให้เป็น false
        if (code == KeyEvent.VK_W) { upPressed = false; }
        if (code == KeyEvent.VK_S) { downPressed = false; }
        if (code == KeyEvent.VK_A) { leftPressed = false; }
        if (code == KeyEvent.VK_D) { rightPressed = false; }
        if (code == KeyEvent.VK_SPACE) { attackPressed = false; }
        if (code == KeyEvent.VK_ENTER) { enterPressed = false; }
    }

}
