(ns clojure.gdx.unproject
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn f [viewport xy]
  (-> viewport
      (viewport/unproject (vector2/new xy))
      vector2/clojurize))
