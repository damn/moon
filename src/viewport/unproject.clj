(ns viewport.unproject
  (:require [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [gdx.math.vector2 :as vector2]
            [gdx.math.vector2.clojurize :as clojurize]))

(defn f [viewport xy]
  (-> viewport
      (viewport/unproject (vector2/f xy))
      clojurize/f))
