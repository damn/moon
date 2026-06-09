(ns stage.dev-menu.update-labels
  (:require [com.badlogic.gdx.graphics.frames-per-second :as frames-per-second]
            [com.badlogic.gdx.graphics.orthographic-camera.get-zoom :refer [get-zoom]]
            [moon.number :as number]))

(def v
  [
   {:label "elapsed-time"
    :update-fn (fn [{:keys [ctx/elapsed-time]}]
                 (str (number/readable elapsed-time) " seconds"))
    :icon "images/clock.png"}
   {:label "FPS"
    :update-fn (fn [{:keys [ctx/graphics]}]
                 (frames-per-second/f graphics))
    :icon "images/fps.png"}
   {:label "Mouseover-entity id"
    :update-fn (fn [{:keys [ctx/mouseover-eid]}]
                 (when-let [entity (and mouseover-eid @mouseover-eid)]
                   (:entity/id entity)))
    :icon "images/mouseover.png"}
   {:label "paused?"
    :update-fn :ctx/paused?}
   {:label "GUI"
    :update-fn (fn [{:keys [ctx/ui-mouse-position]}]
                 (mapv int ui-mouse-position))}
   {:label "World"
    :update-fn (fn [{:keys [ctx/world-mouse-position]}]
                 (mapv int world-mouse-position))}
   {:label "Zoom"
    :update-fn (fn [{:keys [ctx/world-viewport]}]
                 (get-zoom (:viewport/camera world-viewport)))
    :icon "images/zoom.png"}
   ])
