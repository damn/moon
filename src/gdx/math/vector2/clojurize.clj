(ns gdx.math.vector2.clojurize
  (:require [clojure.gdx.vector2.x :as x]
            [clojure.gdx.vector2.y :as y]))

(defn f [v2]
  [(x/f v2)
   (y/f v2)])
