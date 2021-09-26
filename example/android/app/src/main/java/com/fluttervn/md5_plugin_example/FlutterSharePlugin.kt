package com.rapidbooksapp.flutter_share

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.NonNull
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.embedding.engine.plugins.FlutterPlugin


class FlutterSharePlugin(registrar: Registrar) : MethodCallHandler, FlutterPlugin {
    var context: Context? = null

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val instance: com.example.fluttershare.FlutterSharePlugin = com.example.fluttershare.FlutterSharePlugin()
            instance.onAttachedToEngine(registrar.context(), registrar.messenger())
//            val channel = MethodChannel(registrar.messenger(), "flutter_share")
//            channel.setMethodCallHandler(FlutterSharePlugin(registrar))
        }
    }

    @Override
    fun onAttachedToEngine(binding: FlutterPluginBinding) {
        onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger())
    }

    private fun onAttachedToEngine(applicationContext: Context, messenger: BinaryMessenger) {
        context = applicationContext
        methodChannel = MethodChannel(messenger, "flutter_share")
        methodChannel.setMethodCallHandler(this)
    }

    @Override
    fun onDetachedFromEngine(binding: FlutterPluginBinding?) {
        context = null
        methodChannel.setMethodCallHandler(null)
        methodChannel = null
    }

//    init {
//        context = registrar.activeContext()
//    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "isInstallWhatsApp" -> {
                result.success(isInstallWhatsApp())
            }
            "isInstallSms" -> {
                result.success(isInstallSms())
            }
            "sendSms" -> {
                val phone = call.argument<String?>("phone")
                val text = call.argument<String?>("text")
                val ret = sendSms(phone, text)
                result.success(ret)
            }
            "sendToWhatsApp" -> {
                val text = call.argument<String?>("text")
                val ret = sendToWhatsApp(text)
                result.success(ret)
            }
        }
    }

    private fun isInstallSms(): Boolean {
        if (!context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
            return false
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:")
        val activityInfo = intent.resolveActivityInfo(context!!.packageManager, intent.flags)
        return !(activityInfo == null || !activityInfo.exported)
    }

    private fun sendSms(phone: String?, text: String?): Boolean {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:$phone")
            intent.putExtra("sms_body", text)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun isInstallWhatsApp(): Boolean {
        val pm = context?.packageManager
        var installed = false
        try {
            pm?.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            installed = true
        } catch (e: Exception) {
        }
        return installed;
    }

    private fun sendToWhatsApp(text: String?): Boolean {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp") //区别别的应用包名
            context?.startActivity(intent)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}
