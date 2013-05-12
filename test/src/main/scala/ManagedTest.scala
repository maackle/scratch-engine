package test

import grizzled.slf4j.Logging
import java.io.File
import org.lwjgl.input.Keyboard
import scala.Some
import skitch.core.KeyHold
import skitch.core.managed.components.Position2D
import skitch.core.managed.{View2D, SkitchState}
import skitch.core._
import skitch.core.MouseDown
import skitch.gfx.{Sprite, Image}
import skitch.{LWJGLKeyboard, gl, Color, gfx}
import skitch.vector.{vec2, vec}

object ManagedTest extends SkitchApp { self =>

	val loader = new ResourceLoader(new File("/home/michael/code/skitch-dev/test/src/main/resources"))

	def initialize =  {
		loader.autoload()
		loader.watch()
	}

	def cleanup = ()

	val windowTitle = "Test test"
	val initialWindowSize = Some(600, 600)

	val startState = new TestState

	abstract class State extends SkitchState(this) {
		val loader = self.loader
	}

	run()
}


class TestState extends ManagedTest.State with LWJGLKeyboard with Logging {

	def onEnter = info("enter")
	def onExit  = info("exit")

	val backgroundColor = Some(Color(0x222222))

	val image = loader.image("img/snail.png")

	val sprite = new Sprite(image)

	val things = Set(sprite)

	val view = View2D(things)

	val views = Seq (
		view
	)

	listen {
		case KeyHold(Keyboard.KEY_EQUALS) => view.camera.zoom += 0.1f
		case KeyHold(Keyboard.KEY_MINUS) => view.camera.zoom -= 0.1f

		case KeyHold(KEY_A) => view.camera.position.x -= 1f
		case KeyHold(KEY_D) => view.camera.position.x += 1f
		case KeyHold(KEY_S) => view.camera.position.y -= 1f
		case KeyHold(KEY_W) => view.camera.position.y += 1f

		case KeyHold(KEY_LEFT) =>   sprite.position.x -= 1f
		case KeyHold(KEY_RIGHT) =>  sprite.position.x += 1f
		case KeyHold(KEY_DOWN) =>     sprite.position.y -= 1f
		case KeyHold(KEY_UP) =>   sprite.position.y += 1f

		case MouseDown(code, pos) => info("%s  %s" format (pos, view.toWorld(pos)))
	}

}
