package scratch.core

import java.io.{FileInputStream, File}
import grizzled.slf4j.Logging

trait Resource[T <: Resource.Bound] extends Logging {
  import Resource._

  protected val fullPath: Locator
  protected val loadFn: Locator => T
  private var x:T = null.asInstanceOf[T]

  def map[A <: Bound](fn:(T=>A)) = {
    Resource(fullPath)(loc => {
      fn(loadFn(loc))
    })
  }

  def is = {
    if (! isLoaded) throw ResourceAccessException
    x
  }

  def option = Option( is )

  def isLoaded = { x != null }

  private def load() = {
    //TODO: exception for IO error
    x = loadFn(fullPath)
  }

  def refresh() = {
    load()
    info("resource refreshed: %s" format fullPath)
  }

  object ResourceAccessException extends Exception("tried to access resource at '%s' before loading" format fullPath)

}

object Resource extends Logging {

  type Locator = String
  type Bound = AnyRef

  private var basePath = ""
  private var autoload_? = false
  private var unloaded, loaded = Set[Resource[_]]()

  def apply[T <: Bound](locator:Locator)(loader: String => T) = {
    val reso = new Resource[T] {
      val fullPath = getFile(locator).getPath
      val loadFn = loader
    }

    if(autoload_?) {
      reso.load()
    } else {
      unloaded += reso
    }

    reso
  }

  def autoload() {
    autoload_? = true
    unloaded.toSeq.foreach { r =>
      r.load()
      unloaded -= r
      loaded += r
    }
  }

  def setPath(path:String) { basePath = path }

  def getFile(path:Locator) = {
    new File(basePath, path)
  }

  def getInputStream(path:Locator) = {
    new FileInputStream( getFile(path) )
  }

  implicit def path2stream(path:String) = getInputStream(path)

}
