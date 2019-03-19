package atcgame

import scala.swing._
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp

import scala.concurrent.ExecutionContext.Implicits.global

class Plane(var x: Int = 0, var y: Int = 0, var radian: Double = 0.0) {
  
  var orbit = 4
  val centerX = 450
  val centerY = 450
  var orbitRadius = 100.0 * (orbit+1) //+ 20
  var velocity = 150.0
  def vAngular = velocity / orbitRadius
  var state: PlaneState = Approaching
  
  def move(timeDelta: Long) = {
    state.move(timeDelta)
  }
  
  def facingAngle = state.facingAngle
  
  def descend() = {
    //maybe just call this after text has been displayed
    scala.concurrent.Future {
      Thread.sleep(1000)
      orbit -= 1
      state = Descending
    }
    
  }
  
  sealed trait PlaneState {
    def move(timeDelta: Long): Unit
    def facingAngle: Double
  }
  
  case object Orbiting extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      x = (centerX + orbitRadius * Math.cos(radian)).toInt
      y = (centerY + orbitRadius * Math.sin(radian)).toInt
    }
    
    def facingAngle = radian + Math.PI * 3 / 4
  }
  
  case object Descending extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      orbitRadius -= 0.1
      x = (centerX + orbitRadius * Math.cos(radian)).toInt
      y = (centerY + orbitRadius * Math.sin(radian)).toInt
      if(orbitRadius <= 100.0 * orbit)
        state = Orbiting
    }
    
    def facingAngle = radian + 0.1 + Math.PI * 3 / 4
  }
  
  case object Approaching extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      orbitRadius -= 0.3
      velocity -= 0.1
      x = (centerX + orbitRadius * Math.cos(radian)).toInt
      y = (centerY + orbitRadius * Math.sin(radian)).toInt
      if(orbitRadius <= 100.0 * orbit) {
        state = Orbiting
        velocity = 100.0
      }
    }
    
    def facingAngle = radian + 0.3 + Math.PI * 3 / 4
  }
  
  
  def draw(g: Graphics2D) {
    //translate to center of image and rotate around center
	  val at = new AffineTransform()
	  at.translate(-Plane.dX, -Plane.dY)
	  at.rotate(facingAngle, Plane.planeImage.getWidth/2, Plane.planeImage.getHeight/2)
	  val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
	  //val image = op.filter(planeImage, null)    			  
    g.drawImage(Plane.planeImage, op, x, y)
    g.drawString("Plane", x - 10, y - 30)
  }
  
  
}


object Plane {
  val planeImage = ImageIO.read(new File("img/plane2.png"))
  val dX = planeImage.getWidth / 2
  val dY = planeImage.getHeight / 2
}