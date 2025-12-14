package com.github.anastasija2003.jetbrainsplugin

import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class StartGameOnOpen : ProjectActivity {

    override suspend fun execute(project: Project) {
        // 1. Open the game window
        val dialog = GameDialog(project)
        dialog.show()

        // 2. Start the build (optional – remove if you only want the game)
        CompilerManager.getInstance(project).make(null)

        // 3. Start the AI quote loop (same logic you have in BuildAndPlayAction)
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
