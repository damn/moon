(ns clojure.gdx.cell.pad!
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn f [^Cell cell n]
  (.pad cell (float n)))
