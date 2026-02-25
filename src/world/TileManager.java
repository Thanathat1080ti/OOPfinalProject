package world;

import java.awt.Graphics;
import java.io.InputStream;
import java.util.Scanner;
import javax.imageio.ImageIO;

import game.GamePanel;

public class TileManager {
    GamePanel gp;
    public Tile[] tile; 
    public int mapTileNum[][]; 

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10]; 
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow]; 

        getTileType();
        // โหลดแผนที่ด้วย InputStream ที่ถูกต้อง
        loadMap(getClass().getResourceAsStream("/res/map.txt")); 
    }

    public void getTileType() {
        try {
            tile[0] = new Tile(); // grass
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/res/map/grass.png")); 
            
            tile[1] = new Tile(); // wall
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/res/map/wall.png"));
            tile[1].collision = true; 
            
            tile[2] = new Tile(); // water
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/res/map/water.png"));
            tile[2].collision = true; 

            tile[3] = new Tile();   // wood
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/res/map/wood.png"));
            tile[3].collision = true;

            tile[4] = new Tile(); // leaf
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/res/map/leaf.png"));
            tile[4].collision = true;
            
            tile[5] = new Tile(); // grass2
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/res/map/grass2.png"));

            tile[6] = new Tile(); // grass3
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("/res/map/grass3.png"));
            


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap(InputStream is) {
        try {
            Scanner scanner = new Scanner(is); 
            int col = 0;
            int row = 0;

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

    public void draw(Graphics g) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow]; 

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

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