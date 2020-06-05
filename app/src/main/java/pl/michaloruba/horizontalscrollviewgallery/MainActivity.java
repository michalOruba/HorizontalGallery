package pl.michaloruba.horizontalscrollviewgallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    ViewGroup scrollViewgroup;
    TextView txtMsg;
    int index;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    ImageView imageSelected;

    File[] files;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMsg = findViewById(R.id.txtMsg);
        scrollViewgroup = findViewById(R.id.viewgroup);
        imageSelected = findViewById(R.id.imageSelected);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            verifyStoragePermissions(this);
        }
        else {

            try {
                String path2PicturesOnSdCard = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/thumbnails/";
                File sdPictureFiles = new File(path2PicturesOnSdCard);
                files = sdPictureFiles.listFiles();

                txtMsg.append("\nNum files: " + files.length);
                File file;
                for (index = 0; index < files.length; index++) {
                    file = files[index];
                    final View frame = getLayoutInflater().inflate(R.layout.frame_icon_caption, null);
                    TextView caption = frame.findViewById(R.id.caption);
                    ImageView icon = frame.findViewById(R.id.icon);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inSampleSize = 1; // one-to-one scale
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                    icon.setImageBitmap(bitmap);
                    caption.setText("File-" + index);

                    scrollViewgroup.addView(frame);
                    frame.setId(index);
                    frame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text = "Selected position: " + frame.getId();
                            txtMsg.setText(text);
                            imageSelected.setImageBitmap(showLargeImage(frame.getId()));
                        }
                    });
                }
            } catch (Exception e) {
                txtMsg.append("\nError: " + e.getMessage());
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    protected Bitmap showLargeImage(int frameId) {
        Bitmap bitmap = null;
        try {
            String path = files[frameId].getAbsolutePath().replace("thumbnails/", "").replace("_tn", "");
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1; // one-to-one scale
            bitmap = BitmapFactory.decodeFile(path, bmOptions);
        } catch (Exception e) {
            txtMsg.append("\nError: " + e.getMessage());
        }
        return bitmap;
    }
}