package atcgame

import javafx.geometry.Point2D
import scala.swing.Label
import javax.swing.ImageIcon
import java.awt.Point

class Plane(var x: Int = 0, var y: Int = 0, var radian: Double = 0.0) {
  
  var orbit = 4
  val centerX = 450
  val centerY = 450
  var orbitRadius = 100.0 * orbit + 20
  var velocity = 100
  def vAngular = velocity / orbitRadius
  var state: PlaneState = Orbiting
  
  def move(timeDelta: Long) = {
    state.move(timeDelta)
  }
  
  def facingAngle = radian + Math.PI * 3 / 4
  
  def descend() = {
    orbit -= 1
    state = Descending
  }
  
  sealed trait PlaneState {
    def move(timeDelta: Long): Unit
  }
  
  case object Orbiting extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      x = (centerX + orbitRadius * Math.cos(radian)).toInt
      y = (centerY + orbitRadius * Math.sin(radian)).toInt
    }
  }
  
  case object Descending extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      orbitRadius -= 0.1
      x = (centerX + orbitRadius * Math.cos(radian)).toInt
      y = (centerY + orbitRadius * Math.sin(radian)).toInt
      if(orbitRadius <= 100.0 * orbit + 20)
        state = Orbiting
    }
  }
}