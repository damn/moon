(ns clojure.expand-x!
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn f [^Cell cell]
  (.expandX cell))
