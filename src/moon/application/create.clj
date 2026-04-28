(ns moon.application.create
  (:require moon.create.unorganised
            moon.create.audio
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
            moon.create.batch
            moon.create.colors
            moon.create.shape-drawer
            moon.create.shape-drawer-texture
            moon.create.default-font
            moon.create.ui-viewport
            moon.create.stage
            moon.create.tooltip-config
            moon.create.set-input-processor
            moon.create.skin
            moon.create.cursors
            moon.create.textures
            moon.create.world-unit-scale
            moon.create.world-viewport)
  (:import (com.badlogic.gdx Gdx)))

(defn do!
  []
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          {
           :ctx/audio     Gdx/audio
           :ctx/files     Gdx/files
           :ctx/graphics  Gdx/graphics
           :ctx/input     Gdx/input

           ; frame
           :ctx/active-entities nil
           :ctx/delta-time nil
           :ctx/ui-mouse-position nil
           :ctx/world-mouse-position nil
           ;
           :ctx/mouseover-eid nil
           ; graphics
           :ctx/unit-scale (atom 1)
           ;
           ;
           ; world
           :ctx/elapsed-time 0
           :ctx/paused? false
           :ctx/factions-iterations {:good 15 :evil 5}
           ; TODO if it doens't change put in defs?
           :ctx/max-delta 0.04
           :ctx/minimum-size 0.39
           :ctx/z-orders [:z-order/on-ground
                          :z-order/ground
                          :z-order/flying
                          :z-order/effect]
           ;
           :ctx/potential-field-cache (atom nil)
           :ctx/id-counter (atom 0)
           :ctx/entity-ids (atom {})
           }
          [
           [moon.create.unorganised/step]
           [moon.create.tooltip-config/step]
           [moon.create.colors/step]
           [moon.create.audio/step]
           [moon.create.batch/step]
           [moon.create.shape-drawer-texture/step]
           [moon.create.shape-drawer/step]
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
