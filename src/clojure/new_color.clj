(ns clojure.new-color
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.color :as color]))

(defn f [& args]
  (apply color/f args))
