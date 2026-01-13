#include "jni.h"
#include "../main.h"
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <windows.h>
#include "jni_native.h"

JNIEXPORT jstring JNICALL Java_com_xah_send_logic_jni_WindowsJni_pickSingleFile
  (JNIEnv * env, jobject obj) {
    // 调用C函数 pick_file
    const char* filePath = pick_file();

    // 如果没有文件被选择，返回null
    if (filePath == NULL) {
        return NULL;
    }

    // 将C的const char* 转换为jstring并返回
    return (*env)->NewStringUTF(env, filePath);
}

JNIEXPORT jboolean JNICALL Java_com_xah_send_logic_jni_WindowsJni_warn
  (JNIEnv *env, jobject obj, jstring windowName) {

    const jchar* windowNameChars = (*env)->GetStringChars(env, windowName, NULL);
    jsize length = (*env)->GetStringLength(env, windowName);
    wchar_t* windowNameW = (wchar_t*)malloc((length + 1) * sizeof(wchar_t)); // 为宽字符数组分配空间

    wcsncpy(windowNameW, (const wchar_t*)windowNameChars, length);
    windowNameW[length] = L'\0';  // 确保以 NULL 结尾

    bool result = warn(windowNameW);

    (*env)->ReleaseStringChars(env, windowName, windowNameChars);
    free(windowNameW);

    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_com_xah_send_logic_jni_WindowsJni_getDownloadFolder
        (JNIEnv * env, jobject obj) {
    char* path = get_windows_downloads_path();
    if (!path) return NULL;

    jstring ret = (*env)->NewStringUTF(env, path);
    free(path);
    return ret;
}