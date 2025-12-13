package com.github.anastasija2003.jetbrainsplugin // <-- Match your package

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.HierarchyEvent // <--- IMPORT THIS
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JPanel
import javax.swing.SwingUtilities // <--- IMPORT THIS
import javax.swing.Timer

class GamePanel : JPanel(), ActionListener {
    private val timer = Timer(20, this)
    private var playerX = 175
    private val playerY = 350
    private var enemyX = 100
    private var enemyY = 0
    private var score = 0
    private var isGameOver = false

    init {
        preferredSize = Dimension(400, 400)
        background = Color.DARK_GRAY
        isFocusable = true
        focusTraversalKeysEnabled = false

        // --- THE MAGIC FIX START ---
        // This waits for the window to actually "Show" (pop up),
        // and then forcibly grabs the keyboard focus back from the buttons.
        addHierarchyListener { e ->
            if ((e.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong()) != 0L && isShowing) {
                SwingUtilities.invokeLater {
                    requestFocusInWindow()
                }
            }
        }
        // --- THE MAGIC FIX END ---

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (!isGameOver) {
                    if (e.keyCode == KeyEvent.VK_LEFT && playerX > 0) playerX -= 15
                    if (e.keyCode == KeyEvent.VK_RIGHT && playerX < 360) playerX += 15
                } else if (e.keyCode == KeyEvent.VK_SPACE) {
                    resetGame()
                }
                repaint()
            }
        })

        timer.start()
    }

    private fun resetGame() {
        playerX = 175
        enemyY = 0
        score = 0
        isGameOver = false
        timer.start()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.color = Color.GREEN
        g.fillRect(playerX, playerY, 40, 40)

        g.color = Color.RED
        g.fillRect(enemyX, enemyY, 40, 40)

        g.color = Color.WHITE
        g.drawString("Score: $score", 10, 20)

        if (isGameOver) {
            g.color = Color.YELLOW
            g.drawString("GAME OVER! Press SPACE to restart.", 100, 200)
        }
    }

    override fun actionPerformed(e: ActionEvent?) {
        if (isGameOver) return

        enemyY += 5 + (score / 5)

        val playerRect = java.awt.Rectangle(playerX, playerY, 40, 40)
        val enemyRect = java.awt.Rectangle(enemyX, enemyY, 40, 40)

        if (playerRect.intersects(enemyRect)) {
            isGameOver = true
            timer.stop()
        }

        if (enemyY > 400) {
            enemyY = 0
            enemyX = (0..350).random()
            score++
        }

        repaint()
    }
}