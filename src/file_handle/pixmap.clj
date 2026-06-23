(ns file-handle.pixmap
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap)))

(defn f
  [^FileHandle file-handle]
  (Pixmap. file-handle))
