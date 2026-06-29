(ns clojure.gdx.unproject
  (:require [clojure.gdx.new-vector2 :as new-vector2]
            [gdx.math.vector2.clojurize :as clojurize])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn f [^Viewport viewport xy]
  (-> viewport
      (.unproject (new-vector2/f xy))
      clojurize/f))
