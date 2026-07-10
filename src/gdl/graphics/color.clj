(ns gdl.graphics.color
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.color :as color]))

(defn new [& args]
  (apply color/new args))

(defn to-float-bits [& args]
  (apply color/toFloatBits args))
