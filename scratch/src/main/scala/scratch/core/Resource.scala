package scratch.core

import java.io.{PrintWriter, IOException, FileInputStream, File}
import java.nio.file.{Path, WatchEvent, WatchKey, FileSystems}
import scratch.common
import java.lang.InterruptedException
import scala.collection.JavaConversions
import JavaConversions._
import grizzled.slf4j.Logging

trait Resource[T <: Resource.Bound] extends Logging {
	import Resource._

	protected val locator: Locator
	protected val loadFn: Loader[T]
//	protected val reloadFn: T => Unit
	private var x:T = null.asInstanceOf[T]

	def map[A <: Bound](fn:(T=>A)) = {
		Resource(locator)(file => {
			fn(loadFn(file))
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
		x = loadFn(locator.file)
	}

	def refresh() = {
		load()
		info("resource refreshed: %s" format locator.file)
	}

	object ResourceAccessException extends Exception("tried to access resource at '%s' before loading" format locator.file)

}

object Resource extends Logging {

	type Locator = Location
	type Loader[T <: Bound] = File => T
	type Bound = AnyRef
	type Bounded = Resource[_ <: Bound]

	private var baseDirectory:File = null
	private var autoload_? = false
	private var devmode_? = false
	private var unloaded = Set[Bounded]()
	private var loaded = Map[File, Bounded]()

	case class Location(path:String) {
		lazy val file = getFile(path)
	}

	def apply[T <: Bound](locator:String)(loader: Loader[T]/*, reloader: File => T = null*/):Resource[T] = {
		apply(Location(locator))(loader)
	}

	def apply[T <: Bound](locator:Locator)(loader: Loader[T]/*, reloader: File => T = null*/):Resource[T] = {
		val loc = locator
		val reso = new Resource[T] {
			val locator = loc
			val loadFn = loader
//			val reloadFn = reloader
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
			loaded += (r.locator.file -> r)
		}
	}

	private def setPath(path:String) {
		baseDirectory = new File(path)
		info("set path: " + path)
	}

	def getFile(path:String) = {
		if(devmode_?) {
			new File(baseDirectory, path)
		}
		else {
			common.getFile(path)
		}
		//    new File(basePath, path)
	}

	def getInputStream(path:String) = {
		new FileInputStream( getFile(path) )
	}

	implicit def path2stream(path:String) = getInputStream(path)

	private var watchedDirectories = Set[File]()

	def developmentMode(fullResourcePath:String) {

		devmode_? = true

		setPath(fullResourcePath)

		watchedDirectories += (baseDirectory)
		watchThread.start

		if (watchThread.isAlive) {
			debug("watch service is running")
		} else {
			error("couldn't start watch service")
		}
	}

	protected[scratch] lazy val watchThread = new Thread (

		new Runnable {

			def run() {

				import java.nio.file.StandardWatchEventKinds._

				val watcher = FileSystems.getDefault().newWatchService()

				val watched = collection.mutable.Map[WatchKey, File]()

				def subdirectories(dir:File):Array[File] = {
					if (! dir.isDirectory) {
						Array()
					} else {
						val these = dir.listFiles().filter(_.isDirectory)
						these ++ these.flatMap(subdirectories)
					}
				}

				def watchDir(dir:File) {
					try {
						val key = dir.toPath.register(watcher, ENTRY_MODIFY);
						info("watching directory: %s" format dir)
						watched(key) = dir
					} catch {
						case e:IOException =>
							error("failed to watch directory: %s" format dir)
					}
				}

				for (dir <- watchedDirectories) {

					watchDir(dir)

					for (sub <- subdirectories(dir)) {
						watchDir(sub)
					}
				}

				while(true) {

					val key =  try {
						watcher.take()
					} catch {
						case e:InterruptedException =>
							info("interrupted")
							return
					}

					for {
						event <- key.pollEvents()
						dir <- watched.get(key)
						kind = event.kind
					} {
						if(kind != OVERFLOW) {
							val filename = event.asInstanceOf[WatchEvent[Path]].context()
							info(dir.toPath.resolve(filename))
						}
					}

					val valid = key.reset();
					if (!valid) {
						return
					}

				}

			}
		}
	)

}
