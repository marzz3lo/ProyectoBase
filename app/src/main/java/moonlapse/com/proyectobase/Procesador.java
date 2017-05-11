package moonlapse.com.proyectobase;

import org.opencv.core.Mat;

/**
 * Created by marzzelo on 10/5/2017.
 */

public class Procesador {

    public Procesador() { //Constructor
    }
    public Mat procesa(Mat entrada) {
        Mat salida = entrada.clone();
        return salida;
    }
}
