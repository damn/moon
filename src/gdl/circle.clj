(ns gdl.circle
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.math.circle :as circle]))

(defn new [& args]
  (apply circle/new args))
