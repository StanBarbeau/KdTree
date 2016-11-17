package kdtree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main 
{
	public static void main(String[] args)
    {
        System.out.println("Entrer le nom de l'image à charger :");
        String filename = new Scanner(System.in).nextLine();
        
        try{
            File pathToFile = new File(filename);
            BufferedImage img = ImageIO.read(pathToFile);

            int imgHeight = img.getHeight();
            int imgWidth  = img.getWidth();
            BufferedImage res_img = new BufferedImage(imgWidth, imgHeight, img.getType());

/////////////////////////////////////////////////////////////////
//TODO: replace this naive image copy by the quantization
/////////////////////////////////////////////////////////////////

            ArrayList<Point3i> listCol = new ArrayList<Point3i>(imgHeight*imgWidth);

            for (int y = 0; y < imgHeight; y++) {
                for (int x = 0; x < imgWidth; x++) {
                    int Color = img.getRGB(x,y);
                    int R = (Color >> 16) & 0xff;
                    int G = (Color >> 8) & 0xff;
                    int B = Color & 0xff;

                    listCol.add(new Point3i(R,G,B));

                }
            }

            KdTree buildCol = new KdTree(3,listCol,4);

            ArrayList<Point3i> finalListCol = new ArrayList<Point3i>(16);

            buildCol.getPointsFromLeaf(finalListCol);

            assert (finalListCol.size() == 16);

            KdTree finalTreeCol = new KdTree(3,finalListCol,Integer.MAX_VALUE);

            for (int y = 0; y < imgHeight; y++) {
                for (int x = 0; x < imgWidth; x++) {

                    int Color = img.getRGB(x,y);
                    int R = (Color >> 16) & 0xff;
                    int G = (Color >> 8) & 0xff;
                    int B = Color & 0xff;

                    PointI candidate = finalTreeCol.getNN(new Point3i(R,G,B));

                    int resR = candidate.get(0), resG = candidate.get(1), resB = candidate.get(2);

                    int cRes = 0xff000000 | (resR << 16)
                            | (resG << 8)
                            | resB;
                    res_img.setRGB(x,y,cRes);

                }
            }


/////////////////////////////////////////////////////////////////

            ImageIO.write(res_img, "jpg", new File("ResColor.jpg"));
/////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
