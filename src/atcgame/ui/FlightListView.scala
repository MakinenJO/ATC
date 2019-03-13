package atcgame.ui

import java.awt.Window.Type
import java.awt.GridLayout
import scala.swing.Button

class FlightListView
(parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 
    
extends ATCWindow(parent, title, width, height, offsetX, offsetY, Type.UTILITY) {
  val button = new Button("Descend")
  contents = button
}