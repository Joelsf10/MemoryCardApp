package com.curso.memorycardapp.ui.utils

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass


fun WindowSizeClass.isTablet(): Boolean =
    widthSizeClass != WindowWidthSizeClass.Compact