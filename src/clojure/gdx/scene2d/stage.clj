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

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))

(defn add-actor! [^Stage stage actor]
  (.addActor stage actor))

(defn act! [^Stage stage]
  (.act stage))

(defn draw! [^Stage stage]
  (.draw stage))

(defn root [^Stage stage]
  (.getRoot stage))

(defn hit [^Stage stage [x y]]
  (.hit stage x y true))
