(ns gdx.file-texture-data
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)
           (com.badlogic.gdx.graphics.glutils FileTextureData)))

(defn new [file-handle pixmap pixmap-format use-mipmaps?]
  (FileTextureData. ^FileHandle file-handle
                    ^Pixmap pixmap
                    ^Pixmap$Format pixmap-format
                    use-mipmaps?))
