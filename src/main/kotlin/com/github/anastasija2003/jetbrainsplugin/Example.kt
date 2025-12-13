package com.github.anastasija2003.jetbrainsplugin
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class Example : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        Messages.showMessageDialog(
            project,
            "Plugin is working!",
            "Hello",
            Messages.getInformationIcon()
        )
    }
}