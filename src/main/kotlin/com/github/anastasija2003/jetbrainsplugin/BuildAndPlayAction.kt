package com.github.anastasija2003.jetbrainsplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class BuildAndPlayAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // 1. Open the Game Window
        val dialog = GameDialog(project)
        dialog.show() // Show it immediately (non-blocking)

        // 2. Start the Compilation
        CompilerManager.getInstance(project).make { aborted, errors, warnings, context ->

            // 3. The build finished!
            // BUT, let's force the game to stay open for 10 seconds so we can play.
            thread {
                try {
                    // Wait 10 seconds (10000 milliseconds)
                    Thread.sleep(10000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                // 4. NOW close the window on the UI thread
                SwingUtilities.invokeLater {
                    if (dialog.isVisible) {
                        dialog.close(DialogWrapper.OK_EXIT_CODE)
                    }
                }
            }
        }
    }
}