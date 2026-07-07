(ns gdx.unproject
  (:require [clojure.vector2 :as vector2]
            [clojure.viewport :as viewport]))

(defn f [viewport xy]
  (-> viewport
      (viewport/unproject (vector2/new xy))
      vector2/clojurize))
