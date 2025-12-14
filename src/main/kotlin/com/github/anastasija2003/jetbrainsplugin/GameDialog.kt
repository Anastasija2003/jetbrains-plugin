package com.github.anastasija2003.jetbrainsplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

class GameDialog(project: Project?) : DialogWrapper(project) {

    // 1. WE MOVE IT HERE (Class Level)
    val gamePanel = GamePanel()

    init {
        init()
        title = "Compiling... Survive until the build finishes!"
        isModal = false
    }

    override fun createCenterPanel(): JComponent {
        // 2. We just return the variable we created above
        gamePanel.requestFocusInWindow()
        return gamePanel
    }

    // 3. This is needed for the keyboard controls to work
    override fun getPreferredFocusedComponent(): JComponent {
        return gamePanel
    }
}