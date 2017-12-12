package neuro.fin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
        import android.os.Bundle;
        import android.view.View;
        import android.support.v7.app.AppCompatActivity;
        import android.view.Menu;
        import android.widget.Button;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.content.res.Configuration;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.ViewGroup.LayoutParams;
        import android.view.Window;
        import android.view.WindowManager;

        import android.hardware.Camera.Size;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;

public class MainCamera extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback, Camera.AutoFocusCallback
{
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private SurfaceView preview;
    private Button shotBtn;
    private Button c_menuBtn;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // если хотим, чтобы приложение постоянно имело портретную ориентацию
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // если хотим, чтобы приложение было полноэкранным
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // и без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.camera_main);

        // наше SurfaceView имеет имя SurfaceView01
        preview = (SurfaceView) findViewById(R.id.SurfaceView01);

        surfaceHolder = preview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // кнопка имеет имя Button01
        c_menuBtn = (Button) findViewById(R.id.Camera_menu_button);
        shotBtn = (Button) findViewById(R.id.Button01);
        shotBtn.setText("Shot");
        c_menuBtn.setOnClickListener(this);
        shotBtn.setOnClickListener(this);
    }
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.Camera_menu_button:
                    c_menuBtn.setText("Hi!");
                    break;

            }

        }
    };
    @Override
    protected void onResume()
    {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (camera != null)
        {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Size previewSize = camera.getParameters().getPreviewSize();
        float aspect = (float) previewSize.width / previewSize.height;

        int previewSurfaceWidth = preview.getWidth();
        int previewSurfaceHeight = preview.getHeight();

        LayoutParams lp = preview.getLayoutParams();

        // здесь корректируем размер отображаемого preview, чтобы не было искажений

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
        {
            // портретный вид
            camera.setDisplayOrientation(90);
            lp.height = previewSurfaceHeight;
            lp.width = (int) (previewSurfaceHeight / aspect);
            ;
        }
        else
        {
            // ландшафтный
            camera.setDisplayOrientation(0);
            lp.width = previewSurfaceWidth;
            lp.height = (int) (previewSurfaceWidth / aspect);
        }

        preview.setLayoutParams(lp);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    @Override
    public void onClick(View v)
    {
        if (v == shotBtn)
        {
            // либо делаем снимок непосредственно здесь
            // 	либо включаем обработчик автофокуса

            //camera.takePicture(null, null, null, this);
            camera.autoFocus(this);
        }
        if (v == c_menuBtn)
        {
            Intent intent = new Intent(MainCamera.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // сохраняем полученные jpg в папке /sdcard/CameraExample/
        // имя файла - System.currentTimeMillis()

        try
        {
            File saveDir = new File("/sdcard/CameraExample/");

            if (!saveDir.exists())
            {
                saveDir.mkdirs();
            }

            FileOutputStream os = new FileOutputStream(String.format("/sdcard/CameraExample/%d.jpg", System.currentTimeMillis()));
            os.write(paramArrayOfByte);
            os.close();
        }
        catch (Exception e)
        {
        }

        // после того, как снимок сделан, показ превью отключается. необходимо включить его
        paramCamera.startPreview();
    }

    @Override
    public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
    {
        if (paramBoolean)
        {
            // если удалось сфокусироваться, делаем снимок
            paramCamera.takePicture(null, null, null, this);
        }
    }

    @Override
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // здесь можно обрабатывать изображение, показываемое в preview
    }
}
