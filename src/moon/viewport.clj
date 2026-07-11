(ns moon.viewport
  (:require [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [gdx.math.vector2 :as vector2]))

(defn unproject [viewport v2]
  (-> viewport
      (viewport/unproject (vector2/new v2))
      vector2/clojurize))
