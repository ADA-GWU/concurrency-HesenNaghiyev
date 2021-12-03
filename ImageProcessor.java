import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessor {

    private static final Byte PADDING = 40;
    private static JFrame jFrame;
    private static JLabel jLabel;
    private static Integer squareSize;
    private static Integer imageWidth;
    private static BufferedImage image;
    private static String mode;

    public static Integer getSquareSize() {
        return squareSize;
    }

    /**
     *@param mode this parameters just show in which mode application is running
     *This method just initialize Jframe properties
     */
    static void initializeJFrame(String mode) {
        jFrame = new JFrame(String.format("Processing image in %s", mode));
        jFrame.setVisible(true);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * @param mode(in which mode application is running)
     * @param filePath(FileName)
     * This mode just pack all methods in order to make call from one places
     */
    static void executeInSingleThreadedMode(String mode, String filePath) {
        readImageFromPath(filePath);
        initializeJFrame(mode);
        drawInitialImage();
        updateImageInRealTime(0, image.getHeight());
        saveResultToFile();
    }



    /**
     * @param rgb(color that being set to specific place)
     * @param startI
     * @param startJ
     * @param endI
     * @param endJ
     *This four parameters above is just cordinate of x start x end y start y end and
     * in this method l just color the specific place of image according to square size
     */
    static void setSquareColor(int rgb, int startI, int startJ, int endI, int endJ) {
        for (int i = startI; i < endI; i++)
            for (int j = startJ; j < endJ; j++)
                image.setRGB(j, i, rgb);
    }



    /**
     * @param startI
     * @param startJ
     * @param endI
     * @param endJ
     * @return Returns the average of the RGB array based on the given start and end indexes
     */
    static int getAverageOfRgbArray(int startI, int startJ, int endI, int endJ) {
        int averageColor;
        long sumR = 0;
        long sumG = 0;
        long sumB = 0;
        Color color;

        for (int i = startI; i < endI; i++) {
            for (int j = startJ; j < endJ; j++) {
                color = new Color(image.getRGB(j, i));
                sumR += color.getRed();
                sumG += color.getGreen();
                sumB += color.getBlue();
            }
        }

        int area = squareSize * squareSize;
        averageColor = new Color((int) sumR / area, (int) sumG / area, (int) sumB / area).getRGB();
        return averageColor;
    }

    /**
     * Just Save Result to file
     */
    static void saveResultToFile() {
        try {
            ImageIO.write(image, "jpg", new File("result.jpg"));
        } catch (IOException e) {
            throw new CustomException(String.format("Exception occured while writing final result to image %s", e.getMessage()));
        }
    }

    /**
     * This method is called each time when we update the specific square in image  in order to update image in JFrame
     */
    static void redraw() {
        jLabel.setIcon(new ImageIcon(image));
        jFrame.revalidate();
    }

    /**
     * This method draw initial image based on image width and height dynamically
     */
    static void drawInitialImage() {
        jFrame.setSize(imageWidth, image.getHeight() + PADDING);
        jLabel = new JLabel(new ImageIcon(image));
        jLabel.setSize(imageWidth, image.getHeight());
        jFrame.add(jLabel);
    }

    /**
     * @param filePath
     * @return Get the file path and read it
     */
    static BufferedImage readImageFromPath(String filePath) {
        try {
            image = ImageIO.read(new File(filePath));
            image = resizeTheImage();
            imageWidth = image.getWidth();
            return image;
        } catch (IOException e) {
            throw new CustomException(String.format("Exception occured while reading image %s", e.getMessage()));
        }
    }


     /**
     * @return scaled image
     * This methods is used to resize the big image and return new scaled image
     */
    static BufferedImage resizeTheImage() {
        int width = image.getWidth();
        int height = image.getHeight();
        if (width > 1500) width = width / 3;
        if (height > 1200) height = height / 3;
        BufferedImage outputImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return outputImage;
    }  

    /**
     * @param averageColor
     * @param startI
     * @param startJ
     * @param endI
     * @param endJ
     * This method just combine two methods(setSquare and redraw) above
     */
    static void setSquareAndReDraw(int averageColor, int startI, int startJ, int endI, int endJ) {
        setSquareColor(averageColor, startI, startJ, endI, endJ);
        redraw();
        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * @param imageStartHeight
     * @param imageHeight
     * This method just send indexes to above methods to get Average color of squares and update them in real time
     */
    static void updateImageInRealTime(int imageStartHeight, int imageHeight) {
        for (int i = imageStartHeight; i < imageHeight; i += squareSize) {
            for (int j = 0; j < imageWidth; j += squareSize) {
                if (j + squareSize >= imageWidth && i + squareSize >= imageHeight) {
                    int averageOfRgbArray = getAverageOfRgbArray(i, j, imageHeight, imageWidth);
                    setSquareAndReDraw(averageOfRgbArray, i, j, imageHeight, imageWidth);
                } else if (j + squareSize >= imageWidth) {
                    int averageOfRgbArray = getAverageOfRgbArray(i, j, i + squareSize, imageWidth);
                    setSquareAndReDraw(averageOfRgbArray, i, j, i + squareSize, imageWidth);
                } else if (i + squareSize >= imageHeight) {
                    int averageOfRgbArray = getAverageOfRgbArray(i, j, imageHeight, j + squareSize);
                    setSquareAndReDraw(averageOfRgbArray, i, j, imageHeight, j + squareSize);
                } else {
                    int averageOfRgbArray = getAverageOfRgbArray(i, j, i + squareSize, j + squareSize);
                    setSquareAndReDraw(averageOfRgbArray, i, j, i + squareSize, j + squareSize);
                }
            }
        }
    }

    public static void main(String[] args) {
       String imgPath = args[0];
       squareSize = Integer.parseInt(args[1]);
        mode = args[2];

    

        if (mode.equalsIgnoreCase("S"))
            executeInSingleThreadedMode("Single Mode", imgPath);
        else
            Parallel.executeImageInMultiThreadedMode("MultiThreaded Mode", imgPath);
    }

}
