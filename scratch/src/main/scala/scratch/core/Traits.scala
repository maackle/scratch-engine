package scratch.core

import scratch.gl

trait Hook {}

trait Update extends Hook {
  def update(dt:Float)
  private[scratch] def __update(dt:Float) = update(dt)
}

trait Render extends Hook {
  def render()
  private[scratch] def __render() = {
    render()
  }
}

trait EnterExit extends Hook {

  protected def onEnter()
  private[scratch] def __onEnter() = {
    onEnter
  }

  protected def onExit()
  private[scratch] def __onExit() = {
    onExit
  }
}
