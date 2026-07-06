(ns clojure.gdx.unproject
  (:require [clojure.gdx.vector2.new :as new-vector2]
            [clojure.gdx.viewport.unproject :as unproject]
            [clojure.gdx.vector2.clojurize :as clojurize]))

(defn f [viewport xy]
  (-> viewport
      (unproject/f (new-vector2/f xy))
      clojurize/f))
