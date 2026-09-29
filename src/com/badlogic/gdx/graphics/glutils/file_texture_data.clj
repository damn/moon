(ns com.badlogic.gdx.graphics.glutils.file-texture-data
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)
           (com.badlogic.gdx.graphics.glutils FileTextureData)))

(defn new [^FileHandle file ^Pixmap pixmap ^Pixmap$Format format use-mip-maps?]
  (FileTextureData. file pixmap format use-mip-maps?))
