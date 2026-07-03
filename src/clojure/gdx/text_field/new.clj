(ns clojure.gdx.text-field.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextField)))

(defn f [^String text ^Skin skin]
  (TextField. text skin))
