(ns com.badlogic.gdx.graphics.pixmap
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap)))

(defn create [^FileHandle file-handle]
  (Pixmap. file-handle))

(defn dispose! [^Pixmap pixmap]
  (.dispose pixmap))
