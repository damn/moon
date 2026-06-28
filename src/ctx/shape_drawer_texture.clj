(ns ctx.shape-drawer-texture
  (:require [gdx.graphics.pixmap :as pixmap])
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Texture)))

(defn step [_ctx]
  (let [pixmap (doto (pixmap/f 1 1)
                 (Pixmap/.setColor 1 1 1 1)
                 (Pixmap/.drawPixel 0 0))
        texture (Texture. pixmap)]
    (Pixmap/.dispose pixmap)
    texture))
