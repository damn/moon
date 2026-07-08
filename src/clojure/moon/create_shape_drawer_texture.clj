(ns clojure.moon.create-shape-drawer-texture
  (:require [clojure.disposable :as disposable]
            [clojure.pixmap :as pixmap]
            [clojure.pixmap$format :as pixmap-format]
            [clojure.texture :as texture]))

(defn f [ctx]
  (let [pixmap (doto (pixmap/new 1 1 pixmap-format/rgba8888)
                (pixmap/set-color! 1 1 1 1)
                (pixmap/draw-pixel! 0 0))
        texture* (texture/new-from-pixmap pixmap)]
    (disposable/dispose! pixmap)
    (assoc ctx :ctx/shape-drawer-texture texture*)))
