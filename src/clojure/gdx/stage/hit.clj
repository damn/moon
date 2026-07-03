(ns clojure.gdx.stage.hit
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn f [^Stage stage x y touchable?]
  (.hit stage (float x) (float y) touchable?))
