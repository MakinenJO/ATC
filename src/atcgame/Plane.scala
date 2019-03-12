package atcgame

import javafx.geometry.Point2D
import scala.swing.Label
import javax.swing.ImageIcon
import java.awt.Point

class Plane(var x: Int = 0, var y: Int = 0, var radian: Double = 0.0) {
  
  val centerX = 450
  val centerY = 450
  var orbitRadius = 200
  var speed = 0.5
  
  def move(timeDelta: Long) = {
    radian += speed * timeDelta / 1000
    x = centerX + (orbitRadius * Math.cos(radian)).toInt
    y = centerY + (orbitRadius * Math.sin(radian)).toInt
  }
  
  def facingAngle = radian + Math.PI * 3 / 4
}