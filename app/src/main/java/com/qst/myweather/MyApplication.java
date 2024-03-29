package com.qst.myweather;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static final String TAG = "MYAPP";
    private static Application mApplication;
    private CityDB myCityDB;
    private List<City> myCityList;
    public static List<City> fnList = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        myCityDB = openCityDB();
        initCityList();
    }

    public static Application getInstance(){
        return mApplication;
    }

    private CityDB openCityDB(){
        String fileDir = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases";

        try{
            File createDir = new File(fileDir);
            if (!createDir.exists()){
                createDir.mkdir();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        String path = fileDir + File.separator + CityDB.CITY_DB_NAME;
        File db = new File(path);

        if (!db.exists()){
            Log.i("TAG", "db is not exists!");
            try{
                InputStream is = getAssets().open("city.db");

                FileOutputStream out = new FileOutputStream(db);

                int len = -1;
                byte [] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1){
                    Log.i("TAG", buffer.toString());
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
                is.close();

            }catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }

        return new CityDB(this, path);
    }

    private void initCityList(){
        myCityList = new ArrayList<City>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private Boolean prepareCityList(){
        myCityList = myCityDB.getAllCity();
        fnList = myCityDB.getAllCity();
        for (City city:myCityList){
            String cityname = city.getCity();
            Log.d(TAG, cityname);
        }
        return true;
    }

    public List<City> getCityList() {
        return this.myCityList;
    }

}
