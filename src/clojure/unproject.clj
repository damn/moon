(ns clojure.unproject
  (:require [clojure.vector2 :as vector2]
            [clojure.viewport :as viewport]))

(defn f [viewport [x y]]
  (-> viewport
      (viewport/unproject (vector2/new x y))
      vector2/clojurize))
