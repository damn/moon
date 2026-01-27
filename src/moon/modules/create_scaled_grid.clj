(ns moon.modules.create-scaled-grid
  (:require [moon.world-fns.utils :as helper]))

(defn step [w]
  (assoc w :scaled-grid (helper/scale-grid (:grid w) (:scale w))))
