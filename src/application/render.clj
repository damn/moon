(ns application.render
  (:require render.get-stage-ctx
            render.validate
            render.update-mouseover-eid
            render.update-mouse-positions
            render.check-debug-viewer
            render.set-active-entities
            render.set-camera-position
            render.clear-screen
            render.draw-tiled-map
            render.draw-on-world-viewport
            render.assoc-interaction-state
            render.set-cursor
            render.handle-player-input
            render.assoc-paused
            render.if-not-paused
            render.remove-destroyed-entities
            render.window-camera-controls
            render.update-draw-stage))

(defn do! [ctx]
  (-> ctx
      render.get-stage-ctx/step
      render.validate/step
      render.update-mouse-positions/step
      render.update-mouseover-eid/step
      render.check-debug-viewer/step
      render.set-active-entities/step
      render.set-camera-position/step
      render.clear-screen/step
      render.draw-tiled-map/step
      render.draw-on-world-viewport/step
      render.assoc-interaction-state/step
      render.set-cursor/step
      render.handle-player-input/step
      (dissoc :ctx/interaction-state)
      render.assoc-paused/step
      render.if-not-paused/step
      render.remove-destroyed-entities/step
      render.window-camera-controls/step
      render.update-draw-stage/step
      render.validate/step))
