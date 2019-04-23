package atcgame

import scala.swing._

import scala.collection.mutable.Buffer

import scala.concurrent.ExecutionContext.Implicits.global

import atcgame.ui.GameUI
import java.awt.Color

class Game(val ui: GameUI) extends {
  val planes = Buffer[Plane]()
  val runways = Buffer[Runway]()
  val nOfGates = 5
  val gates = Buffer[Gate]()
  
  val addInterval = 10000
  var timeTillAdd = addInterval
  
  private var time = System.currentTimeMillis()
  
  def addPlane(p: Plane) = {
    planes += p
    ui.addArrivingPlane(p)
  }
  
  def start() = {
    createGates()
	  //runways += new Runway((250, 450), (650, 450))
	  runways += new Runway(new Exit(250, 650, "E1"), new Exit(650, 500, "E2"))
	  runways += new Runway(new Exit(450, 250, "F1"), new Exit(450, 650, "F2"))
	  runways += new Runway(new Exit(250, 250, "E1a"), new Exit(650, 650, "E2a"))
	  //runways += new Runway((100, 600), (600, 100))
	  addPlane(new PassengerPlane)
  }
  
  def step() = {
	  val currentTime = System.currentTimeMillis()
	  val timeDelta = currentTime - time
    
	  scala.concurrent.Future {
			planes.foreach(_.move(timeDelta)) 
			
    }
    
	  timeTillAdd -= timeDelta.toInt
		if(timeTillAdd <= 0) {
		  addPlane(Plane())
		  timeTillAdd = addInterval * (scala.util.Random.nextInt(4) + 1)
		}
	  
	  checkCollisions()
	  
		time = currentTime
  }
  
  def checkCollisions(): Boolean = {
    planes.foreach(plane1 => {
      planes.dropWhile(_ != plane1).drop(1).foreach(plane2 => {
        if(plane1.crashesWith(plane2)) return true
      })
    })
    return false
  }
  
  def createGates() {
    for(i <- 1 to nOfGates) {
      val x = 1000
      val y = 50 + i*90
      gates += new Gate(x, y, "A" + i)
    }
  }
  
  
  def drawAirField(g: Graphics2D) {
    gates.foreach(_.draw(g))
    runways.foreach(_.draw(g))
    planes.foreach(_.draw(g))
  }
  
  
}