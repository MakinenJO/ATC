package atcgame

import scala.swing._

import scala.collection.mutable.Buffer

import scala.concurrent.ExecutionContext.Implicits.global

import atcgame.ui.GameUI

class Game(val ui: GameUI) extends {
  val planes = Buffer[Plane]()
  val addInterval = 10000
  var timeTillAdd = addInterval
  
  private var time = System.currentTimeMillis()
  
  def addPlane(p: Plane) = {
    planes += p
    ui.addArrivingPlane(p)
  }
  
  def start() = {
	  addPlane(new Plane("Plane0", -100, -100, 0.0))
	  addPlane(new Plane("Plane1", -100, -100, 1.0))    
  }
  
  def step() = {
	  val currentTime = System.currentTimeMillis()
	  val timeDelta = currentTime - time
    
	  scala.concurrent.Future {
			planes.foreach(_.move(timeDelta)) 
			
    }
    
	  timeTillAdd -= timeDelta.toInt
		if(timeTillAdd <= 0) {
		  addPlane(new Plane("Plane" + planes.size, -100, -100, 0.0))
		  timeTillAdd = addInterval
		}
		time = currentTime
  }
  
  
  def drawAirfield(g: Graphics2D) {
    planes.foreach(_.draw(g))
  }
  
  
}