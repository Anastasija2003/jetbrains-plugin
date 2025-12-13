package com.github.anastasija2003.jetbrainsplugin


import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

class GameDialog(project: Project?) : DialogWrapper(project) {
    init {
        init()
        title = "Compiling... Survive until the build finishes!"
        isModal = false // Important: Allows the build to continue in background
    }

    override fun createCenterPanel(): JComponent {
        val game = GamePanel()
        game.requestFocusInWindow() // Ensure keyboard works immediately
        return game
    }
}