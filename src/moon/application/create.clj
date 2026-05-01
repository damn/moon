(ns moon.application.create
  (:require [moon.application.create.gdx-colors :as gdx-colors]
            [moon.application.create.tooltip-config :as tooltip-config]
            [moon.application.create.batch :as create-batch]
            [moon.application.create.shape-drawer-texture :as create-shape-drawer-texture]
            [moon.application.create.shape-drawer :as create-shape-drawer]
            [moon.application.create.audio :as create-audio]
            [moon.application.create.default-font :as create-default-font]
            [moon.application.create.textures :as create-textures]
            [moon.application.create.world-unit-scale :as create-world-unit-scale]
            [moon.application.create.world-viewport :as create-world-viewport]
            [moon.application.create.cursors :as create-cursors]
            [moon.application.create.ui-viewport :as create-ui-viewport]
            [moon.application.create.stage :as create-stage]
            [moon.application.create.skin :as create-skin]
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
  [app]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          {:ctx/app app
           :ctx/graphics  (.getGraphics app)
           :ctx/input     (.getInput app)}
          [
           [gdx-colors/step]
           [tooltip-config/step]
           [create-batch/step]
           [create-shape-drawer-texture/step]
           [create-shape-drawer/step]
           [create-audio/step]
           [create-default-font/step]
           [create-textures/step]
           [create-world-unit-scale/step]
           [create-world-viewport/step] ; FIXME DEPS
           [create-cursors/step]
           [create-ui-viewport/step] ; FIXME DEPS
           [create-stage/step] ; FIXME DEPS
           [create-skin/step]
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
