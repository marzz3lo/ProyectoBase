package moonlapse.com.proyectobase;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, LoaderCallbackInterface {

    private static final int SOLICITUD_PERMISO_CAMERA = 0;

    private static final String TAG = "Ej OCV (MainActivity)";
    private CameraBridgeViewBase cameraView;
    private int indiceCamara; // 0-> camara trasera; 1-> camara frontal
    private int cam_anchura = 320;// resolucion deseada de la imagen
    private int cam_altura = 240;
    private static final String STATE_CAMERA_INDEX = "cameraIndex";
    private int tipoEntrada = 0; // 0 -> cámara 1 -> fichero1 2 -> fichero2
    Mat imagenRecurso_;
    boolean recargarRecurso = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        cameraView = (CameraBridgeViewBase) findViewById(R.id.vista_camara);
        cameraView.setCvCameraViewListener(this);

        if (savedInstanceState != null) {
            indiceCamara = savedInstanceState.getInt(STATE_CAMERA_INDEX, 0);
        } else {
            indiceCamara = CameraBridgeViewBase.CAMERA_ID_BACK;
        }
        cameraView.setCameraIndex(indiceCamara);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, SOLICITUD_PERMISO_CAMERA);
            }
        }

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    public void onManagerConnected(int status) {
        switch (status) {
            case LoaderCallbackInterface.SUCCESS:
                Log.i(TAG, "OpenCV se cargo correctamente");
                cameraView.setMaxFrameSize(cam_anchura, cam_altura);
                cameraView.enableView();
                break;
            default:
                Log.e(TAG, "OpenCV no se cargo");
                Toast.makeText(MainActivity.this, "OpenCV no se cargo", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        cam_altura = height;
        cam_anchura = width;
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat entrada;
        if (tipoEntrada == 0) {
            entrada = inputFrame.rgba();
        } else {
            if (recargarRecurso == true) {
                imagenRecurso_ = new Mat();
//Poner aqui el nombre de los archivos copiados
                int RECURSOS_FICHEROS[] = {0, R.raw.img1, R.raw.img2};
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        RECURSOS_FICHEROS[tipoEntrada]);
//Convierte el recurso a una Mat de OpenCV
                Utils.bitmapToMat(bitmap, imagenRecurso_);
                Imgproc.resize(imagenRecurso_, imagenRecurso_, new Size(cam_anchura, cam_altura));
                recargarRecurso = false;
            }
            entrada = imagenRecurso_;
        }
        Mat salida = entrada.clone();
        return salida;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cambiarCamara:
                if (indiceCamara == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    indiceCamara = CameraBridgeViewBase.CAMERA_ID_FRONT;
                } else
                    indiceCamara = CameraBridgeViewBase.CAMERA_ID_BACK;
                recreate();
                break;
            case R.id.resolucion_800x600:
                cam_anchura = 800;
                cam_altura = 600;
                reiniciarResolucion();
                break;
            case R.id.resolucion_640x480:
                cam_anchura = 640;
                cam_altura = 480;
                reiniciarResolucion();
                break;
            case R.id.resolucion_320x240:
                cam_anchura = 320;
                cam_altura = 240;
                reiniciarResolucion();
                break;
            case R.id.entrada_camara:
                tipoEntrada = 0;
                break;
            case R.id.entrada_fichero1:
                tipoEntrada = 1;
                recargarRecurso = true;
                break;
            case R.id.entrada_fichero2:
                tipoEntrada = 2;
                recargarRecurso = true;
                break;
        }
        String msg = "W=" + Integer.toString(cam_anchura) + " H= " +
                Integer.toString(cam_altura) + " Cam= " +
                Integer.toBinaryString(indiceCamara);
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_LONG).show();
        return true;
    }

    public void reiniciarResolucion() {
        cameraView.disableView();
        cameraView.setMaxFrameSize(cam_anchura, cam_altura);
        cameraView.enableView();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
// Save the current camera index.
        savedInstanceState.putInt(STATE_CAMERA_INDEX, indiceCamara);
        super.onSaveInstanceState(savedInstanceState);
    }

    public boolean onTouchEvent(MotionEvent event) {
        openOptionsMenu();
        return true;
    }
}
