(ns clojure.gdx.vector3.clojurize
  (:require [clojure.gdx.vector3.x :as x]
            [clojure.gdx.vector3.y :as y]
            [clojure.gdx.vector3.z :as z]))

(defn f [v3]
  [(x/f v3)
   (y/f v3)
   (z/f v3)])
