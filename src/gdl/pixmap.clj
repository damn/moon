(ns gdl.pixmap
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)))

(defn f
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height]
   (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888)))


