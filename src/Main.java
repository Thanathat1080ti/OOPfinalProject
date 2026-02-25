import game.*;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Slime Adventure");
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // X (exit button)
        
        window.setResizable(true); // ปรับขนาดหน้าต่างได้
        
        // GamePanel gamePanel = new GamePanel();
        // window.add(gamePanel);
        MainMenu mainMenu = new MainMenu(window);
        window.add(mainMenu);
        
        // window.setSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        window.pack(); // ให้ JFrame ปรับขนาดตามขนาดของ GamePanel ที่เราจะเพิ่มเข้าไป
        
        window.setLocationRelativeTo(null); // ให้หน้าต่างแสดงตรงกลางจอ
        window.setVisible(true);

        // gamePanel.requestFocusInWindow(); // ให้ JPanel รับโฟกัสเพื่อรับปุ่มกด
        // gamePanel.startGameThread(); // เริ่ม loop เกม
    }
}


