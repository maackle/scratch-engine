
resources
---------

perhaps there should be something similar to `Resource::map` that creates something other than a `Resource`.  I think map won't work well anyway, since we're registering the path, which will get overwritten.  Unless there's a way around that?

The mapped version would need the entire Resource interface, but instead of being mapped to its source path it would list its mappee as a dependency and so would be refreshed when its dependency is reloaded.

- hot reloading via callback
- subclass for image/sprite (later sound), update things like dimensions or animation



management
----------

everything can be either unmanaged, or managed.  managed is more like scalene