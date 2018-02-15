# Encrypt_File
Android Library which use AES to encrypt files

使用AES算法加密文件或者文件夹下所有文件的安卓arr包

使用方法：
```
        //初始化对象
        Setup setup = new Setup(context);
        //设置参数 文件或文件夹路径 文件类型 加密密码 加密或解密
        setup.setData(path, file_type, "6666", true);
        setup.setOnProcessChangedListener(new Setup.OnProcessChangedListener() {

            @Override
            //file_list 所有加密的文件列表  work_amount 已经加密的文件数量
            public void onChanged(ArrayList<File> file_list, int work_mount) {
              
            }

        });
        //开始加密 返回布尔值判断是否成功开始
        setup.start();
        //停止加密
        setup.stop();
```
