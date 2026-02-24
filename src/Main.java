import game.*;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Slime Adventure");
        // window.setSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // X (exit button)
        
        window.setResizable(true); // ไม่ให้ปรับขนาดหน้าต่างได้
        window.setLocationRelativeTo(null); // ให้หน้าต่างแสดงตรงกลางจอ
        
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack(); // ให้ JFrame ปรับขนาดตามขนาดของ GamePanel ที่เราจะเพิ่มเข้าไป

        gamePanel.requestFocusInWindow(); // ให้ JPanel รับโฟกัสเพื่อรับปุ่มกด
        gamePanel.startGameThread(); // เริ่มลูปเกม



    }
}








