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
  
  val layout = new BoxPanel(Orientation.Vertical) {
    contents += newGameButton
    contents += restartButton
  }
  
  
  contents = layout
}