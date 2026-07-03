(ns clojure.gdx.select-box.get-selected
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn f [^SelectBox select-box]
  (SelectBox/.getSelected select-box))
