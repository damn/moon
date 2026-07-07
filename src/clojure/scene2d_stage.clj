(ns clojure.scene2d-stage
  (:import (clojure.lang ILookup)
           (moon Stage)))

(defn create [viewport batch]
  (proxy [Stage ILookup] [viewport batch]
    (valAt [k]
      (case k
        :stage/root     (.getRoot     ^Stage this)
        :stage/ctx      (.ctx         ^Stage this)
        :stage/viewport (.getViewport ^Stage this)))))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))
