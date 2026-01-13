#define INITGUID
#include <windows.h>
#include <shlobj.h>   // SHGetKnownFolderPath
#include <stdlib.h>   // malloc, free
#include <initguid.h>
#include "../main.h"

/* 手动定义 FOLDERID_Downloads（MinGW 兼容） */
DEFINE_GUID(FOLDERID_Downloads,
    0x374DE290, 0x123F, 0x4565,
    0x91, 0x64, 0x39, 0xC4, 0x92, 0x5E, 0x46, 0x7B
);

/**
 * 获取 Windows Downloads 文件夹路径（UTF-8）
 * @return malloc 分配的字符串，失败返回 NULL
 *         调用方必须 free()
 */
char* get_windows_downloads_path(void) {
    PWSTR wpath = NULL;

    HRESULT hr = SHGetKnownFolderPath(
        &FOLDERID_Downloads,
        0,
        NULL,
        &wpath
    );

    if (FAILED(hr) || wpath == NULL) {
        return NULL;
    }

    /* 计算 UTF-8 所需长度 */
    int len = WideCharToMultiByte(
        CP_UTF8,
        0,
        wpath,
        -1,
        NULL,
        0,
        NULL,
        NULL
    );

    if (len <= 0) {
        CoTaskMemFree(wpath);
        return NULL;
    }

    char* path = (char*)malloc(len);
    if (!path) {
        CoTaskMemFree(wpath);
        return NULL;
    }

    /* UTF-16 -> UTF-8 */
    WideCharToMultiByte(
        CP_UTF8,
        0,
        wpath,
        -1,
        path,
        len,
        NULL,
        NULL
    );

    CoTaskMemFree(wpath);
    return path;
}

