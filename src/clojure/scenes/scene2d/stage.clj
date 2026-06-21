(ns clojure.scenes.scene2d.stage
  (:import (clojure.lang ILookup)
           (scene2d Stage)))

(defn create [viewport batch]
  (proxy [Stage ILookup] [viewport batch]
    (valAt [k]
      (case k
        :stage/root     (.getRoot     ^Stage this)
        :stage/ctx      (.ctx         ^Stage this)
        :stage/viewport (.getViewport ^Stage this)))))
