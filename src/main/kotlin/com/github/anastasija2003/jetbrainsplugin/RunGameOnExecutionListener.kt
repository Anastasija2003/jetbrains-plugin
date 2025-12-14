package com.github.anastasija2003.jetbrainsplugin

import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class RunGameOnExecutionListener : ExecutionListener {

    override fun processStarted(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler
    ) {
        val project: Project = env.project

        // 1) Attach a listener to the run process so we know when it ends
        handler.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
                // Close dialog on EDT when run finishes
                SwingUtilities.invokeLater {
                    GameDialogHolder.dialog?.let { dlg ->
                        if (dlg.isVisible) {
                            dlg.close(com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE)
                        }
                    }
                    GameDialogHolder.dialog = null
                }
            }
        })

        // 2) Open game dialog on EDT and start AI loop
        SwingUtilities.invokeLater {
            // Reuse a single dialog instance per run
            if (GameDialogHolder.dialog == null || !GameDialogHolder.dialog!!.isVisible) {
                val dialog = GameDialog(project)
                GameDialogHolder.dialog = dialog
                dialog.show()

                // AI loop in background
                thread(name = "MotivationLoop") {
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
}

// Shared holder so processStarted/processTerminated talk about the same dialog
object GameDialogHolder {
    var dialog: GameDialog? = null
}
