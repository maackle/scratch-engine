
resources
---------

perhaps one "loader" function is enough.  user can specify reload behavior in there.  constructor can take either

    File => T
or
    (File, T) => T

or: better yet!  A class can be said to be dependent on a resource, and it will define its own reload class

    trait ResourceReload {
        val resourceDependencies:Iterable[Resource[_]]
        protected def onReload()
    }

of course such a class should have something in common with a `Resource` so it can be depended upon as well.

and perhaps there should be something similar to `Resource::map` that creates something other than a `Resource`.  I think map won't work well anyway, since we're registering the path, which will get overwritten.  Unless there's a way around that?

The mapped version would need the entire Resource interface, but instead of being mapped to its source path it would list its mappee as a dependency and so would be refreshed when its dependency is reloaded.

- hot reloading via callback
- subclass for image/sprite (later sound), update things like dimensions or animation