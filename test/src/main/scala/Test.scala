package test

import skitch._
import skitch.vector.{vec2, vec}
import org.lwjgl.input.Keyboard
import scala.Some
import java.io.{FileInputStream, File}
import skitch.gfx
import scala.Some
import skitch.gl
import grizzled.slf4j.{Logger, Logging}
import skitch.gfx.Image
import skitch.core._
import scala.Some
import skitch.core.MouseDown
import scala.Some
import skitch.core.KeyHold
import skitch.core.components.Position2D

object Test extends SkitchApp with Logging { self =>

  val loader = new ResourceLoader(new File("/home/michael/code/skitch-dev/test/src/main/resources"))

  def initialize =  {
    loader.autoload()
    loader.watch()
  }

  def cleanup = ()

  val windowTitle = "Test test"
  val initialWindowSize = Some(600, 600)

  val startState = new ManagedTestState

  abstract class State extends SkitchState(this) {
    val loader = self.loader
  }

  run()
}

class Image4(image:Resource[Image]) extends ResourceDependent with Logging with Position2D {

	val resourceDependencies = Set(image)

	val position = vec(200, 200)
	val scale = vec2.one
	val rotation = 0.0

	def w = image.is.width
	def h = image.is.height

	def refresh(reso:ResourceLike) {
		info("refreshed %s due to %s" format (this, reso))
	}

	def render() {
		image.option.map({ im =>
			gl.matrix {
				gl.translate(position)
				for (t <- Seq(vec(0,0), vec(w, 0), vec(0, h), vec(-w, 0))) {
					gl.translate(t)
					im.render()
				}
			}
		})
	}
}

class ManagedTestState extends Test.State with LWJGLKeyboard with Logging {

	def onEnter = info("enter")
	def onExit  = info("exit")

	//  lazy val image = Image(common.getFile("img/snail3.png"))
	val image = loader.image("img/snail.png")

	val image4 = new Image4(image)

	val view = View2D(app)

	def update(dt:Float) {

	}

	def render() {
		val (w, h) = view.bounds.dimensions
		def circles() = {
			gl.clear(Color(0x222222))
			gl.fill(false)
			Color.red.bind()
			for(i <- -w to w by 10) {
				if(i > 0) Color.blue.bind()
				gfx.circle(4, vec(i, 0))
			}
		}
		view.apply {
			circles()
			image.option.map( im => im.render() )
			image4.render()
		}
	}

	listen {
		case KeyHold(Keyboard.KEY_EQUALS) => view.camera.zoom += 0.1f
		case KeyHold(Keyboard.KEY_MINUS) => view.camera.zoom -= 0.1f

		case KeyHold(KEY_A) => view.camera.position.x -= 1f
		case KeyHold(KEY_D) => view.camera.position.x += 1f
		case KeyHold(KEY_S) => view.camera.position.y -= 1f
		case KeyHold(KEY_W) => view.camera.position.y += 1f

		case KeyHold(KEY_LEFT) =>   image4.position.x -= 1f
		case KeyHold(KEY_RIGHT) =>  image4.position.x += 1f
		case KeyHold(KEY_DOWN) =>     image4.position.y -= 1f
		case KeyHold(KEY_UP) =>   image4.position.y += 1f


		case MouseDown(code, pos) => info("%s  %s" format (pos, view.toWorld(pos)))
	}

}
