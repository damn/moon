(ns clojure.gdx.pixmap.new
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)))

(defn f
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height ^Pixmap$Format format]
   (Pixmap. (int width) (int height) format)))
