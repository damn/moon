(ns game.impl.white-pixel-texture
  (:require [com.badlogic.gdx.gdx :as gdx]
            [gdl.graphics.pixmap :as pixmap]))

(defn create [_ctx]
  (let [pixmap (doto (gdx/pixmap 1 1)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (pixmap/dispose! pixmap)
    texture))
