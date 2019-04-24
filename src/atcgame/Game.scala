package atcgame

import scala.swing._

import scala.collection.mutable.Buffer

import scala.concurrent.ExecutionContext.Implicits.global



class Game extends {
  val planes = Buffer[Plane]()
  val addedArrivals = Buffer[Plane]()
  val addedDepartures = Buffer[Plane]()
  val runways = Buffer[Runway]()
  val nOfGates = 5
  val gates = Buffer[Gate]()
  var crash = false
  var points = 0
  
  val addInterval = 10000
  var timeTillAdd = addInterval
  
  private var time = 0L
  
  def addPlane(p: Plane) = {
    planes += p
    addedArrivals += p
  }
  
  def handleNewArrivals() = {
    val ret = addedArrivals.toVector
    addedArrivals.clear()
    ret
  }
  
  def addNewDeparture(p: Plane) = addedDepartures += p
  
  def handleNewDepartures() = {
    val ret = addedDepartures.toVector
    addedDepartures.clear()
    ret
  }
  
  def removePlane(p: Plane) = {
    points += p.points
    planes -= p
  }
  
  def start() = {
    time = System.currentTimeMillis()
    createGates()
	  //runways += new Runway((250, 450), (650, 450))
	  runways += new Runway(new Exit(250, 650, "E1"), new Exit(650, 500, "E2"))
	  //runways += new Runway(new Exit(450, 250, "F1"), new Exit(450, 650, "F2"))
	  runways += new Runway(new Exit(250, 250, "E1a"), new Exit(650, 650, "E2a"))
	  //runways += new Runway((100, 600), (600, 100))
    runways += new Runway(new Exit(450, 200, "W1"), new Exit(650, 400, "W2"))
	  addPlane(new PassengerPlane(this))
  }
  
  def step() = {
	  val currentTime = System.currentTimeMillis()
	  val timeDelta = currentTime - time
    
	  scala.concurrent.Future {
			planes.foreach(_.move(timeDelta)) 
			
    }
    
	  timeTillAdd -= timeDelta.toInt
		if(timeTillAdd <= 0) {
		  addPlane(Plane(this))
		  timeTillAdd = addInterval * (scala.util.Random.nextInt(4) + 1)
		}
	  
	  if(checkCollisions()) crash = true
	  
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
  
  def isLost = crash
  
  
  def createGates() {
    for(i <- 1 to nOfGates) {
      val x = 1000
      val y = 30 + i*90
      gates += new Gate(x, y, "A" + i)
    }
  }
  
  
  def drawAirField(g: Graphics2D) {
    gates.foreach(_.draw(g))
    runways.foreach(_.draw(g))
    planes.foreach(_.draw(g))
  }
  
  
}

object Game {
  
}