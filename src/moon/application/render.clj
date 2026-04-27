(ns moon.application.render
  (:require moon.render.if-not-paused
            moon.render.remove-destroyed-entities
            moon.render.assoc-paused
            moon.render.get-stage-ctx
            moon.render.update-player-state
            moon.render.draw-tiled-map
            moon.render.validate
            moon.render.update-mouse
            moon.render.update-mouseover-eid
            moon.render.draw-on-world-viewport
            moon.render.check-debug-viewer
            moon.render.active-entities
            moon.render.set-camera
            moon.render.clear-screen
            moon.render.window-camera-controls
            moon.render.stage))

(defn do! [ctx]
  (-> ctx
      moon.render.get-stage-ctx/step
      moon.render.validate/step
      moon.render.update-mouse/step
      moon.render.update-mouseover-eid/step
      moon.render.check-debug-viewer/step
      moon.render.active-entities/step
      moon.render.set-camera/step
      moon.render.clear-screen/step
      moon.render.draw-tiled-map/step
      moon.render.draw-on-world-viewport/step
      moon.render.update-player-state/step
      moon.render.assoc-paused/step
      moon.render.if-not-paused/step
      moon.render.remove-destroyed-entities/step
      moon.render.window-camera-controls/step
      moon.render.stage/step
      moon.render.validate/step))
