package src;
import java.awt.Image;


public class ImageScalor {
    public static Image scaleImage(Image image, int previewWidth, int previewHeight){
        int[] scales = calculateScales(image, previewWidth, previewHeight);

        return image.getScaledInstance(scales[0], scales[1], Image.SCALE_SMOOTH);
    }

    private static int[] calculateScales(Image image, int previewWidth, int previewHeight){
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double widthRatio = (double) previewWidth / imageWidth;
        double heightRatio = (double) previewHeight / imageHeight;
        double scale = Math.min(widthRatio, heightRatio);
        int scaledWidth = (int) (scale * imageWidth);
        int scaledHeight = (int) (scale * imageHeight);

        int[] result = new int[2];
        result[0] = scaledWidth;
        result[1] = scaledHeight;
        return result;
    }
}
