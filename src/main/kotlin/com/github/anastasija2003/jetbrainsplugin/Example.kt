package com.github.anastasija2003.jetbrainsplugin
// IMPORT CHECK: Ensure these are the exact imports
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

// SYNTAX CHECK: Ensure you have "()" after AnAction
class Example : AnAction() {

    // TYPE CHECK: The parameter must be 'AnActionEvent', NOT 'ActionEvent'
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