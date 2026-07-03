(ns clojure.gdx.text-button.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextButton)))

(defn f [^String text ^Skin skin]
  (TextButton. text skin))
