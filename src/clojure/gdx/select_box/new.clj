(ns clojure.gdx.select-box.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox Skin)))

(defn f [^Skin skin]
  (SelectBox. skin))
