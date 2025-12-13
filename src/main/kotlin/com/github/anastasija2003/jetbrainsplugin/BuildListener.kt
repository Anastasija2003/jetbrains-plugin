package com.github.anastasija2003.jetbrainsplugin


import com.intellij.openapi.compiler.CompilationStatusListener
import com.intellij.openapi.compiler.CompileContext
import com.intellij.openapi.compiler.CompilerTopics
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.util.messages.MessageBusConnection
import com.intellij.openapi.ui.DialogWrapper


class BuildGameListener : ProjectActivity {

    // Hold a reference so we can close it later
    private var activeDialog: GameDialog? = null

    override suspend fun execute(project: Project) {
        val connection: MessageBusConnection = project.messageBus.connect()

        connection.subscribe(CompilerTopics.COMPILATION_STATUS, object : CompilationStatusListener {

            // 1. Build Started -> Show Game
            override fun compilationFinished(
                aborted: Boolean,
                errors: Int,
                warnings: Int,
                compileContext: CompileContext
            ) {
                // Close game when build ends
                activeDialog?.close(DialogWrapper.OK_EXIT_CODE)
                activeDialog = null
            }

            // Note: IntelliJ SDK changed recently.
            // We interpret "automake" or plain build events here.
            // For simplicity in this demo, we assume the dialog opens on a custom action or
            // we can try to hook into 'compilationStarted' if available in your version.
        })
    }

    // Helper to open game (Call this from Step 4)
    fun openGame(project: Project) {
        if (activeDialog == null) {
            val dialog = GameDialog(project)
            activeDialog = dialog
            dialog.show()
        }
    }
}