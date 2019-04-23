package atcgame

import java.awt.Graphics2D
import java.awt.Color

case class Gate(x: Int, y: Int, name: String) {
  
  def draw(g: Graphics2D) {
    g.setColor(Color.GREEN)
    g.fillRect(x, y, 40, 40)
    g.setColor(Color.BLACK)
    g.drawString(name, x - 30, y + 20)
  }
}