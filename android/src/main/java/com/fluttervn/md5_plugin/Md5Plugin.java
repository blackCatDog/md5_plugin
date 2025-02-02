package com.fluttervn.md5_plugin;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;


/** Md5Plugin */
public class Md5Plugin implements MethodCallHandler,FlutterPlugin {
  /** Plugin registration. *//*
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "md5_plugin");
    channel.setMethodCallHandler(new Md5Plugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }*/

  private final int BUFFER_SIZE = 1024 * 8;

//  companion object {
//    @JvmStatic
//    fun registerWith(registrar: Registrar) {
//      val instance: com.example.fluttershare.FlutterSharePlugin = com.example.fluttershare.FlutterSharePlugin()
//      instance.onAttachedToEngine(registrar.context(), registrar.messenger())
////            val channel = MethodChannel(registrar.messenger(), "flutter_share")
////            channel.setMethodCallHandler(FlutterSharePlugin(registrar))
//    }
//  }

  @Override
  public void  onAttachedToEngine(FlutterPluginBinding binding) {
    onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    context = applicationContext;
    methodChannel = new MethodChannel(messenger, "md5_plugin");
    methodChannel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {
    context = null;
    methodChannel.setMethodCallHandler(null);
    methodChannel = null;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    com.example.fluttershare.FlutterSharePlugin instance = new com.example.fluttershare.FlutterSharePlugin();
    instance.onAttachedToEngine(registrar.context(), registrar.messenger());
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "md5_plugin");
    channel.setMethodCallHandler(new Md5Plugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if("getMD5".equals(call.method)){
      String path= call.argument("file_path");
      Log.d("MD5Plugin","getMD5 @filepath="+path);
      result.success(getFileChecksum(path));
    } else {
      result.notImplemented();
    }
  }

  private String getFileChecksum(String filePath) {
    File file=new File(filePath);
    if (file.exists()) {
      try {
        InputStream is = new FileInputStream(file);
        return getStreamChecksum(is);
      } catch (FileNotFoundException e) {
      }
    }
    return null;
  }

  private String getStreamChecksum(InputStream is) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] buffer = new byte[BUFFER_SIZE];
      int numRead;
      while ((numRead = is.read(buffer)) != -1) {
        md.update(buffer, 0, numRead);
      }
      byte[] digest = md.digest();
      String checksum = new String(Base64.encode(digest, Base64.DEFAULT));
      String result= checksum.trim();
      Log.d("MD5Plugin","getStreamChecksum @result="+result);
      return result;
    } catch (NoSuchAlgorithmException e) {
    } catch (IOException e) {
    } finally {
      try {
        is.close();
      } catch (IOException e) {
      }
    }

    return null;
  }
}
