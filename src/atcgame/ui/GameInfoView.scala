package atcgame.ui

import java.awt.Window.Type
import scala.swing._

class GameInfoView
   (parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 
    
extends ATCWindow(parent, title, width, height, offsetX, offsetY, Type.NORMAL) {
  
  val newGameButton = new Button("New Game") {
    reactions += {
      case event.ButtonClicked(_) => {
        parent.newGame()
      }
    }
  }
  
  val restartButton = new Button("Restart") {
    reactions += {
      case event.ButtonClicked(_) =>{
        parent.restart()
      }
    }
  }
  
  val points = new Label("Points: 0") {
    def updatePoints(num: Int) = {
      text = "Points: " + num
    }
  }
  
  val layout = new BoxPanel(Orientation.Vertical) {
    contents += newGameButton
    contents += restartButton
    contents += points
  }
  
  
  contents = layout
  
  def update() = {
    points.updatePoints(game.points)
  }
}