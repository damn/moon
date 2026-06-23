(ns viewport.unproject
  (:require [gdx.vector2 :as vector2]
            [gdx.vector2.clojurize :as clojurize])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn f [^Viewport viewport xy]
  (-> viewport
      (.unproject (vector2/f xy))
      clojurize/f))
