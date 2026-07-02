(ns clojure.gdx.skin.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn f [file-handle]
  (Skin. file-handle))
