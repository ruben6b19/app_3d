package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.utils

import androidx.compose.ui.input.key.*

fun handleGlobalKeyEvent2(event: KeyEvent, onNext: () -> Unit, onBack: () -> Unit): Boolean {
    if (event.type == KeyEventType.KeyDown) {
        when (event.key) {
            Key.DirectionRight, Key.Spacebar -> {
                onNext()
                return true
            }
            Key.DirectionLeft -> {
                onBack()
                return true
            }
        }
    }
    return false
}