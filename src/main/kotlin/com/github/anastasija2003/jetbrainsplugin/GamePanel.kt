package com.github.anastasija2003.jetbrainsplugin

import com.intellij.ui.Gray
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
import java.awt.Graphics2D
import java.awt.BasicStroke

data class Enemy(var x: Int, var y: Int, val size: Int = 40)

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
        drawDeveloper(g, playerX, playerY, 40)

        // 3. Draw Enemies
        g.color = JBColor.RED
        for (enemy in enemies) {
            drawBug(g, enemy.x, enemy.y, enemy.size)
        }

        // 4. Draw Score
        g.color = JBColor.WHITE
        g.drawString("Score: $score", 10, 20)

        // 5. Draw Footer (The Message Area)
        g.color = JBColor.BLACK
        g.fillRect(0, gameHeight, width, footerHeight)

        g.color = JBColor.CYAN
        g.font = Font("Arial", Font.ITALIC, 14)

        val words = motivationalMessage.split(" ")
        var line = ""
        var y = gameHeight + 25

        for (word in words) {
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

            if (enemy.y > gameHeight) {
                enemy.y = 0
                enemy.x = (0..350).random()
                score++
            }
        }
        repaint()
    }
    private fun drawBug(g: Graphics, x: Int, y: Int, size: Int) {
        val g2d = g as Graphics2D
        g2d.color = JBColor(Color.BLACK, Color.BLACK)

        val scale = size / 40f

        // Body
        g2d.fillOval(
            x + (8 * scale).toInt(),
            y + (14 * scale).toInt(),
            (20 * scale).toInt(),
            (12 * scale).toInt()
        )

        // Head
        g2d.fillOval(
            x + (12 * scale).toInt(),
            y + (8 * scale).toInt(),
            (10 * scale).toInt(),
            (8 * scale).toInt()
        )

        // Wings
        g2d.fillOval(
            x + (6 * scale).toInt(),
            y + (12 * scale).toInt(),
            (12 * scale).toInt(),
            (6 * scale).toInt()
        )
        g2d.fillOval(
            x + (18 * scale).toInt(),
            y + (12 * scale).toInt(),
            (12 * scale).toInt(),
            (6 * scale).toInt()
        )

        // Legs
        g2d.stroke = BasicStroke(1.5f)
        g2d.drawLine(
            x + (10 * scale).toInt(), y + (24 * scale).toInt(),
            x + (6 * scale).toInt(), y + (28 * scale).toInt()
        )
        g2d.drawLine(
            x + (16 * scale).toInt(), y + (24 * scale).toInt(),
            x + (16 * scale).toInt(), y + (28 * scale).toInt()
        )
        g2d.drawLine(
            x + (22 * scale).toInt(), y + (24 * scale).toInt(),
            x + (26 * scale).toInt(), y + (28 * scale).toInt()
        )
    }
    private fun drawDeveloper(g: Graphics, x: Int, y: Int, size: Int = 40) {
        val g2d = g as Graphics2D

        val scale = size / 40f

        // Colors
        val skin = JBColor(Color(240, 220, 190), Color(240,220,190))
        val shirt = JBColor(Color(60, 140, 255),Color(60,140,255)) // blue shirt
        val pants = JBColor(Color(40, 40, 60),Color(40,40,60))         // dark pants
        val shoes = JBColor(Gray._20, Gray._20)        // almost black
        val hair = JBColor(Color(40, 25, 15),Color(40,25,15) )        // dark hair

        // HEAD
        g2d.color = skin
        g2d.fillOval(
            x + (10 * scale).toInt(),
            y + (2 * scale).toInt(),
            (20 * scale).toInt(),
            (20 * scale).toInt()
        )

        // HAIR (top strip)
        g2d.color = hair
        g2d.fillRect(
            x + (10 * scale).toInt(),
            y + (2 * scale).toInt(),
            (20 * scale).toInt(),
            (7 * scale).toInt()
        )

        // BODY (torso)
        g2d.color = shirt
        g2d.fillRect(
            x + (8 * scale).toInt(),
            y + (22 * scale).toInt(),
            (24 * scale).toInt(),
            (12 * scale).toInt()
        )

        // ARMS
        g2d.color = skin
        // left arm
        g2d.fillRect(
            x + (2 * scale).toInt(),
            y + (24 * scale).toInt(),
            (6 * scale).toInt(),
            (10 * scale).toInt()
        )
        // right arm
        g2d.fillRect(
            x + (32 * scale).toInt(),
            y + (24 * scale).toInt(),
            (6 * scale).toInt(),
            (10 * scale).toInt()
        )

        // LEGS
        g2d.color = pants
        // left leg
        g2d.fillRect(
            x + (10 * scale).toInt(),
            y + (34 * scale).toInt(),
            (7 * scale).toInt(),
            (10 * scale).toInt()
        )
        // right leg
        g2d.fillRect(
            x + (23 * scale).toInt(),
            y + (34 * scale).toInt(),
            (7 * scale).toInt(),
            (10 * scale).toInt()
        )

        // SHOES
        g2d.color = shoes
        g2d.fillRect(
            x + (9 * scale).toInt(),
            y + (42 * scale).toInt(),
            (9 * scale).toInt(),
            (4 * scale).toInt()
        )
        g2d.fillRect(
            x + (22 * scale).toInt(),
            y + (42 * scale).toInt(),
            (9 * scale).toInt(),
            (4 * scale).toInt()
        )

        // SIMPLE EYES & MOUTH (optional)
        g2d.color = Color.BLACK
        val eyeY = y + (10 * scale).toInt()
        g2d.fillOval(x + (14 * scale).toInt(), eyeY, (3 * scale).toInt(), (3 * scale).toInt())
        g2d.fillOval(x + (23 * scale).toInt(), eyeY, (3 * scale).toInt(), (3 * scale).toInt())

        g2d.drawLine(
            x + (16 * scale).toInt(),
            y + (17 * scale).toInt(),
            x + (24 * scale).toInt(),
            y + (17 * scale).toInt()
        )
    }
}