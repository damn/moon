(ns gdx.pixmap-texture-data
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)
           (com.badlogic.gdx.graphics.glutils PixmapTextureData)))

(defn create [pixmap format dispose-pixmap? use-mip-maps?]
  ;; Preserve prior wrapper→facade arg swap into the Java ctor.
  (PixmapTextureData. ^Pixmap pixmap
                      ^Pixmap$Format format
                      dispose-pixmap?
                      use-mip-maps?))
