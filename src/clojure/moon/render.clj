(ns clojure.moon.render
  (:require [clojure.moon.stage-ctx :as stage-ctx]
            [clojure.moon.render-validate :as render-validate]
            [clojure.moon.update-mouse-positions :as update-mouse-positions]
            [clojure.moon.update-mouseover-eid :as update-mouseover-eid]
            [clojure.moon.check-debug-viewer :as check-debug-viewer]
            [clojure.moon.set-active-entities :as set-active-entities]
            [clojure.moon.set-camera-position :as set-camera-position]
            [clojure.moon.clear-screen :as clear-screen]
            [clojure.moon.render-draw-tiled-map :as render-draw-tiled-map]
            [clojure.moon.draw-on-world-viewport :as draw-on-world-viewport]
            [clojure.moon.assoc-interaction-state :as assoc-interaction-state]
            [clojure.moon.set-cursor :as set-cursor]
            [clojure.moon.handle-player-input :as handle-player-input]
            [clojure.moon.dissoc-interaction-state :as dissoc-interaction-state]
            [clojure.moon.assoc-paused :as assoc-paused]
            [clojure.moon.when-not-paused :as when-not-paused]
            [clojure.moon.remove-destroyed-entities :as remove-destroyed-entities]
            [clojure.moon.window-camera-controls :as window-camera-controls]
            [clojure.moon.update-draw-stage :as update-draw-stage]))

(defn render [ctx]
  (-> ctx
      stage-ctx/f
      render-validate/f
      update-mouse-positions/f
      update-mouseover-eid/f
      check-debug-viewer/f
      set-active-entities/f
      set-camera-position/f
      clear-screen/f
      render-draw-tiled-map/f
      draw-on-world-viewport/f
      assoc-interaction-state/f
      set-cursor/f
      handle-player-input/f
      dissoc-interaction-state/f
      assoc-paused/f
      when-not-paused/f
      remove-destroyed-entities/f
      window-camera-controls/f
      update-draw-stage/f
      render-validate/f))
