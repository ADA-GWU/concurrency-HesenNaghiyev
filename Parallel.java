import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Parallel extends Thread {

    public int imageStartHeight, imageEndHeight;

    public int getImageStartHeight() {
        return imageStartHeight;
    }

    public Parallel setImageStartHeight(int imageStartHeight) {
        this.imageStartHeight = imageStartHeight;
        return this;
    }

    public int getImageEndHeight() {
        return imageEndHeight;
    }

    public void setImageEndHeight(int imageEndHeight) {
        this.imageEndHeight = imageEndHeight;
    }

    @Override
    public void run() {
        ImageProcessor.updateImageInRealTime(imageStartHeight, imageEndHeight);
    }

    /**
     * @param mode
     * @param filePath l divide the image by height and assign the specific height range to each thread.
     */
    public static void executeImageInMultiThreadedMode(String mode, String filePath) {
        ImageProcessor.initializeJFrame(mode);
        BufferedImage bufferedImage = ImageProcessor.readImageFromPath(filePath);

        int coresCount = Runtime.getRuntime().availableProcessors();
        Integer squareSize = ImageProcessor.getSquareSize();
        int amountOfCompleteSquares = bufferedImage.getHeight() / squareSize;
        int squarePerThread = amountOfCompleteSquares / (coresCount - 1);

        ImageProcessor.drawInitialImage();
        int eachThreadArea = squarePerThread * squareSize;
        Parallel[] parallels = new Parallel[coresCount];
        parallels[0] = new Parallel();
        parallels[0].setImageStartHeight(0).setImageEndHeight(eachThreadArea);
        for (int i = 1; i < parallels.length - 1; i++) {
            parallels[i] = new Parallel();
            parallels[i].setImageStartHeight(parallels[i - 1].getImageEndHeight())
                    .setImageEndHeight(parallels[i].getImageStartHeight() + eachThreadArea);
        }
        int lastElementIndex = parallels.length - 1;
        parallels[lastElementIndex] = new Parallel();
        parallels[lastElementIndex].setImageStartHeight(parallels[lastElementIndex - 1].getImageEndHeight())
                .setImageEndHeight(bufferedImage.getHeight());

        Arrays.stream(parallels).forEach(Parallel::start);
        try {
            for (Parallel parallel : parallels) parallel.join();
        } catch (InterruptedException ex) {
            throw new CustomException("Exception occurred while threads finishing their jobs " + ex.getMessage());
        }
        ImageProcessor.saveResultToFile();

    }

}
