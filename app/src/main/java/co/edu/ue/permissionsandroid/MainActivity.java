package co.edu.ue.permissionsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    //1. A nivel general
    private Context context;
    private MainActivity activity;
    //2. Version de Android
    private TextView versionAndroid;
    //Bateria
    private ProgressBar pbLevelBattery;
    private TextView tvLevelBattery;
    private IntentFilter batteryFilter;
    //conexion
    private TextView tvConexion;
    private ConnectivityManager connectivityManager;
    //Flash o Linterna
    private CameraManager cameraManager;
    String cameraId;
    private Button onFlash;
    private Button offFlash;

    //File
    private ClFile clFile;
    private ImageButton btnSaveFile;
    private EditText etNameFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //1. Inicialización
        initObject();
        //Instanciar el filtro
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broReceiver, batteryFilter);

        //Linterna
        onFlash.setOnClickListener(this::onLight);
        offFlash.setOnClickListener(this::offLight);

        //Guardar archivo
        btnSaveFile.setOnClickListener(v -> saveFile());


    }

    private void onLight(View view){
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            }
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        }catch (CameraAccessException e){
            throw new RuntimeException("Error al encender la linterna: "+e.getMessage().toString());
        }
    }

    private void offLight(View view){
        if (cameraManager != null && cameraId != null) {
            try {
                cameraManager.setTorchMode(cameraId, false);
            } catch (CameraAccessException e) {
                throw new RuntimeException("Error al apagar la linterna: " + e.getMessage());
            }
        }
    }


    //1. Inicialización
    private void initObject(){
        this.context = getApplicationContext();
        this.activity = this;
        this.versionAndroid = findViewById(R.id.tvVersionAndroid);
        this.pbLevelBattery = findViewById(R.id.pbLevelBattery);
        this.tvLevelBattery = findViewById(R.id.tvLevelBatteryLB);
        this.tvConexion = findViewById(R.id.tvConexion);
        this.onFlash = findViewById(R.id.btnOn);
        this.offFlash = findViewById(R.id.btnOff);
        this.etNameFile = findViewById(R.id.etNameFile);
        this.btnSaveFile = findViewById(R.id.btnSaveFile);
        clFile = new ClFile(this);
    }

    //2. Version Android
    private void getAndroidVersion(){
        int versionSDK = Build.VERSION.SDK_INT;
        String releaseSO = Build.VERSION.RELEASE;
        this.versionAndroid.setText("Version SO: "+releaseSO+" / SDK: "+ versionSDK);

    }

    @Override
    protected void onResume(){
        super.onResume();
        this.getAndroidVersion();
        this.checkConnection();
    }

    //3. Bateria
    BroadcastReceiver broReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBattery.setProgress(levelBattery);
            tvLevelBattery.setText("El nivel de la bateria es: "+levelBattery+" %");
        }
    };

    //4. Conexión
    private void checkConnection(){
            try {
                connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if ((connectivityManager != null ) || (connectivityManager.equals(null))){
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    boolean stateNet = networkInfo != null && networkInfo.isConnectedOrConnecting();
                    if(stateNet) tvConexion.setText(" State ON");
                    else tvConexion.setText(" State OFF");
                }else{
                    tvConexion.setText(" NOT INFO");
                }
            }catch (Exception e){
                Log.e("CONEXIÓN: ", e.getMessage().toString());
            }
    }

    //5. Guardar Archivo
    private void saveFile() {
        String fileName = etNameFile.getText().toString().trim();
        String androidVersion = "Version SO: " + Build.VERSION.RELEASE + " / SDK: " + Build.VERSION.SDK_INT;
        String batteryLevel = tvLevelBattery.getText().toString();

        String content = androidVersion + "\n" + batteryLevel;

        if (!fileName.isEmpty()) {
            clFile.saveTextFile(fileName, content);
        } else {
            Toast.makeText(this, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show();
        }
    }

}