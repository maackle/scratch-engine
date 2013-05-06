package scratch.core

import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import scratch.vector.{vec3, vec2, vec}
import scratch.gl


trait StateBase extends Update with Render with EnterExit with EventSink {

  implicit val app:ScratchApp

  protected[scratch] def onEnter:Unit
  protected[scratch] def onExit:Unit

}

abstract class ScratchState(val app:ScratchApp) extends StateBase {


}


trait State3D extends StateBase {

  def view:View3D

  override def __render() {
    view {
      super.__render()
    }
  }

  override def __onEnter() {
    scratch.GLView.perspective()
    super.__onEnter()
  }

}


class StateMachine(startState:StateBase) {
  assert(startState != null)
  private val stack = collection.mutable.Stack[StateBase]()

  push(startState)

  def current = {
    assert(!stack.isEmpty)
    stack.top
  }

  def change(s:StateBase) {
    assert(!stack.isEmpty)
    current.__onExit
    stack.pop()
    stack.push(s)
    current.__onEnter
  }

  def push(s:StateBase) {
    stack.push(s)
    current.__onEnter
  }

  def pop() {
    assert(!stack.tail.isEmpty)
    val top = stack.pop()
    top.__onExit
  }
}
