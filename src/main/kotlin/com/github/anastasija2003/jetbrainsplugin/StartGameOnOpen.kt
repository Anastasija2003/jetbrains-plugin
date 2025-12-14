package com.github.anastasija2003.jetbrainsplugin

import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class StartGameOnOpen : ProjectActivity {

    override suspend fun execute(project: Project) {
        val dialog = GameDialog(project)
        dialog.show()

        CompilerManager.getInstance(project).make(null)

        Thread {
            while (dialog.isVisible) {
                val message = BuildAndPlayAction().getMotivationalQuote()
                javax.swing.SwingUtilities.invokeLater {
                    if (dialog.isVisible) {
                        dialog.gamePanel.updateFooterText(message)
                    }
                }
                try {
                    Thread.sleep(3000)
                } catch (_: InterruptedException) {
                    break
                }
            }
        }.start()
    }
}
