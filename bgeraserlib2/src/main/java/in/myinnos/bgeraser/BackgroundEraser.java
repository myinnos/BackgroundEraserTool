package in.myinnos.bgeraser;

import android.app.Activity;
import android.content.Intent;

public class BackgroundEraser {

    public static void start(Activity activity, String path, Boolean closeButton) {
        Intent intent = new Intent(activity, EraserActivity.class);
        intent.putExtra(BGEraserConstants.IMAGE_PATH, path);
        intent.putExtra("closeButton", closeButton);
        activity.startActivityForResult(intent, BGEraserConstants.REQUEST_CODE);
    }
}
