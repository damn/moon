Start writing and doing things together.
That way I can share progress and see also what I did so far and maybe straighten up/getless confused.

So I have this project. What is it? A folder hierarchy with files

▸ java-src/
▸ resources/
▸ src/
▸ target/
▸ test/
  check_uberjar.sh
  count_java.sh
  count_locs.sh
  current
  design_decisions.md
  documentation.md
  EXTEND_TYPE_NO_WAY_RELATED.md
  imports
  moon.iml
  private
  project.clj
  README.md
  transitive.md

The most important part is the clojure 'src/' and there we go with the `lein hiera :layout :horizontal` to see the connections in the codebase.
From there we understand the big picture.

We can see use of protocol to keep the hierarcht flat and the code decoupled mostly.

We are highlighting the transitive dependencies in red now to see probably problem areas.

Also project can most likely be split up.

Now we don't care much for names but FORMS! figure out names later

Folder hierarchy should be like dependency hierarchy too
(now no clojure,gdx,etc. matteres really )

(Next step would probably not to depend on concretion classes with implementation )

Transitive dependencies:
```
moon.player-item-on-cursor => ?
moon.order => ?
moon.stats => VERY MUCH
moon.nads  => pipeline fn
moon.val-max => access through hp/damage/mana protocol?
com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable => unanvoidable?
```

Otherwise good, mostly flat.
Also external dependencies or imports are not shown
Soo all what is dependent on can be one 'layer' ?
and see ...

Also count line of codes and count comments to see problem areas.


BUUUUT

Also had the idea to go 'deeper'
Not wider, game is endless.
But engine is not ....
Abstraction for libgdx called 'gdl' ? or whatever ???
And port it to clojure step by step ?
Integrate all java code ... make separate libraries no Gdx state
quite straightforward (chatgpt ) work and _foundational_

=> the whole game depends on libgdx

=> port one package first (smallest freetype ) or the tiledmapdrawer
and see how it goes
or ony upto 'Gdx/' / 'Application' and pass state
also add tests

Because if we look at project.clj we see the foundation is how to talk with the 'hardware'
thats the game engine:

                 [com.badlogicgames.gdx/gdx                   "1.14.0"]
                 [com.badlogicgames.gdx/gdx                   "1.14.0"]
                 [com.badlogicgames.gdx/gdx-freetype          "1.14.0"]
                 [com.badlogicgames.gdx/gdx-freetype-platform "1.14.0" :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl3    "1.14.0"]
                 [com.badlogicgames.gdx/gdx-platform          "1.14.0" :classifier "natives-desktop"]
                 [space.earlygrey/shapedrawer "2.6.0"]

This is the foundation, going deeper, straightforward and effective
But if I do anything there have to do the tests first
Or leave it alone ? I mean its fine right ?
java-osrc/TiledMapRenderer could check ...

or make PR for Stage/TiledMapRenderer (colorsetter) ?

~~~

OTHER PERSPECTIVE
THERE IS NO GAME OR NAMES JUST +FORMS+ and how they are connected
WHAT IS THE BIGGEST FILE?
schemas_impl
CAN BE SUPER EASY SPLIT UP BY KEYWORD NAME !
FORMS NO NAMES!!!

TOP 10 LoC:
     194 src//moon/create/add_stage_actors/windows/inventory.clj
     222 src//moon/tx/spawn_entity.clj
     230 src//moon/render/draw_on_world_viewport/draw_entities.clj
     273 src//moon/render/if_not_paused/tick_entities.clj
     303 src//moon/grid2d.clj
     306 src//moon/world_fns/modules.clj
     331 src//moon/impl/grid.clj
     335 src//moon/info_impl.clj
     352 src//moon/effect_impl.clj
     531 src//moon/schema_impl.clj
    8993 total
