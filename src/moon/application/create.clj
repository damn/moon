(ns moon.application.create
  (:require moon.create.unorganised
            moon.create.spawn-enemies
            moon.create.raycaster
            moon.create.spawn-player
            moon.create.grid
            moon.create.content-grid
            moon.create.explored-tile-corners
            moon.create.add-stage-actors
            moon.create.tiled-map
            moon.create.into-record
            moon.create.render-z-order
            moon.create.max-speed
            moon.create.db
            moon.create.ctx-colors
            moon.create.controls
            moon.create.default-font
            moon.create.ui-viewport
            moon.create.stage
            moon.create.set-input-processor
            moon.create.skin
            moon.create.cursors
            moon.create.textures
            moon.create.world-unit-scale
            moon.create.world-viewport))

(defn do!
  [ctx]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          [
           [moon.create.unorganised/step]
           [moon.create.ui-viewport/step]
           [moon.create.stage/step]
           [moon.create.set-input-processor/step]
           [moon.create.skin/step]
           [moon.create.cursors/step]
           [moon.create.textures/step]
           [moon.create.world-unit-scale/step]
           [moon.create.world-viewport/step]
           [moon.create.default-font/step]
           [moon.create.controls/step]
           [moon.create.ctx-colors/step]
           [moon.create.render-z-order/step]
           [moon.create.max-speed/step]
           [moon.create.db/step]
           [moon.create.into-record/step]
           [moon.create.add-stage-actors/step]
           [moon.create.tiled-map/step]
           [moon.create.grid/step]
           [moon.create.content-grid/step]
           [moon.create.explored-tile-corners/step]
           [moon.create.raycaster/step]
           [moon.create.spawn-player/step]
           [moon.create.spawn-enemies/step]
           ]))
