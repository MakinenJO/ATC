package atcgame

import scala.swing._
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp

import scala.concurrent.ExecutionContext.Implicits.global
import java.awt.Color

class Plane(val name: String, var x: Double = 0, var y: Double = 0, var radian: Double = 0.0) {
  
  var orbit = 4
  val centerX = 450
  val centerY = 450
  var orbitRadius = 100.0 * (orbit+1) //+ 20
  var velocity = 150.0
  def vAngular = velocity / orbitRadius
  var state: PlaneState = Approaching
  var selected = false //is plane hovered over in flightlistview
  
  var targetX = 0
  var targetY = 0
  var targetAngle = 0.0
  
  def move(timeDelta: Long) = {
    state.move(timeDelta)
  }
  
  def facingAngle = state.facingAngle
  
  def descend() = {
    //maybe just call this after text has been displayed ie. no need for future
    scala.concurrent.Future {
      Thread.sleep(1000)
      orbit -= 1
      state = Descending
    }
    
  }
  
  def land(targetExit: (Int, Int), targetRunway: Runway) = {
	  targetX = targetExit._1
	  targetY = targetExit._2
	  targetAngle = targetRunway.approachAngle(targetExit)
	  println(targetAngle)
    state = Landing
  }
  
  def crashesWith(p: Plane) = {
    math.abs(p.x - x) < 32 && math.abs(p.y - y) < 32
  }
  
  
  sealed trait PlaneState {
    def move(timeDelta: Long): Unit
    def facingAngle: Double
  }
  
  case object Orbiting extends PlaneState {
    override def move(timeDelta: Long) = {
      //println(radian % (Math.PI*2))
      radian += vAngular * timeDelta / 1000
      x = (centerX + orbitRadius * Math.cos(radian))//.toInt
      y = (centerY + orbitRadius * Math.sin(radian))//.toInt
    }
    
    override def facingAngle = radian + Math.PI * 3 / 4
  }
  
  case object Descending extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      orbitRadius -= 0.1
      x = (centerX + orbitRadius * Math.cos(radian))//.toInt
      y = (centerY + orbitRadius * Math.sin(radian))//.toInt
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
      x = (centerX + orbitRadius * Math.cos(radian))//.toInt
      y = (centerY + orbitRadius * Math.sin(radian))//.toInt
      if(orbitRadius <= 100.0 * orbit) {
        state = Orbiting
        velocity = 100.0
      }
    }
    
    def facingAngle = radian + 0.3 + Math.PI * 3 / 4
  }
  
  
  case object Landing extends PlaneState {
    var touchdown = false
    
    override def move(timeDelta: Long) = {
  		//println(radian % (Math.PI*2))
      if(touchdown == true) {
        println((velocity * timeDelta / 1000 * Math.cos(targetAngle)).toInt)
        x -= (velocity * timeDelta / 1000 * Math.cos(targetAngle))//.toInt
        y -= (velocity * timeDelta / 1000 * Math.sin(targetAngle))//.toInt
        velocity -= 0.1
      }
      
      else if(Math.abs(radian % (Math.PI*2) - targetAngle) > 0.01) 
        Orbiting.move(timeDelta)
      
      else {
        radian = targetAngle
        x = (centerX + orbitRadius * Math.cos(radian))//.toInt
        y = (centerY + orbitRadius * Math.sin(radian))//.toInt
        touchdown = true
      }
    }
    
    def facingAngle = {
      if(!touchdown) radian + Math.PI * 3 / 4 
      else targetAngle - Math.PI * 3 / 4
    }
  }
  
  
  
  case object Taxiing extends PlaneState {
    override def move(timeDelta: Long) = {
      
    }
    def facingAngle = Math.PI * 3 / 4
  }
  
  case object Takeoff extends PlaneState {
    override def move(timeDelta: Long) = {
      
    }
    def facingAngle = Math.PI * 3 / 4
  }
  
  //TODO: maybe move logic into companion object
  def draw(g: Graphics2D) {
    //translate to center of image and rotate around center
	  val at = new AffineTransform()
	  at.translate(-Plane.dX, -Plane.dY)
	  at.rotate(facingAngle, Plane.dX, Plane.dY)
	  val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR) 			  
    g.drawImage(Plane.planeImage, op, x.toInt, y.toInt)
    
    if(selected) g.setColor(Color.YELLOW) //selected plane will be highlighted with yellow
    else g.setColor(Color.BLACK)
    g.drawString(velocity.toInt.toString() + " kn", x.toInt - 10, y.toInt - 45)
    g.drawString(name, x.toInt - 10, y.toInt - 30)
  }
  
  
}

//contains information for drawing planes
object Plane {
  private val planeImage = ImageIO.read(new File("img/plane2.png"))
  private val dX = planeImage.getWidth / 2
  private val dY = planeImage.getHeight / 2
}