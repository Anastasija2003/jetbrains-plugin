package com.github.anastasija2003.jetbrainsplugin

import com.intellij.openapi.compiler.CompilationStatusListener
import com.intellij.openapi.compiler.CompileContext
import com.intellij.openapi.compiler.CompileTask
import com.intellij.openapi.components.service

// 1. Detects START of compilation
class GameStartTask : CompileTask {
    override fun execute(context: CompileContext): Boolean {
        val project = context.project
        project.service<GameService>().startGame()
        return true
    }
}

// 2. Detects END of compilation
// FIX: Removed '(val project: Project)' from constructor.
// We get the project from 'compileContext' inside the function instead.
class GameEndListener : CompilationStatusListener {
    override fun compilationFinished(
        aborted: Boolean,
        errors: Int,
        warnings: Int,
        compileContext: CompileContext
    ) {
        // Correct way to get the project
        val project = compileContext.project
        project.service<GameService>().stopGame()
    }
}