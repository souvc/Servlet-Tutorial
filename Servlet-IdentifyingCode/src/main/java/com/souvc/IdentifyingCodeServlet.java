package com.souvc;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 生产验证码
 *
 * @author souvc
 */
//@WebServlet("/image/identifyingCode.img")
public class IdentifyingCodeServlet extends HttpServlet {
    private int imgWidth = 85;
    private int imgHeight = 35;
    private int codeCount = 4;
    private int x = 0;
    private int fontHeight;
    private int codeY;
    private String fontStyle = "Times New Roman";
    private static final long serialVersionUID = 128554012633034503L;

    /**
     * 初始化配置参数
     */
    @Override
    public void init() {
        x = imgWidth / (codeCount + 1);
        fontHeight = imgHeight - 2;
        codeY = imgHeight - 12;
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession(true);

        // 在内存中创建图象
        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        // 获取图形上下文
        Graphics2D g = image.createGraphics();

        // 生成随机类
        Random random = new Random();

        // 设定背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imgWidth, imgHeight);

        // 设定字体
        g.setFont(new Font(fontStyle, Font.PLAIN + Font.ITALIC, fontHeight));

        // 画边框
        g.setColor(new Color(55, 55, 12));
        g.drawRect(0, 0, imgWidth - 1, imgHeight - 1);

        // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(imgWidth);
            int y = random.nextInt(imgHeight);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        // 取随机产生的认证码(4位数字)
        String sRand = "";
        int red = 0, green = 0, blue = 0;
        for (int i = 0; i < codeCount; i++) {
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            int wordType = random.nextInt(3);
            char retWord = 0;
            switch (wordType) {
                case 1:
                    retWord = this.getLowerOrUpperChar(0);
                    break;
                case 2:
                    retWord = this.getLowerOrUpperChar(1);
                    break;
                default:
                    retWord = this.getSingleNumberChar();
                    break;
            }
            sRand += String.valueOf(retWord);
            g.setColor(new Color(red, green, blue));
            g.drawString(String.valueOf(retWord), (i) * x, codeY);

        }

        //生成的验证码
        System.out.println("生成的验证码:"+sRand);

        // 将认证码存入SESSION
        session.setAttribute("rand", sRand);
        // 图象生效
        g.dispose();
        ServletOutputStream responseOutputStream = response.getOutputStream();
        // 输出图象到页面
        ImageIO.write(image, "JPEG", responseOutputStream);

        // 以下关闭输入流！
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    /**
     * 给定范围获得随机颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 处理get请求
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }

    /**
     * 处理post请求
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }

    /**
     * 数字
     *
     * @return
     */
    private char getSingleNumberChar() {
        Random random = new Random();
        int numberResult = random.nextInt(10);
        int ret = numberResult + 48;
        return (char) ret;
    }

    /**
     * 字母
     *
     * @param upper
     * @return
     */
    private char getLowerOrUpperChar(int upper) {
        Random random = new Random();
        int numberResult = random.nextInt(26);
        int ret = 0;
        // 小写
        if (upper == 0) {
            ret = numberResult + 97;

            // 大写
        } else if (upper == 1) {
            ret = numberResult + 65;
        }
        return (char) ret;
    }

}
