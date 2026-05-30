(ns application.create
  (:require [create.audio]
            [create.require-components]
            [create.stage]
            [create.batch]
            [create.shape-drawer-texture]
            [create.default-font]
            [create.shape-drawer]
            [create.textures]
            [create.cursors]
            [create.skin]
            [create.record]
            [create.unorganised]
            [create.controls]
            [create.colors]
            [create.render-z-order]
            [create.max-speed]
            [create.db]
            [create.add-stage-actors]
            [create.tiled-map]
            [create.grid]
            [create.content-grid]
            [create.explored-tile-corners]
            [create.world-viewport]
            [create.raycaster]
            [create.spawn-player]
            [create.spawn-enemies]))

(defn do! [app]
  (-> {:ctx/app app}
      create.require-components/step
      create.batch/step
      create.audio/step
      create.shape-drawer-texture/step
      create.shape-drawer/step
      create.skin/step
      create.stage/step
      create.cursors/step
      create.textures/step
      create.unorganised/step
      create.world-viewport/step
      create.default-font/step
      create.record/step
      create.controls/step
      create.colors/step
      create.render-z-order/step
      create.max-speed/step
      create.db/step
      create.add-stage-actors/step
      create.tiled-map/step
      create.grid/step
      create.content-grid/step
      create.explored-tile-corners/step
      create.raycaster/step
      create.spawn-player/step
      create.spawn-enemies/step))
