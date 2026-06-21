(ns clojure.stage.hit
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn hit [^Stage stage [x y]]
  (.hit stage x y true))
