package com.example.plugindynamic;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class PluginHelper {
    private static final String TAG = "PluginHelper";

    private static final String CLASS_DEX_PATH_LIST = "dalvik.system.DexPathList";
    private static final String FIELD_PATH_LIST = "pathList";
    private static final String FIELD_DEX_ELEMENTS = "dexElements";

    private static Resources sPluginResources;

    public static void loadPlugin(Context context, ClassLoader hostClassLoader) throws Exception {
        loadPluginClass(context, hostClassLoader);
        initPluginResource(context);
        Toast.makeText(context, "插件加载成功", Toast.LENGTH_SHORT).show();
    }

    private static void loadPluginClass(Context context, ClassLoader hostClassLoader) throws Exception {
        File file = Environment.getExternalStorageDirectory();
        File pluginFile = new File(file, "app-debug.apk");
        Log.i(TAG, "loadPluginClass: " +pluginFile.length());
        // Step2. 创建插件的DexClassLoader
        DexClassLoader pluginClassLoader = new DexClassLoader(pluginFile.getAbsolutePath(), null, null, hostClassLoader);


        // Step3. 通过反射获取到pluginClassLoader中的pathList字段
        Object pluginDexPathList = ReflectUtil.getField(BaseDexClassLoader.class, pluginClassLoader, FIELD_PATH_LIST);
        // Step4. 通过反射获取到DexPathList的dexElements字段
        Object pluginElements = ReflectUtil.getField(Class.forName(CLASS_DEX_PATH_LIST), pluginDexPathList, FIELD_DEX_ELEMENTS);
        // Step5. 通过反射获取到宿主工程中ClassLoader的pathList字段
        Object hostDexPathList = ReflectUtil.getField(BaseDexClassLoader.class, hostClassLoader, FIELD_PATH_LIST);
        // Step6. 通过反射获取到宿主工程中DexPathList的dexElements字段
        Object hostElements = ReflectUtil.getField(Class.forName(CLASS_DEX_PATH_LIST), hostDexPathList, FIELD_DEX_ELEMENTS);




        // Step7. 将插件ClassLoader中的dexElements合并到宿主ClassLoader的dexElements
        Object array = combineArray(hostElements, pluginElements);

        // Step8. 将合并的dexElements设置到宿主ClassLoader
        ReflectUtil.setField(Class.forName(CLASS_DEX_PATH_LIST), hostDexPathList, FIELD_DEX_ELEMENTS, array);
    }

    private static Object combineArray(Object hostElements, Object pluginElements) {
        Class<?> componentType = hostElements.getClass().getComponentType();
        int i = Array.getLength(hostElements);
        int j = Array.getLength(pluginElements);
        int k = i + j;
        Object result = Array.newInstance(componentType, k);
        System.arraycopy(pluginElements, 0, result, 0, j);
        System.arraycopy(hostElements, 0, result, j, i);
        return result;
    }

    public static void initPluginResource(Context context) throws Exception {
        Class<AssetManager> clazz = AssetManager.class;
        AssetManager assetManager = clazz.newInstance();
        Method method = clazz.getMethod("addAssetPath", String.class);
        File file = Environment.getExternalStorageDirectory();
        File pluginFile = new File(file, "app-debug.apk");
        method.invoke(assetManager, pluginFile.getAbsolutePath());
        sPluginResources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
    }

    public static Resources getPluginResources() {
        return sPluginResources;
    }
}
