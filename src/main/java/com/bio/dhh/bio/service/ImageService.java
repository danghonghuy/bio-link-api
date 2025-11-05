package com.bio.dhh.bio.service;

import com.bio.dhh.bio.dto.GitHubStatsDTO;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImageService {

    private Font baseFont;

    public ImageService() {
        // Load font khi service được tạo
        try {
            this.baseFont = loadFont("ArianaVioleta-dz2K.ttf"); // <<<--- SỬA LẠI TÊN FILE FONT CỦA BẠN
        } catch (Exception e) {
            System.err.println("Không thể load font, dùng font mặc định. Lỗi: " + e.getMessage());
            this.baseFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    private Font loadFont(String fontFileName) throws IOException, FontFormatException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("fonts/" + fontFileName);
        if (is == null) {
            throw new IOException("Không tìm thấy file font trong 'resources/fonts/'");
        }
        return Font.createFont(Font.TRUETYPE_FONT, is);
    }

    public byte[] generateProfileCard(GitHubStatsDTO stats) throws IOException {
        int width = 800;
        int height = 400;

        // Load ảnh nền
        InputStream bgStream = getClass().getClassLoader().getResourceAsStream("card_background.png");
        BufferedImage cardImage = (bgStream != null) ? ImageIO.read(bgStream) : new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = cardImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Vẽ avatar tròn
        try {
            BufferedImage avatar = ImageIO.read(new URL(stats.getAvatarUrl()));
            int avatarSize = 150;
            // Tạo một khuôn hình tròn
            RoundRectangle2D.Double circleClip = new RoundRectangle2D.Double(75, 50, avatarSize, avatarSize, avatarSize, avatarSize);
            g2d.setClip(circleClip);
            g2d.drawImage(avatar, 75, 50, avatarSize, avatarSize, null);
            g2d.setClip(null); // Reset khuôn
            // Vẽ viền tròn cho avatar
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.draw(circleClip);

        } catch (IOException e) {
            // Xử lý lỗi
        }

        // Vẽ tên và username
        g2d.setColor(Color.WHITE);
        g2d.setFont(baseFont.deriveFont(Font.BOLD, 40f));
        g2d.drawString(stats.getName(), 250, 110);

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(baseFont.deriveFont(Font.PLAIN, 24f));
        g2d.drawString("@" + stats.getUsername(), 252, 145);

        // Vẽ các chỉ số (followers, stars...)
        g2d.setColor(Color.WHITE);
        g2d.setFont(baseFont.deriveFont(Font.BOLD, 22f));
        g2d.drawString("Followers: " + stats.getFollowers(), 75, 250);
        g2d.drawString("Total Stars: " + stats.getTotalStars(), 75, 285);
        g2d.drawString("Public Repos: " + stats.getPublicRepos(), 75, 320);

        // Vẽ biểu đồ ngôn ngữ
        drawLanguageChart(g2d, stats.getLanguageStats());

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(cardImage, "png", baos);
        return baos.toByteArray();
    }

    private void drawLanguageChart(Graphics2D g2d, Map<String, Long> langStats) {
        g2d.setFont(baseFont.deriveFont(Font.BOLD, 20f));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Top Languages:", 450, 240);

        if (langStats == null || langStats.isEmpty()) {
            g2d.setFont(baseFont.deriveFont(Font.PLAIN, 18f));
            g2d.setColor(Color.GRAY);
            g2d.drawString("No language data available.", 450, 270);
            return;
        }

        // Sắp xếp và lấy top 3
        List<Map.Entry<String, Long>> sortedLangs = new ArrayList<>(langStats.entrySet());
        sortedLangs.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int barY = 260;
        int barWidth = 300;
        int barHeight = 25;

        for (int i = 0; i < Math.min(3, sortedLangs.size()); i++) {
            Map.Entry<String, Long> entry = sortedLangs.get(i);
            String langName = entry.getKey();
            long percentage = entry.getValue();

            // Vẽ tên ngôn ngữ và %
            g2d.setFont(baseFont.deriveFont(Font.PLAIN, 18f));
            g2d.setColor(Color.WHITE);
            g2d.drawString(langName + " - " + percentage + "%", 450, barY + (barHeight / 2f) + 5);

            // Vẽ thanh background
            g2d.setColor(new Color(60, 60, 60)); // Màu xám tối
            g2d.fill(new RoundRectangle2D.Double(450, barY + barHeight, barWidth, 10, 10, 10));

            // Vẽ thanh %
            g2d.setColor(Color.decode("#33FF99")); // Màu xanh mint
            g2d.fill(new RoundRectangle2D.Double(450, barY + barHeight, barWidth * (percentage / 100.0), 10, 10, 10));

            barY += 45; // Tăng y cho thanh tiếp theo
        }
    }
}