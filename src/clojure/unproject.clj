(ns clojure.unproject
  (:require [gdl.math.vector2 :as vector2]
            [com.badlogic.gdx.math.vector2 :as gdx-vector2]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn f [viewport [x y]]
  (-> viewport
      (viewport/unproject (gdx-vector2/new x y))
      vector2/clojurize))
