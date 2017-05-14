package moonlapse.com.proyectobase;

import org.opencv.core.Core;
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
        paso_bajo= new Mat();
    }

    private Mat FiltroPasoAlto(Mat entrada)
    {
        Mat salida = new Mat();
        int filter_size = 17;
        Size s=new Size(filter_size,filter_size);
        Imgproc.blur(entrada, paso_bajo, s);
// Hacer la resta. Los valores negativos saturan a cero
        Core.subtract(paso_bajo, entrada, salida);
//Aplicar Ganancia para ver mejor. La multiplicacion satura
        Scalar ganancia = new Scalar(2);
        Core.multiply(salida, ganancia, salida);
        return salida;
    }

    public Mat procesa(Mat entrada) {
        return FiltroPasoAlto(entrada);
    }
}
