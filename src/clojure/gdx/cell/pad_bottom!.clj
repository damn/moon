(ns clojure.gdx.cell.pad-bottom!
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn f [^Cell cell n]
  (.padBottom cell (float n)))
