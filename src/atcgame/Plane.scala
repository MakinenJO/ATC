package atcgame

import javafx.geometry.Point2D
import scala.swing.Label
import javax.swing.ImageIcon
import java.awt.Point

class Plane(var x: Double = 0, var y: Double = 0) {
  
  def move() = {
    x += 0.1
    y += 0.1
  }
}