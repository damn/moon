(ns clojure.gdx.label.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label Skin)))

(defn f [^String text ^Skin skin]
  (Label. text skin))
