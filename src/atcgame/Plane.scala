package atcgame

import scala.swing._
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp

import scala.concurrent.ExecutionContext.Implicits.global
import java.awt.Color
import scala.util.Random

class Plane(val name: String, var x: Double = 0, var y: Double = 0) {
  
  var radian = 3.0
  var orbit = 4
  val centerX = 450
  val centerY = 450
  var orbitRadius = 100.0 * (orbit+1) //+ 20
  var velocity = 150.0
  var acceleration = 5.0
  def vAngular = velocity / orbitRadius
  var state: PlaneState = Approaching
  var selected = false //is plane hovered over in flightlistview
  
  var targetX = 0
  var targetY = 0
  var landingAngle = 0.0
  var approachAngle = 0.0
  
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
  
  def land(targetExit: Exit, targetRunway: Runway) = {
	  targetX = targetExit.x
	  targetY = targetExit.y
	  landingAngle = targetRunway.approachAngle(targetExit)
	  approachAngle = (Math.atan2((targetY - centerY), (targetX - centerX)) + 2*math.Pi - 0.5) % (2 * math.Pi)
	  state = PreparingForLanding
  }
  
  def assignGate(gate: Gate) = {
    x = gate.x + 20
    y = gate.y + 20
    state = OnGate
  }
  
  def takeoff(targetExit: Exit, targetRunway: Runway) = {
    
  }
  
  def crashesWith(p: Plane) = (this sameHeightWith p) && (this overlapsWith p)
  
  private def sameHeightWith(p: Plane) = orbit == p.orbit
  private def overlapsWith(p: Plane) = math.abs(p.x - x) < 32 && math.abs(p.y - y) < 32
 
  
  
  
  
  sealed abstract trait PlaneState {
    def move(timeDelta: Long): Unit
    def facingAngle: Double
  }
  
  case object Orbiting extends PlaneState {
    override def move(timeDelta: Long) = {
      //println(radian % (Math.PI*2))
      radian += vAngular * timeDelta / 1000
      radian %= Math.PI * 2
      x = (centerX + orbitRadius * Math.cos(radian))//.toInt
      y = (centerY + orbitRadius * Math.sin(radian))//.toInt
    }
    
    override def facingAngle = radian + Math.PI * 3 / 4
  }
  
  case object Descending extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000 % (Math.PI * 2)
      radian %= Math.PI * 2
      orbitRadius -= 0.1
      x = (centerX + orbitRadius * Math.cos(radian))//.toInt
      y = (centerY + orbitRadius * Math.sin(radian))//.toInt
      if(orbitRadius <= 100.0 * orbit)
        state = Orbiting
    }
    
    def facingAngle = Orbiting.facingAngle + 0.1
  }
  
  case object Approaching extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      radian %= Math.PI * 2
      orbitRadius -= 0.3
      velocity -= acceleration * timeDelta / 1000
      x = (centerX + orbitRadius * Math.cos(radian))
      y = (centerY + orbitRadius * Math.sin(radian))
      if(orbitRadius <= 100.0 * orbit) {
        state = Orbiting
        velocity = 100.0
      }
    }
    
    def facingAngle = Orbiting.facingAngle + 0.3
  }
  
  
  case object PreparingForLanding extends PlaneState {
    override def move(timeDelta: Long) = {
      if(Math.abs(radian - approachAngle) > 0.01) Orbiting.move(timeDelta)
      else {
        orbitRadius = Math.hypot(x - targetX, y - targetY)
        radian = (Math.atan2((targetY.toDouble - y) , (targetX.toDouble - x)) + math.Pi) % (math.Pi*2)
        println(radian)
        state = ApproachingRunway
      }
    }
    
    override def facingAngle = Orbiting.facingAngle
  } 
  
  
  case object ApproachingRunway extends PlaneState {
    override def move(timeDelta: Long) = {
      //println(radian)
      if(math.abs(radian - landingAngle) < 0.01) state = Landing
      else {
        radian += vAngular * timeDelta / 1000
        radian %= Math.PI * 2
        orbitRadius -= 1
        velocity -= acceleration * timeDelta / 1000
        x = (targetX + orbitRadius * Math.cos(radian))
        y = (targetY + orbitRadius * Math.sin(radian))
      }
    }
    
    override def facingAngle = Orbiting.facingAngle + 0.6
  } 
  
  
  case object Landing extends PlaneState {
    override def move(timeDelta: Long) = {
      if(velocity <= 0) state = Landed
      else {
        x -= (velocity * timeDelta / 1000 * Math.cos(landingAngle))
        y -= (velocity * timeDelta / 1000 * Math.sin(landingAngle))
        velocity -= acceleration * timeDelta / 1000
        acceleration += acceleration * 0.3 * timeDelta / 1000
      }
    }
    
    override def facingAngle = landingAngle - Math.PI * 3 / 4
  }
  
  
  case object Landed extends PlaneState {
    override def move(timeDelta: Long) = {
      
    }
    
    def facingAngle = Math.PI * 3 / 4
  }
  
  
  case object OnGate extends PlaneState {
    override def move(timeDelta: Long) = {
      
    }
    
    override def facingAngle = 0.0
  }
  
  
  case object Takeoff extends PlaneState {
    override def move(timeDelta: Long) = {
      
    }
    def facingAngle = Math.PI * 3 / 4
  }
  
  
  
  def draw(g: Graphics2D) {
    //translate to center of image and rotate around center
	  val at = new AffineTransform()
	  at.translate(-Plane.dX, -Plane.dY)
	  at.rotate(facingAngle, Plane.dX, Plane.dY)
	  val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR) 
	  
	  //convert location to int for drawing
	  val drawX = x.toInt
	  val drawY = y.toInt
	  
	  //draw plane
    g.drawImage(Plane.image(this), op, drawX, drawY)
    
    //draw some info above plane
    if(selected) g.setColor(Color.YELLOW) //selected plane will be highlighted with yellow
    g.drawString(velocity.toInt.toString() + " kn", drawX - 10, drawY - 45)
    g.drawString(name, drawX - 10, drawY - 30)
    g.setColor(Color.BLACK)
  }
  
  
}

//contains information for drawing planes
object Plane {
  private val nOfPlaneTypes = 3
  
  //generates a plane of random type
  def apply() = {
    Random.nextInt(nOfPlaneTypes) match {
      case 0 => new PassengerPlane
      case 1 => new FreightPlane
      case _ => new JumboJet
    }
  }
  
  private val passengerImage = ImageIO.read(new File("img/passenger.png"))
  private val freightImage = ImageIO.read(new File("img/freight.png"))
  private val jumboJetImage = ImageIO.read(new File("img/jumbo.png"))
  
  private val dX = passengerImage.getWidth / 2
  private val dY = passengerImage.getHeight / 2
  
  def image(p: Plane) = {
    p match {
      case _: PassengerPlane => passengerImage
      case _: FreightPlane   => freightImage
      case _                 => jumboJetImage
    }
  }
  
  val carrierNames = Vector("AY", "LH", "SN", "OS", "EY", "CA", "TP")
  def randomFlightName = {
    Random.shuffle(carrierNames).head + (Random.nextInt(9000) + 1000)
  }
}

class PassengerPlane extends Plane(Plane.randomFlightName, -100, -100) {
  
}

class FreightPlane extends Plane(Plane.randomFlightName, -100, -100) {
  
}

class JumboJet extends Plane(Plane.randomFlightName, -100, -100) {
  
}