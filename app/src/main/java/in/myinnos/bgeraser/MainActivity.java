package in.myinnos.bgeraser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.suke.widget.SwitchButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.switch_button)
    SwitchButton switchButton;
    @BindView(R.id.activity_chooser)
    Button activity_chooser;
    @BindView(R.id.edit_query)
    EditText edit_query;
    @BindView(R.id.input_layout_name)
    TextInputLayout input_layout_name;
    @BindView(R.id.imageView)
    ImageView imageView;

    private Boolean IS_IMAGE_CHOOSER = true;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener((view, isChecked) -> {
            //TODO do your job

            if (isChecked) {
                IS_IMAGE_CHOOSER = false;
                activity_chooser.setText("SUBMIT");
                input_layout_name.setVisibility(View.VISIBLE);

            } else {
                IS_IMAGE_CHOOSER = true;
                activity_chooser.setText(getString(R.string.choose_image));
                input_layout_name.setVisibility(View.GONE);
            }
        });

    }

    @OnClick(R.id.activity_chooser)
    void setActivity_chooser() {
        if (IS_IMAGE_CHOOSER) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        100);
                return;
            }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else {
            String url = edit_query.getText().toString().trim();
            if (BGEraseExtra.isValid(url)) {
                BackgroundEraser.start(MainActivity.this, url, true);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Image Url!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {

            if (data != null) {
                Uri uriImage = data.getData();
                BackgroundEraser.start(MainActivity.this, String.valueOf(uriImage), true);
            }

        } else if (requestCode == BGEraserConstants.REQUEST_CODE) {

            if (data != null) {
                String image = Objects.requireNonNull(data.getExtras()).getString(BGEraserConstants.IMAGE_PATH);
                if (image != null) {
                    Bitmap mBitmap = BGEraseExtra.getBitmap(image, this);
                    imageView.setImageBitmap(mBitmap);
                }
            }

        }
    }
}

