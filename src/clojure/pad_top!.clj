(ns clojure.pad-top!
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn f [^Cell cell n]
  (.padTop cell (float n)))
