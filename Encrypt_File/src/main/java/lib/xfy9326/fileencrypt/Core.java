package lib.xfy9326.fileencrypt;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

class Core {
    private String pw;
    private String path;
    private String[] type;
    private boolean enCode;
    private int finish_file_mount;
    private ExecutorService executor;
    private ArrayList<File> selected_file;
    private Setup.OnProcessChangedListener listener;

    Core(String file_path, String[] file_type, String password, boolean isEncrypt) {
        this.pw = password;
        this.path = file_path;
        this.type = file_type;
        this.enCode = isEncrypt;
        selected_file = new ArrayList<>();
        executor = Executors.newFixedThreadPool(getCpuNum() - 1);
    }

    void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                selected_file.clear();
                finish_file_mount = 0;
                searchFile(new File(path), type);
                if (selected_file.size() > 0) {
                    if (listener != null) {
                        listener.onChanged(selected_file, 0);
                    }
                    if (enCode) {
                        enFile();
                    } else {
                        deFile();
                    }
                }
            }
        }).start();
    }

    void stop() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    void setListener(Setup.OnProcessChangedListener listener) {
        this.listener = listener;
    }

    private void searchFile(File file, String[] file_type) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        searchFile(f, file_type);
                    }
                }
            } else if (file.canRead() && file.canWrite()) {
                for (String type : file_type) {
                    if (file.getName().contains(type)) {
                        selected_file.add(file);
                        break;
                    }
                }

            }
        }
    }

    private void enFile() {
        for (File f : selected_file) {
            final File file = f;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] data = IO.read(file);
                    data = IO.encrypt(data, pw);
                    IO.write(data, file);
                    eachFileFinish();
                }
            });
        }
    }

    private void deFile() {
        for (File f : selected_file) {
            final File file = f;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] data = IO.read(file);
                    data = IO.decrypt(data, pw);
                    IO.write(data, file);
                    eachFileFinish();
                }
            });
        }
    }

    synchronized private void eachFileFinish() {
        if (listener != null) {
            finish_file_mount++;
            listener.onChanged(selected_file, finish_file_mount);
        }
    }

    private int getCpuNum() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        }

        File dir = new File("/sys/devices/system/cpu/");
        if (dir.exists()) {
            File[] files = dir.listFiles(new CpuFilter());
            if (files != null) {
                return files.length;
            }
        }
        return 2;

    }

}
