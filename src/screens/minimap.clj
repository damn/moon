(ns screens.minimap
  (:require [gdx.graphics.color :as color]
            [gdx.graphics.orthographic-camera :as orthographic-camera]
            [gdx.graphics.camera :as camera]
            [gdx.input :as input]
            [gdx.input.keys :as input.keys]
            [core.component :refer [defcomponent]]
            [api.context :as ctx :refer [explored?]]
            [api.graphics :as g]
            api.graphics.camera
            [api.screen :as screen]
            app))

; 28.4 viewportwidth
; 16 viewportheight
; camera shows :
;  [-viewportWidth/2, -(viewportHeight/2-1)] - [(viewportWidth/2-1), viewportHeight/2]
; zoom default '1'
; zoom 2 -> shows double amount

; we want min/max explored tiles X / Y and show the whole explored area....

(defn- calculate-zoom [{:keys [world/explored-tile-corners] :as ctx}]
  (let [positions-explored (map first
                                (remove (fn [[position value]]
                                          (false? value))
                                        (seq @explored-tile-corners)))
        left   (apply min-key (fn [[x y]] x) positions-explored)
        top    (apply max-key (fn [[x y]] y) positions-explored)
        right  (apply max-key (fn [[x y]] x) positions-explored)
        bottom (apply min-key (fn [[x y]] y) positions-explored)]
    (api.graphics.camera/calculate-zoom (ctx/world-camera ctx)
                                        :left left
                                        :top top
                                        :right right
                                        :bottom bottom)))

; TODO FIXME deref'fing app/state at each tile corner
; massive performance issue - probably
; => pass context through java tilemap render class
; or prepare colors before
(defn- tile-corner-color-setter [color x y]
  (if (explored? @app/state [x y])
    color/white
    color/black))

(deftype Screen []
  api.screen/Screen
  (show [_ ctx]
    (orthographic-camera/set-zoom! (ctx/world-camera ctx) (calculate-zoom ctx)))

  (hide [_ ctx]
    (orthographic-camera/reset-zoom! (ctx/world-camera ctx)))

  ; TODO fixme not subscreen
  (render [_ {:keys [world/tiled-map] :as context}]
    (ctx/render-tiled-map context tiled-map tile-corner-color-setter)
    (ctx/render-world-view context
                           (fn [g]
                             (g/draw-filled-circle g
                                                   (camera/position (ctx/world-camera context))
                                                   0.5
                                                   color/green)))
    (if (or (input/key-just-pressed? input.keys/tab)
            (input/key-just-pressed? input.keys/escape))
      (ctx/change-screen context :screens/world)
      context)))

(defcomponent :screens/minimap {}
  (screen/create [_ _ctx]
    (->Screen)))
