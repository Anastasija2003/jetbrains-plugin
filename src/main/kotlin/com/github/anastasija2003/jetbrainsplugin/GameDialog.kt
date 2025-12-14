package com.github.anastasija2003.jetbrainsplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

class GameDialog(project: Project?) : DialogWrapper(project) {

    val gamePanel = GamePanel()

    init {
        init()
        title = "Compiling... Survive until the build finishes!"
        isModal = false
    }

    override fun createCenterPanel(): JComponent {
        gamePanel.requestFocusInWindow()
        return gamePanel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return gamePanel
    }
}