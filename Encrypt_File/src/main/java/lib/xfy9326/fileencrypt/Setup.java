package lib.xfy9326.fileencrypt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;

public class Setup {
    private Core core;
    private Context context;
    private boolean dataSet = false;

    public Setup(Context context) {
        this.context = context;
    }

    public void setData(String path, String[] type, String pw, boolean enCode) {
        dataSet = true;
        core = new Core(path, type, pw, enCode);
    }

    public boolean start() {
        if (dataSet && PermissionCheck(context)) {
            core.start();
            return true;
        } else {
            return false;
        }
    }

    public void stop() {
        core.stop();
    }

    private boolean PermissionCheck(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) + context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void setOnProcessChangedListener(OnProcessChangedListener listener) {
        core.setListener(listener);
    }

    public interface OnProcessChangedListener {
        void onChanged(ArrayList<File> file_list, int work_mount);
    }
}
