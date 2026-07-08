(ns clojure.ui.cell.height!
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn f [^Cell cell n]
  (.height cell (float n)))
