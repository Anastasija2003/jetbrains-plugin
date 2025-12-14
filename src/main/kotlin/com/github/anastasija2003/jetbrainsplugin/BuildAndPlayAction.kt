package com.github.anastasija2003.jetbrainsplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.ui.DialogWrapper
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class BuildAndPlayAction : AnAction() {
    val apiKey = System.getenv("API_KEY")
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val dialog = GameDialog(project)
        dialog.show()

        // TASK 1: Fetch Motivation Immediately in Background
        thread {
            val message = getMotivationalQuote()
            SwingUtilities.invokeLater {
                if (dialog.isVisible) {
                    dialog.gamePanel.updateFooterText(message)
                }
            }
        }

        // TASK 2: Start Build & Timer
        CompilerManager.getInstance(project).make { aborted, errors, warnings, context ->
            thread {
                try {
                    Thread.sleep(10000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                SwingUtilities.invokeLater {
                    if (dialog.isVisible) {
                        dialog.close(DialogWrapper.OK_EXIT_CODE)
                    }
                }
            }
        }
    }

    private fun getMotivationalQuote(): String {
        try {
            val url = URL("https://api.openai.com/v1/chat/completions")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonBody = """
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [
                        {"role": "system", "content": "You are a helpful assistant."},
                        {"role": "user", "content": "Give me a short, 1-sentence funny motivational quote for a programmer."}
                    ],
                    "max_tokens": 50,
                    "temperature": 0.8
                }
            """.trimIndent()

            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonBody)
            writer.flush()
            writer.close()

            val response = connection.inputStream.bufferedReader().use { it.readText() }

            val searchKey = "\"content\": \""
            val startIndex = response.indexOf(searchKey)

            if (startIndex == -1) return "Build successful! (API Error)"

            val sb = StringBuilder()
            var i = startIndex + searchKey.length
            var isEscaped = false

            while (i < response.length) {
                val c = response[i]
                if (isEscaped) {
                    if (c == 'n') sb.append(' ')
                    else sb.append(c)
                    isEscaped = false
                } else {
                    if (c == '\\') isEscaped = true
                    else if (c == '"') break
                    else sb.append(c)
                }
                i++
            }
            return sb.toString()

        } catch (e: Exception) {
            return "Keep coding, you're doing great! (Offline)"
        }
    }
}