(ns moon.file-texture-data
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.glutils.file-texture-data :as file-texture-data]))

(defn new [file-handle pixmap pixmap-format use-mipmaps?]
  (file-texture-data/new file-handle
                         pixmap
                         pixmap-format
                         use-mipmaps?))
