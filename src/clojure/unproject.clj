(ns clojure.unproject
  (:require [gdl.vector2 :as vector2]
            [gdl.viewport :as viewport]))

(defn f [viewport [x y]]
  (-> viewport
      (viewport/unproject (vector2/new x y))
      vector2/clojurize))
