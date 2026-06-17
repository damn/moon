(ns create.shape-drawer-texture
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn step [_ctx]
  (let [pixmap (doto (pixmap/create 1 1)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (pixmap/dispose! pixmap)
    texture))
