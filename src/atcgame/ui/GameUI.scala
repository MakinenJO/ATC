package atcgame.ui

import atcgame.Game

import scala.swing._
import java.awt.Color
import javax.swing.JInternalFrame
import javax.swing.JDesktopPane
import javax.swing.JFrame
import java.awt.Window.Type
import javax.swing.WindowConstants
import javax.swing.UIManager
import javax.imageio.ImageIO
import java.io.File
import java.awt.event.ActionListener
import javax.swing.Timer

class GameUI extends SimpleSwingApplication {
  
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

	val game = new Game()
	
  val arrivalsView = new FlightListView(this, "Arrivals", 400, 500, 10, 10)
  
  val departuresView = new FlightListView(this, "Departures", 400, 500, 10, 510)
  
  val airfieldView = new AirFieldView(this, "Airport", 900, 900, 400, 50)
  
  val gameInfoView = new GameInfoView(this, "Air Traffic Control", 450, 400, 1300, 200) {
    
    val windows = Vector(arrivalsView, departuresView, airfieldView)
		
		reactions += {
      case w: event.WindowDeiconified => {
        windows.foreach(_.peer.setState(java.awt.Frame.NORMAL))
      }
      case v: event.WindowIconified => {
        //TODO: pause game
        windows.foreach(_.peer.setState(java.awt.Frame.ICONIFIED))
      }
    }
  }
  
  def top = gameInfoView
  
  
  val listener = new ActionListener() {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      game.step()
    }
  }
  
  val timer = new Timer(4, listener)
  timer.start()
  
  def end() = {
    val result = Dialog.showConfirmation(null, "Do you want to quit?", "Quit", Dialog.Options.YesNo)
    if(result == Dialog.Result.Ok) sys.exit()
  }

}