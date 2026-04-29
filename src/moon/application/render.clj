(ns moon.application.render
  (:require moon.application.render.if-not-paused
            moon.application.render.remove-destroyed-entities
            moon.application.render.assoc-paused
            moon.application.render.get-stage-ctx
            moon.application.render.update-player-state
            moon.application.render.draw-tiled-map
            moon.application.render.validate
            moon.application.render.update-mouse
            moon.application.render.update-mouseover-eid
            moon.application.render.draw-on-world-viewport
            moon.application.render.check-debug-viewer
            moon.application.render.active-entities
            moon.application.render.set-camera
            moon.application.render.clear-screen
            moon.application.render.window-camera-controls
            moon.application.render.stage))

(defn do! [ctx]
  (-> ctx
      moon.application.render.get-stage-ctx/step
      moon.application.render.validate/step
      moon.application.render.update-mouse/step
      moon.application.render.update-mouseover-eid/step
      moon.application.render.check-debug-viewer/step
      moon.application.render.active-entities/step
      moon.application.render.set-camera/step
      moon.application.render.clear-screen/step
      moon.application.render.draw-tiled-map/step
      moon.application.render.draw-on-world-viewport/step
      moon.application.render.update-player-state/step
      moon.application.render.assoc-paused/step
      moon.application.render.if-not-paused/step
      moon.application.render.remove-destroyed-entities/step
      moon.application.render.window-camera-controls/step
      moon.application.render.stage/step
      moon.application.render.validate/step))
