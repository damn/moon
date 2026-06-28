(ns ctx.shape-drawer-texture
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format
                                      Texture)))

(defn step [_ctx]
  (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                 (Pixmap/.setColor 1 1 1 1)
                 (Pixmap/.drawPixel 0 0))
        texture (Texture. pixmap)]
    (Pixmap/.dispose pixmap)
    texture))
