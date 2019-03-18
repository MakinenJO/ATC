package atcgame.ui

import java.awt.Window.Type
import scala.swing.Button

class GameInfoView
   (parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 
    
extends ATCWindow(parent, title, width, height, offsetX, offsetY, Type.NORMAL) {
  val button = new Button("restart")
  contents = button
}