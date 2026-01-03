package com.xah.send.ui.componment

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.xah.send.logic.util.resolveFileConflict
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.io.copyTo
import kotlin.io.outputStream
import kotlin.io.use
import kotlin.let


fun getFileName(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            return it.getString(0)
        }
    }
    return "unknown_${System.currentTimeMillis()}"
}

// 复制到缓存区
private fun copyUriToCacheFile(
    context: Context,
    uri: Uri
): File? {
    val inputStream =
        context.contentResolver.openInputStream(uri) ?: return null

    val originalName = getFileName(context, uri)
    val outFile = resolveFileConflict(context.cacheDir, originalName)

    outFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }

    return outFile
}


// 文件选择器 onResult代表选择文件后的操作，记得跟随open = false关闭文件选择器
@Composable
fun LaunchFilePicker(open : Boolean,onResult: (File?) -> Unit) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                val newPath = copyUriToCacheFile(context,it)
                onResult(newPath)
            } ?: onResult(null)
        }
    )

    // 启动文件选择器
    LaunchedEffect(open) {
        if(open) {
            filePickerLauncher.launch(arrayOf("*/*"))
        }
    }
}

suspend fun cleanCopiedCache(context: Context) = withContext(Dispatchers.IO) {
    context.cacheDir.listFiles()?.forEach {
        it.deleteRecursively()
    }
}
