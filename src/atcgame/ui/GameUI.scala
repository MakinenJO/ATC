package atcgame.ui


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
import scala.swing.event.WindowDeiconified
import scala.swing.event.WindowIconified

class GameUI extends SimpleSwingApplication {
  
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

	
	
  val arrivalsView = new FlightListView(this, "Arrivals", 400, 500, 10, 10)
  
  val departuresView = new FlightListView(this, "Departures", 400, 500, 10, 510)
  
  val airfieldView = new AirFieldView(this, "Airport", 900, 900, 400, 50)
  
  val gameInfoView = new GameInfoView(this, "Info", 400, 200, 1300, 200)
  
  
  //val window5 = new AirFieldView(this, "Testi", 300, 300, 300, 300)
  
  def top = new MainFrame {
    val windows = Vector(arrivalsView, departuresView, airfieldView, gameInfoView)
		//visible = false
		title = "Air Traffic Contol"
		//iconify()
		//dispose()
		
		reactions += {
      case w: WindowDeiconified => {
        windows.foreach(_.peer.setState(java.awt.Frame.NORMAL))
      }
      case v: WindowIconified => {
        //TODO: pause game
        windows.foreach(_.peer.setState(java.awt.Frame.ICONIFIED))
      }
    }
	}
  
  
  
  def end() = {
    val result = Dialog.showConfirmation(null, "Do you want to quit?", "Quit", Dialog.Options.YesNo)
    if(result == Dialog.Result.Ok) sys.exit()
  }

}