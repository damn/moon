(ns clojure.gdx.cell.colspan!
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn f [^Cell cell n]
  (.colspan cell (int n)))
