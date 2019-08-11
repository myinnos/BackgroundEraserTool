package in.myinnos.bgeraser;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;

import in.myinnos.bgeraserlib.R;

public class EraserActivity extends Activity implements OnClickListener {
    private Bitmap mBitmap;
    private String imageUrl;
    private Boolean IS_IMAGE_URL = false;
    private Boolean CLOSE_BUTTON_VISIBILTY = true;

    HoverView mHoverView;
    double mDensity;

    int viewWidth;
    int viewHeight;
    int bmWidth;
    int bmHeight;

    int actionBarHeight;
    int bottombarHeight;
    double bmRatio;
    double viewRatio;

    Button eraserMainButton, magicWandMainButton, mirrorButton, positionButton;
    ImageView eraserSubButton, unEraserSubButton;
    ImageView brushSize1Button, brushSize2Button, brushSize3Button, brushSize4Button;
    ImageView magicRemoveButton, magicRestoreButton;
    ImageView undoButton, redoButton, closeButton;
    Button nextButton;
    ImageView colorButton;
    ProgressBar progressBar;

    SeekBar magicSeekbar;
    RelativeLayout eraserLayout, magicLayout;
    RelativeLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eraser);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            CLOSE_BUTTON_VISIBILTY = extras.getBoolean("closeButton", true);
            imageUrl = extras.getString(BGEraserConstants.IMAGE_PATH);

            if (imageUrl != null) {
                if (BGEraseExtra.isValid(imageUrl)) {
                    IS_IMAGE_URL = true;
                }
            } else {
                invalidImage(1);
            }

        } else {
            invalidImage(2);
        }

        // progress bar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        try {
            if (IS_IMAGE_URL) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //URL url = new URL(imageUrl);
                //mBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                mBitmap = resource;
                                setmLayout();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });


            } else {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUrl));
                setmLayout();
            }


        } catch (IOException e) {
            //mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ks2);
            invalidImage(3);
        }

    }

    private void setmLayout() {
        progressBar.setVisibility(View.GONE);
        mLayout = findViewById(R.id.mainLayout);
        mDensity = getResources().getDisplayMetrics().density;
        actionBarHeight = (int) (110 * mDensity);
        bottombarHeight = (int) (60 * mDensity);

        viewWidth = getResources().getDisplayMetrics().widthPixels;
        viewHeight = getResources().getDisplayMetrics().heightPixels - actionBarHeight - bottombarHeight;
        viewRatio = (double) viewHeight / (double) viewWidth;

        bmRatio = (double) mBitmap.getHeight() / (double) mBitmap.getWidth();
        if (bmRatio < viewRatio) {
            bmWidth = viewWidth;
            bmHeight = (int) (((double) viewWidth) * ((double) (mBitmap.getHeight()) / (double) (mBitmap.getWidth())));
        } else {
            bmHeight = viewHeight;
            bmWidth = (int) (((double) viewHeight) * ((double) (mBitmap.getWidth()) / (double) (mBitmap.getHeight())));
        }


        mBitmap = Bitmap.createScaledBitmap(mBitmap, bmWidth, bmHeight, false);

        mHoverView = new HoverView(this, mBitmap, bmWidth, bmHeight, viewWidth, viewHeight);
        mHoverView.setLayoutParams(new LayoutParams(viewWidth, viewHeight));

        mLayout.addView(mHoverView);

        initButton();
    }

    private void invalidImage(int mDensity) {
        Log.d("fromInvalidKey", String.valueOf(mDensity));
        Toast toast = Toast.makeText(getApplicationContext(), "Invalid Image Url", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        finish();
    }

    public void initButton() {
        eraserMainButton = findViewById(R.id.eraseButton);
        eraserMainButton.setOnClickListener(this);
        magicWandMainButton = findViewById(R.id.magicButton);
        magicWandMainButton.setOnClickListener(this);
        mirrorButton = findViewById(R.id.mirrorButton);
        mirrorButton.setOnClickListener(this);
        positionButton = findViewById(R.id.positionButton);
        positionButton.setOnClickListener(this);

        eraserSubButton = findViewById(R.id.erase_sub_button);
        eraserSubButton.setOnClickListener(this);
        unEraserSubButton = findViewById(R.id.unerase_sub_button);
        unEraserSubButton.setOnClickListener(this);

        brushSize1Button = findViewById(R.id.brush_size_1_button);
        brushSize1Button.setOnClickListener(this);

        brushSize2Button = findViewById(R.id.brush_size_2_button);
        brushSize2Button.setOnClickListener(this);

        brushSize3Button = findViewById(R.id.brush_size_3_button);
        brushSize3Button.setOnClickListener(this);

        brushSize4Button = findViewById(R.id.brush_size_4_button);
        brushSize4Button.setOnClickListener(this);

        magicSeekbar = findViewById(R.id.magic_seekbar);
        magicSeekbar.setProgress(15);
        magicSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHoverView.setMagicThreshold(seekBar.getProgress());
                if (mHoverView.getMode() == mHoverView.MAGIC_MODE)
                    mHoverView.magicEraseBitmap();
                else if (mHoverView.getMode() == mHoverView.MAGIC_MODE_RESTORE)
                    mHoverView.magicRestoreBitmap();
                mHoverView.invalidateView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					/*mHoverView.setMagicThreshold(progress);
					if(mHoverView.getMode() == mHoverView.MAGIC_MODE)
						mHoverView.magicEraseBitmap();
					else if(mHoverView.getMode() == mHoverView.MAGIC_MODE_RESTORE)
						mHoverView.magicRestoreBitmap();
					mHoverView.invalidateView();*/
            }
        });

        magicRemoveButton = findViewById(R.id.magic_remove_button);
        magicRemoveButton.setOnClickListener(this);
        magicRestoreButton = findViewById(R.id.magic_restore_button);
        magicRestoreButton.setOnClickListener(this);

        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);

        if (!CLOSE_BUTTON_VISIBILTY) {
            closeButton.setVisibility(View.GONE);
        }

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        undoButton = findViewById(R.id.undoButton);
        undoButton.setOnClickListener(this);

        redoButton = findViewById(R.id.redoButton);
        redoButton.setOnClickListener(this);
        updateRedoButton();

        eraserLayout = findViewById(R.id.eraser_layout);
        magicLayout = findViewById(R.id.magicWand_layout);
        eraserMainButton.setSelected(true);

        colorButton = findViewById(R.id.colorButton);
        colorButton.setOnClickListener(this);
    }

    public void resetSeekBar() {
        magicSeekbar.setProgress(0);
        mHoverView.setMagicThreshold(0);
    }

    int currentColor = 0;

    public void setBackGroundColor(int color) {
        switch (color) {
            case 0:
                mLayout.setBackgroundResource(R.drawable.bg);
                colorButton.setBackgroundResource(R.drawable.white_drawable);
                break;
            case 1:
                mLayout.setBackgroundColor(Color.WHITE);
                colorButton.setBackgroundResource(R.drawable.black_drawable);
                break;
            case 2:
                mLayout.setBackgroundColor(Color.BLACK);
                colorButton.setBackgroundResource(R.drawable.transparent_drawable);
                break;

            default:
                break;
        }

        currentColor = color;
    }

    public void resetMainButtonState() {
        eraserMainButton.setSelected(false);
        magicWandMainButton.setSelected(false);
        mirrorButton.setSelected(false);
        positionButton.setSelected(false);
    }

    public void resetSubEraserButtonState() {
        eraserSubButton.setSelected(false);
        unEraserSubButton.setSelected(false);
    }

    public void resetSubMagicButtonState() {
        magicRemoveButton.setSelected(false);
        magicRestoreButton.setSelected(false);
    }

    public void resetBrushButtonState() {
        brushSize1Button.setSelected(false);
        brushSize2Button.setSelected(false);
        brushSize3Button.setSelected(false);
        brushSize4Button.setSelected(false);
    }

    public void updateUndoButton() {
        if (mHoverView.checkUndoEnable()) {
            undoButton.setEnabled(true);
            undoButton.setAlpha(1.0f);
        } else {
            undoButton.setEnabled(false);
            undoButton.setAlpha(0.3f);
        }
    }

    public void updateRedoButton() {
        if (mHoverView.checkRedoEnable()) {
            redoButton.setEnabled(true);
            redoButton.setAlpha(1.0f);
        } else {
            redoButton.setEnabled(false);
            redoButton.setAlpha(0.3f);
        }
    }

    @Override
    public void onClick(View v) {
        updateUndoButton();
        updateRedoButton();

        int i = v.getId();
        if (i == R.id.eraseButton) {
            mHoverView.switchMode(mHoverView.ERASE_MODE);
            if (eraserLayout.getVisibility() == View.VISIBLE) {
                eraserLayout.setVisibility(View.GONE);
            } else {
                eraserLayout.setVisibility(View.VISIBLE);
            }
            magicLayout.setVisibility(View.GONE);
            resetMainButtonState();
            resetSubEraserButtonState();
            eraserSubButton.setSelected(true);
            eraserMainButton.setSelected(true);
        } else if (i == R.id.magicButton) {
            mHoverView.switchMode(HoverView.MAGIC_MODE);
            if (magicLayout.getVisibility() == View.VISIBLE) {
                magicLayout.setVisibility(View.GONE);
            } else {
                magicLayout.setVisibility(View.VISIBLE);
            }
            eraserLayout.setVisibility(View.GONE);
            resetMainButtonState();
            resetSubMagicButtonState();
            resetSeekBar();
            magicRemoveButton.setSelected(true);
            magicWandMainButton.setSelected(true);
        } else if (i == R.id.mirrorButton) {
            findViewById(R.id.eraser_layout).setVisibility(View.GONE);
            findViewById(R.id.magicWand_layout).setVisibility(View.GONE);
            mHoverView.mirrorImage();
        } else if (i == R.id.positionButton) {
            mHoverView.switchMode(HoverView.MOVING_MODE);
            findViewById(R.id.magicWand_layout).setVisibility(View.GONE);
            findViewById(R.id.eraser_layout).setVisibility(View.GONE);
            resetMainButtonState();
            positionButton.setSelected(true);
        } else if (i == R.id.erase_sub_button) {
            mHoverView.switchMode(HoverView.ERASE_MODE);
            resetSubEraserButtonState();
            eraserSubButton.setSelected(true);
        } else if (i == R.id.unerase_sub_button) {
            mHoverView.switchMode(HoverView.UNERASE_MODE);
            resetSubEraserButtonState();
            unEraserSubButton.setSelected(true);
        } else if (i == R.id.brush_size_1_button) {
            resetBrushButtonState();
            mHoverView.setEraseOffset(40);
            brushSize1Button.setSelected(true);
        } else if (i == R.id.brush_size_2_button) {
            resetBrushButtonState();
            mHoverView.setEraseOffset(60);
            brushSize2Button.setSelected(true);
        } else if (i == R.id.brush_size_3_button) {
            resetBrushButtonState();
            mHoverView.setEraseOffset(80);
            brushSize3Button.setSelected(true);
        } else if (i == R.id.brush_size_4_button) {
            resetBrushButtonState();
            mHoverView.setEraseOffset(100);
            brushSize4Button.setSelected(true);
        } else if (i == R.id.magic_remove_button) {
            resetSubMagicButtonState();
            magicRemoveButton.setSelected(true);
            mHoverView.switchMode(HoverView.MAGIC_MODE);
            resetSeekBar();
        } else if (i == R.id.magic_restore_button) {
            resetSubMagicButtonState();
            magicRestoreButton.setSelected(true);
            mHoverView.switchMode(HoverView.MAGIC_MODE_RESTORE);
            resetSeekBar();
        } else if (i == R.id.colorButton) {
            setBackGroundColor((currentColor + 1) % 3);
        } else if (i == R.id.undoButton) {
            findViewById(R.id.eraser_layout).setVisibility(View.GONE);
            findViewById(R.id.magicWand_layout).setVisibility(View.GONE);
            mHoverView.undo();
            if (mHoverView.checkUndoEnable()) {
                undoButton.setEnabled(true);
                undoButton.setAlpha(1.0f);
            } else {
                undoButton.setEnabled(false);
                undoButton.setAlpha(0.3f);
            }
            updateRedoButton();
        } else if (i == R.id.redoButton) {
            findViewById(R.id.eraser_layout).setVisibility(View.GONE);
            findViewById(R.id.magicWand_layout).setVisibility(View.GONE);
            mHoverView.redo();
            updateUndoButton();
            updateRedoButton();
        } else if (i == R.id.closeButton) {
            showAlert();

        } else if (i == R.id.nextButton) {
            Intent intent = new Intent();
            intent.putExtra(BGEraserConstants.IMAGE_PATH, String.valueOf(mHoverView.save()));
            setResult(BGEraserConstants.REQUEST_CODE, intent);
            finish();
        }

    }

    private void showAlert() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.dialog_message))

                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })

                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        showAlert();
    }
}
