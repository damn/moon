(ns game.impl.white-pixel-texture
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.texture :as texture]))

(defn create [_ctx]
  (let [pixmap (doto (pixmap/create 1 1)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (texture/create pixmap)]
    (pixmap/dispose! pixmap)
    texture))
