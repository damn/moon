(ns moon.application.create
  (:require moon.application.create.gdx
            moon.application.create.unorganised
            moon.application.create.ui-impls
            moon.application.create.spawn-enemies
            moon.application.create.raycaster
            moon.application.create.spawn-player
            moon.application.create.grid
            moon.application.create.content-grid
            moon.application.create.explored-tile-corners
            moon.application.create.add-stage-actors
            moon.application.create.tiled-map
            moon.application.create.into-record
            moon.application.create.render-z-order
            moon.application.create.max-speed
            moon.application.create.db
            moon.application.create.ctx-colors
            moon.application.create.controls))

(defn do!
  [application]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          application
          [
           [moon.application.create.gdx/step]
           [moon.application.create.unorganised/step]
           [moon.application.create.controls/step]
           [moon.application.create.ctx-colors/step]
           [moon.application.create.render-z-order/step]
           [moon.application.create.max-speed/step]
           [moon.application.create.db/step]
           [moon.application.create.into-record/step]
           [moon.application.create.ui-impls/step]
           [moon.application.create.add-stage-actors/step]
           [moon.application.create.tiled-map/step]
           [moon.application.create.grid/step]
           [moon.application.create.content-grid/step]
           [moon.application.create.explored-tile-corners/step]
           [moon.application.create.raycaster/step]
           [moon.application.create.spawn-player/step]
           [moon.application.create.spawn-enemies/step]
           ]))
