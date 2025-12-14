package com.github.anastasija2003.jetbrainsplugin

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.HierarchyEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.Timer
import com.intellij.ui.JBColor

data class Enemy(var x: Int, var y: Int)

class GamePanel : JPanel(), ActionListener {
    private val timer = Timer(20, this)
    private var playerX = 175
    private val playerY = 350

    // Game settings
    private val gameHeight = 400
    private val footerHeight = 80

    private val enemies = mutableListOf<Enemy>()
    private var score = 0
    private var isGameOver = false

    // Default text while loading
    private var motivationalMessage: String = "Asking AI for motivation..."

    init {
        // Increased height to 480 to fit the text bar at bottom
        preferredSize = Dimension(400, gameHeight + footerHeight)
        background = JBColor.DARK_GRAY
        isFocusable = true
        focusTraversalKeysEnabled = false

        spawnEnemies()

        addHierarchyListener { e ->
            if ((e.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong()) != 0L && isShowing) {
                SwingUtilities.invokeLater { requestFocusInWindow() }
            }
        }

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (!isGameOver) {
                    if (e.keyCode == KeyEvent.VK_LEFT && playerX > 0) playerX -= 20
                    if (e.keyCode == KeyEvent.VK_RIGHT && playerX < 360) playerX += 20
                } else if (e.keyCode == KeyEvent.VK_SPACE) {
                    resetGame()
                }
                repaint()
            }
        })

        timer.start()
    }

    private fun spawnEnemies() {
        enemies.clear()
        enemies.add(Enemy((0..350).random(), -50))
        enemies.add(Enemy((0..350).random(), -250))
    }

    // This just updates the text, it DOES NOT stop the game anymore
    fun updateFooterText(text: String) {
        this.motivationalMessage = text
        repaint()
    }

    private fun resetGame() {
        playerX = 175
        score = 0
        isGameOver = false
        spawnEnemies()
        timer.start()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // 1. Draw Game Background
        g.color = JBColor.DARK_GRAY
        g.fillRect(0, 0, width, gameHeight)

        // 2. Draw Player
        g.color = JBColor.GREEN
        g.fillRect(playerX, playerY, 40, 40)

        // 3. Draw Enemies
        g.color = JBColor.RED
        for (enemy in enemies) {
            g.fillRect(enemy.x, enemy.y, 40, 40)
        }

        // 4. Draw Score
        g.color = JBColor.WHITE
        g.drawString("Score: $score", 10, 20)

        // 5. Draw Footer (The Message Area)
        g.color = JBColor.BLACK
        g.fillRect(0, gameHeight, width, footerHeight)

        g.color = JBColor.CYAN
        g.font = Font("Arial", Font.ITALIC, 14)

        // Text Wrapping Logic for the footer
        val words = motivationalMessage.split(" ")
        var line = ""
        var y = gameHeight + 25 // Start drawing inside the footer

        for (word in words) {
            // Check if adding the word exceeds width
            if (g.fontMetrics.stringWidth(line + word) < width - 20) {
                line += "$word "
            } else {
                g.drawString(line, 10, y)
                y += 20 // Move to next line
                line = "$word "
            }
        }
        g.drawString(line, 10, y)

        // 6. Draw Game Over Overlay (if dead)
        if (isGameOver) {
            g.color = JBColor(Color(0, 0, 0, 150), Color(0, 0, 0, 150))
            g.fillRect(0, 0, width, gameHeight) // Only cover game area
            g.color = JBColor.YELLOW
            g.font = Font("Arial", Font.BOLD, 20)
            g.drawString("GAME OVER", 140, 180)
            g.drawString("Press SPACE to restart", 90, 210)
        }
    }

    override fun actionPerformed(e: ActionEvent?) {
        if (isGameOver) return

        val playerRect = java.awt.Rectangle(playerX, playerY, 40, 40)

        for (enemy in enemies) {
            val currentSpeed = 7 + (score / 5)
            enemy.y += currentSpeed

            val enemyRect = java.awt.Rectangle(enemy.x, enemy.y, 40, 40)
            if (playerRect.intersects(enemyRect)) {
                isGameOver = true
                timer.stop()
            }

            // Reset enemy if it passes the game height
            if (enemy.y > gameHeight) {
                enemy.y = 0
                enemy.x = (0..350).random()
                score++
            }
        }
        repaint()
    }
}