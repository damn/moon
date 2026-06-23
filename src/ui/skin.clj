(ns ui.skin
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn f [^FileHandle file-handle]
  (Skin. file-handle))
