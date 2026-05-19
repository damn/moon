(ns game.impl.white-pixel-texture
  (:require [com.badlogic.gdx.graphics.pixmap]
            [gdl.graphics.pixmap :as pixmap]))

(defn create [_ctx]
  (let [pixmap (doto (com.badlogic.gdx.graphics.pixmap/create 1 1)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (pixmap/dispose! pixmap)
    texture))
