(ns clojure.white-pixel-texture
  (:require [gdx.graphics.pixmap :as pixmap]))

(defn f []
  (let [pixmap (doto (pixmap/create 1 1)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (pixmap/dispose! pixmap)
    texture))
