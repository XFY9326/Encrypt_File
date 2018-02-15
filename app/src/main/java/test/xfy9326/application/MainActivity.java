package test.xfy9326.application;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import lib.xfy9326.fileencrypt.Setup;

public class MainActivity extends Activity {
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Test/";
    private static final String[] file_type = {"doc", "docx", "ppt", "pptx", "xls", "xlsx", "jpg", "jpeg", "bmp", "png", "db", "sqlite", "json", "txt"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewSet();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    private void viewSet() {
        final Switch switcher = findViewById(R.id.switcher);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick(MainActivity.this, switcher);
            }
        });
    }

    private void buttonClick(Activity activity, Switch switcher) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_process, (ViewGroup) activity.findViewById(R.id.dialog_layout));

        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(100);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);

        final Setup setup = new Setup(activity);
        setup.setData(path, file_type, "6666", switcher.isChecked());
        setup.setOnProcessChangedListener(new Setup.OnProcessChangedListener() {

            @Override
            public void onChanged(ArrayList<File> file_list, int work_mount) {
                int total_mount = file_list.size();
                System.out.println(total_mount + "|" + work_mount);

                float count = (float) work_mount / (float) total_mount;
                progressBar.setProgress(Math.round(count * (float) 100));
            }

        });

        builder.setPositiveButton(android.R.string.yes, null);
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setup.stop();
            }
        });
        builder.setTitle("Encrypt File");

        builder.setView(view);
        builder.show();

        if (setup.start()) {
            Toast.makeText(activity, "Work!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Not Work!", Toast.LENGTH_SHORT).show();
        }
    }
}
