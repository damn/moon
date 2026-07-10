(ns clojure.unproject
  (:require [gdl.math.vector2 :as vector2]
            [gdl.utils.viewport :as viewport]))

(defn f [viewport [x y]]
  (-> viewport
      (viewport/unproject (vector2/new x y))
      vector2/clojurize))
