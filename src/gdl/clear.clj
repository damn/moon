(ns gdl.clear
  (:require [gdl.get-gl20 :as get-gl20]
            [gdl.gl20 :as gl20]))

(defn f!
  [graphics r g b a]
  (let [gl (get-gl20/f graphics)]
    (gl20/clear-color! gl r g b a)
    (gl20/clear! gl gl20/color-buffer-bit)))
