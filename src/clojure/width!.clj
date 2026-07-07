(ns clojure.width!
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn f [^Cell cell n]
  (.width cell (float n)))
