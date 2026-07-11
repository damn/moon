(ns moon.stage
  (:import (clojure.lang ILookup)
           (clojure Stage))
  (:require [clojure.set-ctx :as set-ctx]))

(defn create [viewport batch]
  (proxy [Stage ILookup] [viewport batch]
    (valAt [k]
      (case k
        :stage/root     (.getRoot     ^Stage this)
        :stage/ctx      (.ctx         ^Stage this)
        :stage/viewport (.getViewport ^Stage this)))))

(defn apply-ctx! [stage f]
  (set-ctx/f stage (f (:stage/ctx stage))))
