(ns clojure.expand-y!
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn f [^Cell cell]
  (.expandY cell))
