# clojure/
    => only `[org.clojure/clojure "1.12.0"]`

# com/badlogic/gdx/
    => 1 class == 1 namespace and no cross dependencies
        => depending on 'com.badlogic.gdx' packages:
                 [com.badlogicgames.gdx/gdx                   "1.14.2"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl3    "1.14.2"]
                 [com.badlogicgames.gdx/gdx-freetype          "1.14.2"]

# ctx/
    => game 'application'
        => knows about game application context map :ctx/foo :ctx/bar
            => nothing should depend on this
            => can depend on everything else (?)

                (NOT ON DEVLOOP, OTHER APPS< EDITOR LEVLEGEN )

# dev/
    probably some parts go to 'clojure/'
        the dev.loop also usees nrepl & tools.namespace (separate folder/level/etc. )

# editor/
    => editor 'application'
        => knows about its context map shape
            -> maybe shouldnt have same name for grepping than game
                :ctx/input :editor/input ???
                not sure?
                can reuse ctx fns with same :app/ or :ctx/names ?

# game/
    Just the state, should probably be merged with 'ctx'

# gdx/
    convinience layer which exposes combinatioric functions/shorter names
        for dealing with libgdx framework libraries (space/com/lwjgl through org/...)
            ideally only one who knows about com/space/org.lwjgl

# levelgen-test/
    another application, same rules as editor/ctx

# malli/
    Not clear defined
        Dependency facade to : [metosin/malli "0.13.0"]
        Although the 'map-form' idea is pure clojure .... just a data shape ???

        we sdhould validate document shape at boundaries? and maybe dont need huge malli library for simple validation we are doing???

# moon/
    not well defined layer , main TODO

# org/lwjgl/system/
    1 - 1 class - namespace for lwjgl classes

# space/earlygrey/shape-drawer
    1 - 1 class - namespace for shape drawer

# world-fns
    not totally well defined yet
        the world creation pipelines not sure they should be in multiple files
        some hlepers could maybe go to core/clojure or gdx ?
