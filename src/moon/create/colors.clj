(ns moon.create.colors
  (:require [gdl.colors :as colors]))

(defn step [ctx colors]
  (colors/put! colors)
  ctx)
