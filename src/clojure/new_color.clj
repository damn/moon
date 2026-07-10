(ns clojure.new-color
  (:refer-clojure :exclude [new])
  (:require [gdl.graphics.color :as color]))

(defn f [& args]
  (apply color/new args))
