package moonlapse.com.proyectobase;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marzzelo on 10/5/2017.
 */

public class Procesador {

    Mat paso_bajo;

    public Procesador() { //Constructor
        paso_bajo = new Mat();
    }

    private Mat FiltroPasoAlto(Mat entrada) {
        Mat salida = new Mat();
        int filter_size = 17;
        Size s = new Size(filter_size, filter_size);
        Imgproc.blur(entrada, paso_bajo, s);
// Hacer la resta. Los valores negativos saturan a cero
        Core.subtract(paso_bajo, entrada, salida);
//Aplicar Ganancia para ver mejor. La multiplicacion satura
        Scalar ganancia = new Scalar(2);
        Core.multiply(salida, ganancia, salida);
        return salida;
    }

    private Mat Gradiente(Mat entrada) {
        Mat salidatrlocal = new Mat();

        Mat Gx = new Mat();
        Mat Gy = new Mat();
        //Derivada Horizontal
        Imgproc.Sobel(entrada, Gx, CvType.CV_32FC1, 1, 0); //Derivada primera respecto x
        //Derivada Vertical
        Imgproc.Sobel(entrada, Gy, CvType.CV_32FC1, 0, 1); //Derivada primera respecto y
        //Módulo del gradiente
        Mat Gx2 = new Mat();
        Mat Gy2 = new Mat();
        Core.multiply(Gx, Gx, Gx2); //Gx2 = Gx*Gx elemento a elemento
        Core.multiply(Gy, Gy, Gy2); //Gy2 = Gy*Gy elemento a elemento
        Mat ModGrad2 = new Mat();
        Core.add(Gx2, Gy2, ModGrad2);
        Mat ModGrad = new Mat();
        Core.sqrt(ModGrad2, ModGrad);
        //COnvertir a unsigned char
        ModGrad.convertTo(salidatrlocal, CvType.CV_8UC1);
        ModGrad.release();
        ModGrad2.release();
        Gy2.release();
        Gx2.release();
        Gy.release();
        Gx.release();

        return salidatrlocal;
    }

    private Mat residuoDilatacion(Mat entrada) {
        double tam = 11;
        Mat gray = new Mat();
        Mat salidatrlocal = new Mat();

        Mat gray_dilation = new Mat(); // Result

        if (entrada.channels() > 1) // doy supuesto que es color....
        {
            Imgproc.cvtColor(entrada, gray, Imgproc.COLOR_RGBA2GRAY); //Me aseguro que la imagen que llega es gris
        }
        Mat SE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tam, tam));
        Imgproc.dilate(gray, gray_dilation, SE); // 3x3 dilation

        Core.subtract(gray_dilation, gray, salidatrlocal);

        SE.release();
        gray_dilation.release();
        gray.release();

        return salidatrlocal;
    }


    private Mat residuoErosion(Mat entrada) {
        double tam = 11;
        Mat gray = new Mat();
        Mat salidatrlocal = new Mat();
        Mat gray_erosion = new Mat(); // Result

        if (entrada.channels() > 1) // doy supuesto que es color....
        {
            Imgproc.cvtColor(entrada, gray, Imgproc.COLOR_RGBA2GRAY); //Me aseguro que la imagen que llega es gris
        }
        Mat SE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tam, tam));

        Imgproc.erode(gray, gray_erosion, SE); // 3x3 erosion

        Core.subtract(gray, gray_erosion, salidatrlocal);

        SE.release();
        gray_erosion.release();
        gray.release();

        return salidatrlocal;
    }


    public Mat procesa(Mat entrada) {
//        return FiltroPasoAlto(entrada);
//        return Gradiente(entrada);
        return residuoDilatacion(entrada);
    }
}
