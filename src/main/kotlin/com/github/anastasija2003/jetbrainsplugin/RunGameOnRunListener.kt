package com.github.anastasija2003.jetbrainsplugin

import com.intellij.execution.RunManagerListener
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.project.Project
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class RunGameOnRunListener : RunManagerListener {

    override fun runConfigurationSelected(settings: RunnerAndConfigurationSettings?) {
        // Called when you press Run or change config; we only care when a config is actually selected to run.
        // You may want a stricter hook (e.g. beforeRunTasks), but this is simple and works for hackathon.

        val project: Project = settings?.configuration?.project ?: return

        // Open dialog on EDT
        SwingUtilities.invokeLater {
            val dialog = GameDialog(project)
            dialog.show()

            // Start build in background (optional)
            CompilerManager.getInstance(project).make(null)

            // Start AI quotes loop
            thread {
                val helper = BuildAndPlayAction()
                while (dialog.isVisible) {
                    val msg = helper.getMotivationalQuote()
                    SwingUtilities.invokeLater {
                        if (dialog.isVisible) {
                            dialog.gamePanel.updateFooterText(msg)
                        }
                    }
                    try {
                        Thread.sleep(3000)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            }
        }
    }
}
