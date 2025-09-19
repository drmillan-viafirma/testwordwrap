import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        int maxWidth=200;
        String testString= """
                palabra inicial AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed quis nisi tellus. Phasellus pellentesque venenatis neque, id rutrum odio scelerisque sit amet. Aenean bibendum ullamcorper ipsum, eleifend posuere sem finibus ac. Cras ac dictum dolor. Sed et felis interdum, viverra mi vel, dignissim neque. Quisque maximus vitae elit vel fermentum. Ut aliquam magna in cursus luctus. Nulla facilisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin justo mi, malesuada vitae nisl at, laoreet dapibus ex.
                """;

        Font f=new Font("Arial",Font.PLAIN,13);
        testWithImage(testString,f,maxWidth);

        // benchmark(testString,f,maxWidth,300000);

        // testAllFonts(testString,maxWidth);
    }
    public static void testAllFonts(String testString,int maxWidth){
        int[] fontsizes=new int[]{
                0,5,12,20,50,100
        };
        for(TextWrapper textWrapper: Arrays.asList(MJTextWrapper.INSTANCE, DRMTextWrapper.INSTANCE,OriginalTextWrapper.INSTANCE)) {
            Canvas c = new Canvas();
            for (Font f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
                for (int fontSize : fontsizes) {
                    System.out.println("Testing wrapper:"+textWrapper.getClass().getSimpleName()+":" + f.getFontName() + " withSize:" + fontSize);
                    Font font = f.deriveFont(Font.PLAIN, fontSize);
                    FontMetrics fontMetrics = c.getFontMetrics(font);
                    textWrapper.wrap(testString, fontMetrics, maxWidth);
                    System.out.println("Tested wrapper:"+textWrapper.getClass().getSimpleName()+":" + f.getFontName() + " withSize:" + fontSize);
                }
            }
        }
    }
    public static void testWithImage(String string,Font font,int maxWidth){

        FontMetrics metrics = new Canvas().getFontMetrics(font);
        BufferedImage destImage=new BufferedImage(maxWidth,450,BufferedImage.TYPE_INT_RGB);

        List<String> splitted= DRMTextWrapper.INSTANCE.wrap(string, metrics, maxWidth);
        System.out.println(String.join("\n", splitted));
        drawTextOnImage(destImage,splitted,font);
        saveImage(destImage, DRMTextWrapper.class.getSimpleName()+".png");
        System.out.println("-----------");

        splitted= OriginalTextWrapper.INSTANCE.wrap(string, metrics, maxWidth);
        System.out.println(String.join("\n", splitted));
        drawTextOnImage(destImage,splitted,font);
        saveImage(destImage,OriginalTextWrapper.class.getSimpleName()+".png");
        System.out.println("-----------");

        splitted= MJTextWrapper.INSTANCE.wrap(string, metrics, maxWidth);
        System.out.println(String.join("\n", splitted));
        drawTextOnImage(destImage,splitted,font);
        saveImage(destImage, MJTextWrapper.class.getSimpleName()+".png");
    }

    private static void benchmark(String string,Font font, int maxWidth,int repeat){
        for(TextWrapper textWrapper: Arrays.asList(MJTextWrapper.INSTANCE, DRMTextWrapper.INSTANCE,OriginalTextWrapper.INSTANCE)){
            benchmark(string,font,maxWidth,repeat,textWrapper);
        }
    }
    private static void benchmark(String string, Font font, int maxWidth, int repeat, TextWrapper textWrapper){
        FontMetrics metrics = new Canvas().getFontMetrics(font);
        long t=System.currentTimeMillis();
        for(int i=0;i<repeat;i++) {
            textWrapper.wrap(string, metrics, maxWidth);
        }
        System.out.println("TextWrapper: "+textWrapper.getClass().getName()+" "+(System.currentTimeMillis()-t)+" ms");
    }



    private static void saveImage(BufferedImage image, String filename) {
        try {
            javax.imageio.ImageIO.write(image, "png", new java.io.File(filename));
            System.out.println("Image saved to " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void drawTextOnImage(BufferedImage image, List<String> lines, Font font) {
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setColor(Color.BLACK);
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        int y = lineHeight;
        for (String line : lines) {
            g2d.drawString(line, 0, y);
            y += lineHeight;
        }
        g2d.dispose();
    }
}
