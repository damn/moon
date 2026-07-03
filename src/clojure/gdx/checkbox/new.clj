(ns clojure.gdx.checkbox.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox Skin)))

(defn f [text skin]
  (CheckBox. ^String text ^Skin skin))
