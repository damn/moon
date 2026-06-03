(ns clojure.gdx.scene2d.stage
  (:import (clojure.lang ILookup)
           (clojure.gdx.scene2d Stage)))

(defn create [viewport batch]
  (proxy [Stage ILookup] [viewport batch]
    (valAt [k]
      (case k
        ; TODO :stage/root
        :stage/ctx      (.ctx         ^Stage this)
        :stage/viewport (.getViewport ^Stage this)))))

(defn root [^Stage stage]
  (.getRoot stage))

(defn hit [^Stage stage [x y]]
  (.hit stage x y true))
