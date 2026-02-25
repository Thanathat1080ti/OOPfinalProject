package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class MainMenu extends JPanel {
    private JFrame window;
    private Image bgImage;
    private Sound bgmSound = new Sound();

    public MainMenu(JFrame window) {
        this.window = window;

        // ตั้งค่าหน้าต่างเมนูให้ขนาดเท่ากับหน้าจอเกม
        try {
            bgImage = ImageIO.read(getClass().getResourceAsStream("/res/menu_bg.png"));
        } catch (Exception e) {
            System.out.println("Not found!");
            e.printStackTrace();
        }
        // กำหนดขนาดของ JPanel ให้เท่ากับขนาดที่ต้องการสำหรับเกม
        this.setPreferredSize(new Dimension(GameConfig.MAX_SCREEN_COL * GameConfig.ORIGINAL_TILE_SIZE * GameConfig.SCALE, 
                                            GameConfig.MAX_SCREEN_ROW * GameConfig.ORIGINAL_TILE_SIZE * GameConfig.SCALE));
        // this.setBackground(Color.BLACK); // พื้นหลังสีดำ
        
        // GridBagLayout เพื่อให้ทุกอย่างถูกจัดให้อยู่ "กึ่งกลางจอ" อัตโนมัติ
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0); // ระยะห่าง บน-ล่าง ของแต่ละปุ่ม
        gbc.gridx = 0;

        // สร้างข้อความชื่อเกม (JLabel)
        JLabel titleLabel = new JLabel("Slime Adventure");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 80));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0; // ให้อยู่แถวบนสุด
        this.add(titleLabel, gbc);

        // สร้างปุ่มเริ่มเกม (JButton)
        JButton playButton = new JButton("PLAY GAME");
        playButton.setFont(new Font("Arial", Font.BOLD, 40));
        playButton.setFocusPainted(false); // เอาเส้นประขอบปุ่มออกให้ดูสวยขึ้น
        gbc.gridy = 1; // ให้อยู่แถวที่ 2
        this.add(playButton, gbc);

        // สร้างปุ่มออกเกม (JButton)
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial", Font.BOLD, 40));
        exitButton.setFocusPainted(false);
        gbc.gridy = 2; // ให้อยู่แถวที่ 3
        this.add(exitButton, gbc);

        // ใส่ Event Listener ดักจับการคลิกเมาส์ (ใส่ Event ดักจับการคลิกปุ่ม Play)
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(); // ถ้ากด Play ให้เรียกเมธอดเริ่มเกม
            }
        });

        // ใส่ Event Listener ดักจับการคลิกปุ่ม Exit
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // ถ้ากด Exit ให้ปิดโปรแกรมทันที
            }
        });

        // ใส่ 0 คือเรียกไฟล์ bgm.wav / ใส่ -20.0f คือลดเสียงลงนิดนึงจะได้ไม่หนวกหูไป
        bgmSound.setFile(0, -20.0f); 
        bgmSound.play(); // สั่งเล่น
        bgmSound.loop(); // สั่งวนลูป
    }


    // เมธอดสำหรับวาดรูปพื้นหลัง
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // ถ้าโหลดรูปภาพมาสำเร็จ ให้วาดรูปลงเป็นพื้นหลัง
        if (bgImage != null) {
            // ใช้ getWidth() และ getHeight() เพื่อยืดภาพให้พอดีหน้าต่างเสมอ
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            // ถ้าหารูปไม่เจอ ให้ใช้พื้นหลังสีดำแทน
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }


    // เมธอดสำหรับสลับหน้าจอจากเมนู ไปเป็นหน้าเกม
    private void startGame() {
        // หยุดเสียง BGM ของเมนู
        bgmSound.stop();

        // ลบหน้าเมนูตัวเอง (MainMenu) ออกจากหน้าต่างหลัก
        window.remove(this);
        
        // สร้างหน้าเกม (GamePanel) และยัดเข้าไปแทนที่
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        
        // สั่งให้ JFrame คำนวณ Layout และวาดหน้าต่างใหม่ (สำคัญมาก ไม่งั้นจอดำ)
        window.revalidate();    // คำนวณ Layout ใหม่
        window.repaint();       // วาดหน้าต่างใหม่
        
        // คืนโฟกัสให้กับเกม เพื่อให้ตัวละครรับคีย์บอร์ดเดินได้
        gamePanel.requestFocusInWindow(); 
        gamePanel.startGameThread(); // สตาร์ทลูปเกม
    }
}