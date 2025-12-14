package com.github.anastasija2003.jetbrainsplugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.SwingUtilities
import kotlin.concurrent.thread
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@Service(Service.Level.PROJECT)
class GameService(private val project: Project) {

    private var activeDialog: GameDialog? = null
    // ⚠️ PUT KEY HERE
    private val apiKey = "sk-proj-YOUR_KEY_HERE"

    fun startGame() {
        // If a game is already running, don't open another one
        if (activeDialog != null && activeDialog!!.isVisible) return

        SwingUtilities.invokeLater {
            activeDialog = GameDialog(project)
            activeDialog?.show()

            // Fetch AI motivation immediately in background
            thread {
                val message = getMotivationalQuote()
                SwingUtilities.invokeLater {
                    activeDialog?.gamePanel?.updateFooterText(message)
                }
            }
        }
    }

    fun stopGame() {
        if (activeDialog == null) return

        // Keep game open for at least a few seconds if build was superfast
        thread {
            try {
                // Optional: Ensure they see the game for at least 3 seconds
                // You can remove this sleep if you want it to close INSTANTLY when build is done
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            SwingUtilities.invokeLater {
                activeDialog?.close(DialogWrapper.OK_EXIT_CODE)
                activeDialog = null
            }
        }
    }

    // Reuse your AI logic here
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
            return "Keep coding! (Offline)"
        }
    }
}