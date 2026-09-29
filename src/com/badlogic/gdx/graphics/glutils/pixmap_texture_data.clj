(ns com.badlogic.gdx.graphics.glutils.pixmap-texture-data
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)
           (com.badlogic.gdx.graphics.glutils PixmapTextureData)))

(defn new [^Pixmap pixmap ^Pixmap$Format format use-mip-maps? dispose-pixmap?]
  (PixmapTextureData. pixmap format use-mip-maps? dispose-pixmap?))
