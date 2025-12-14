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

        handler.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
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

        SwingUtilities.invokeLater {
            if (GameDialogHolder.dialog == null || !GameDialogHolder.dialog!!.isVisible) {
                val dialog = GameDialog(project)
                GameDialogHolder.dialog = dialog
                dialog.show()

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

object GameDialogHolder {
    var dialog: GameDialog? = null
}
