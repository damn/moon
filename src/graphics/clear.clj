(ns graphics.clear
  (:require [graphics.get-gl20 :as get-gl20]
            [gl20.clear :as clear]
            [gl20.clear-color :as clear-color]
            [gl20.color-buffer-bit :as color-buffer-bit]))

(defn f!
  [graphics r g b a]
  (let [gl (get-gl20/f graphics)]
    (clear-color/f gl r g b a)
    (clear/f gl color-buffer-bit/v)))
