//
// Created by Chiu-xaH on 2025/3/21.
//

#ifndef BSDIFF_WIN_MASTER_MAIN_H
#define BSDIFF_WIN_MASTER_MAIN_H

#include <stdbool.h>

__declspec(dllexport) const char* pick_file();
bool warn(const wchar_t *windowName);
char* get_windows_downloads_path(void);

#endif //BSDIFF_WIN_MASTER_MAIN_H
