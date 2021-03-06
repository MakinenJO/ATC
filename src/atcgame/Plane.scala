package atcgame

import scala.swing._
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp

import scala.concurrent.ExecutionContext.Implicits.global
import java.awt.Color
import scala.util.Random

sealed abstract class Plane(val game: Game, val name: String) {
  
  var x = -100.0
  var y = -100.0
  var radian = 3.0
  var altitude = 4
  val centerX = 450
  val centerY = 450
  var orbitRadius = 500.0
  var velocity = 150.0
  var deceleration = 5.0
  var acceleration = 5.0
  var state: PlaneState = Approaching
  var selected = false //is plane hovered over in flightlistview
  var crashAlert = false //is plane too close to another plane
  
  val points: Int
  
  var targetX = 0
  var targetY = 0
  var runwayAlignAngle = 0.0
  var approachAngle = 0.0
  var targetName = ""

  
  def vAngular = velocity / orbitRadius
  
  def move(timeDelta: Long) = {
    state.move(timeDelta)
  }
  
  def facingAngle = state.facingAngle
  
  def descend() = {
    scala.concurrent.Future {
      Thread.sleep(2000)
      altitude = 3
      state = Descending
    }
  }
  
  def land(targetExit: Exit, targetRunway: Runway) = {
    scala.concurrent.Future{
      Thread.sleep(5000)
  	  targetX = targetExit.x
  	  targetY = targetExit.y
  	  targetName = targetExit.name
  	  runwayAlignAngle = targetRunway.landingAngle(targetExit)
  	  approachAngle = (math.atan2((targetY - centerY), (targetX - centerX)) + 2*math.Pi - 1) % (2 * math.Pi)
  	  state = PreparingForLanding
    }
  }
  
  def assignGate(gate: Gate) = {
    scala.concurrent.Future{
      Thread.sleep(7000)
      altitude = 0
      x = gate.x + 20
      y = gate.y + 20
      addNewDeparture()
      state = OnGate
    }
  }
  
  def takeoff(targetExit: Exit, targetRunway: Runway) = {
    scala.concurrent.Future{
      Thread.sleep(10000)
      altitude = 1
      runwayAlignAngle = targetRunway.landingAngle(targetExit) + 2 * math.Pi
      x = targetExit.x
      y = targetExit.y
      velocity = 0
      acceleration = 1
      state = PreparingForTakeoff
    }
  }
  
  def crashesWith(p: Plane) = (this sameHeightWith p) && (this overlapsWith p)
  
  private def sameHeightWith(p: Plane) = altitude == p.altitude
  private def overlapsWith(p: Plane) = {
    val distX = math.abs(p.x - x)
    val distY = math.abs(p.y - y)
    if(distX < 64 && distY < 64) {crashAlert = true; p.crashAlert = true}
    else {crashAlert = false; p.crashAlert = false}
    distX < 32 && distY < 32
  }
 
  def stateDescription = state.description
  
  def addNewDeparture() = game.addNewDeparture(this)
  
  def readyToDescend = state == Orbiting && altitude == 4
  
  def readyToLand = state == Orbiting && altitude == 3
  
  def hasLanded = state == Landed
  
  def hasArrived = state == OnGate
  
  def readyToDepart = state == ReadyToDepart
  
  def hasDeparted = state == Departed
  
  sealed abstract trait PlaneState {
    def move(timeDelta: Long): Unit
    def facingAngle: Double
    def description: String
  }
  
  case object Orbiting extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      radian %= math.Pi * 2
      x = (centerX + orbitRadius * math.cos(radian))
      y = (centerY + orbitRadius * math.sin(radian))
    }
    
    override def facingAngle = radian + math.Pi * 3 / 4
    override def description = if(altitude==4) "Orbiting" else "Request land"
  }
  
  case object Descending extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000 % (math.Pi * 2)
      radian %= math.Pi * 2
      orbitRadius -= 0.1
      velocity -= deceleration * 0.5 * timeDelta / 1000
      x = (centerX + orbitRadius * math.cos(radian))
      y = (centerY + orbitRadius * math.sin(radian))
      if(orbitRadius <= 450 - (4 - altitude) * 50) {
        state = Orbiting
        velocity = 80.0
      }
    }
    
    override def facingAngle = Orbiting.facingAngle + 0.1
    override def description = "Descending"
  }
  
  case object Approaching extends PlaneState {
    override def move(timeDelta: Long) = {
      radian += vAngular * timeDelta / 1000
      radian %= math.Pi * 2
      orbitRadius -= 0.3
      velocity -= deceleration * 2 * timeDelta / 1000
      x = (centerX + orbitRadius * math.cos(radian))
      y = (centerY + orbitRadius * math.sin(radian))
      if(orbitRadius <= 450 - (4 - altitude) * 50) {
        state = Orbiting
        velocity = 100.0
      }
    }
    
    override def facingAngle = Orbiting.facingAngle + 0.3
    override def description = "Approaching"
  }
  
  
  case object PreparingForLanding extends PlaneState {
    override def move(timeDelta: Long) = {
      if(math.abs(radian - approachAngle) > 0.01) Orbiting.move(timeDelta)
      else {
        orbitRadius = math.hypot(x - targetX, y - targetY)
        radian = (math.atan2((targetY.toDouble - y) , (targetX.toDouble - x)) + math.Pi) % (math.Pi*2)
        altitude = 2
        state = ApproachingRunway
      }
    }
    
    override def facingAngle = Orbiting.facingAngle
    override def description = "Landing ->" + targetName
  } 
  
  
  case object ApproachingRunway extends PlaneState {
    override def move(timeDelta: Long) = {
      //println("radian: " + radian + " alignangle: " + runwayAlignAngle)
      if(math.abs(radian - runwayAlignAngle) < 0.03) {
        altitude = 1
        state = Landing
      }
      else {
        radian += vAngular * timeDelta / 1000
        radian %= math.Pi * 2
        orbitRadius -= 1
        velocity -= deceleration * timeDelta / 1000
        x = (targetX + orbitRadius * math.cos(radian))
        y = (targetY + orbitRadius * math.sin(radian))
      }
    }
    
    override def facingAngle = Orbiting.facingAngle + 0.6
    override def description = "Landing ->" + targetName
  } 
  
  
  case object Landing extends PlaneState {
    override def move(timeDelta: Long) = {
      if(velocity <= 0) state = Landed
      else {
        x -= (velocity * timeDelta / 1000 * math.cos(runwayAlignAngle))
        y -= (velocity * timeDelta / 1000 * math.sin(runwayAlignAngle))
        velocity -= deceleration * timeDelta / 1000
        deceleration += deceleration * 0.1 * timeDelta / 1000
      }
    }
    
    override def facingAngle = runwayAlignAngle - math.Pi * 3 / 4
    override def description = "Landing ->" + targetName
  }
  
  
  case object Landed extends PlaneState {
    override def move(timeDelta: Long) = {
      
    }
    
    override def facingAngle = math.Pi * 3 / 4
    override def description = "Landed"
  }
  
  
  case object OnGate extends PlaneState {
    var waitTime: Long = 30000 + Random.nextInt(3) * 10000
    override def move(timeDelta: Long) = {
      waitTime -= timeDelta
      if(waitTime <= 0) {
        state = ReadyToDepart
      }
    }
    
    override def facingAngle = 0.0
    override def description = "Boarding"
  }
 
  /* TODO: move logic for time passed when taxiing here
   * show target gate/runway in description
  case object TaxiingToGate extends PlaneState {
    var waitTime: Long = 
  }
  */
  
  case object ReadyToDepart extends PlaneState {
    override def move(timeDelta: Long) = {
      
    }
    override def facingAngle = -math.Pi * 3 / 4
    override def description = "Ready"
  }
  
  
  case object PreparingForTakeoff extends PlaneState {
    var waitTime: Long = 5000
    
    override def move(timeDelta: Long) = {
      waitTime -= timeDelta
      if(waitTime <= 0) state = TakingOff
    }
    
    override def facingAngle = runwayAlignAngle - math.Pi * 3 / 4
    override def description = "Taxiing"
  }
  
  
  case object TakingOff extends PlaneState {
    override def move(timeDelta: Long) = {
      if(velocity > 1000) {
        altitude = 1
        state = Departed
      }
      
      else {
        x -= (velocity * timeDelta / 1000 * math.cos(runwayAlignAngle))
        y -= (velocity * timeDelta / 1000 * math.sin(runwayAlignAngle))
        velocity += acceleration * timeDelta / 1000
        acceleration += acceleration * 0.3 * timeDelta / 1000
      }
    }
    
    override def facingAngle = runwayAlignAngle - math.Pi * 3 / 4
    override def description = if(velocity > 200) "Departed" else "Takeoff"
  }
  
  case object Departed extends PlaneState {
    override def move(timeDelta: Long) = {}
    override def facingAngle = 0
    override def description = "Departed"
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
    
    //draw circle around plane if it's dangerously close to others
    if(crashAlert) {
      g.setColor(Color.ORANGE)
      g.drawOval(drawX - Plane.dX, drawY - Plane.dY, 40, 40)
      g.setColor(Color.BLACK)
    }
    
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
  def apply(g: Game) = {
    Random.nextInt(nOfPlaneTypes) match {
      case 0 => new PassengerPlane(g)
      case 1 => new FreightPlane(g)
      case _ => new JumboJet(g)
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
      case _: JumboJet       => jumboJetImage
    }
  }
  
  val carrierNames = Vector("AY", "LH", "SN", "OS", "EY", "CA", "TP")
  def randomFlightName = {
    Random.shuffle(carrierNames).head + (Random.nextInt(9000) + 1000)
  }
}

class PassengerPlane(g: Game) extends Plane(g, Plane.randomFlightName) {
  deceleration = 6.0
  val points = 100
}

class FreightPlane(g: Game) extends Plane(g, Plane.randomFlightName) {
  deceleration = 5.0
  val points = 150
}

class JumboJet(g: Game) extends Plane(g, Plane.randomFlightName) {
  deceleration = 4.0
  val points = 300
}