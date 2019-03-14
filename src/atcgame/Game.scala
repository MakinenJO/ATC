package atcgame

import scala.swing._

import scala.collection.mutable.Buffer
import scala.concurrent.ExecutionContext.Implicits.global


class Game {
  val planes = Buffer[Plane]()
  
  private var time = System.currentTimeMillis()
  
  planes += new Plane(10, 10, 0.0)
  planes += new Plane(50, 50, 1.0)
  
  def step() = {
    scala.concurrent.Future {
    	val currentTime = System.currentTimeMillis()
			val timeDelta = currentTime - time
			planes.foreach(_.move(timeDelta))      
			time = currentTime
    }
    
  }
  
}