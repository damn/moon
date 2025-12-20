(ns gdl.ui
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn skin [file-handle]
  (Skin. file-handle))
