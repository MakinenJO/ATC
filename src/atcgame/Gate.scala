package atcgame

import java.awt.Graphics2D
import java.awt.Color

case class Gate(x: Int, y: Int, name: String, var selected: Boolean = false) {
  
  def draw(g: Graphics2D) {
    g.setColor(Color.GREEN)
    g.fillRect(x, y, 40, 40)
    if(selected) g.setColor(Color.YELLOW) else g.setColor(Color.BLACK)
    g.drawString(name, x - 30, y + 20)
    g.setColor(Color.BLACK)
  }
}