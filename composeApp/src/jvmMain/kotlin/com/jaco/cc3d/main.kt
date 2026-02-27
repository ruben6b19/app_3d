package com.jaco.cc3d


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jaco.cc3d.di.AppComponent
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.manageSecondaryDisplays
import com.jaco.cc3d.presentation.app.App


@OptIn(ExperimentalMaterial3Api::class)
fun main() = application {




    Window(
        onCloseRequest = {
            manageSecondaryDisplays(false, { _,_ -> "" }) // Asegúrate de cerrar al salir
            exitApplication()
        },
        title = "CC3D",
        state = rememberWindowState(placement = WindowPlacement.Maximized )
    ) {
        App()
    }

}

