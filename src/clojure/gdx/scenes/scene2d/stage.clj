(ns clojure.gdx.scenes.scene2d.stage
  (:import (clojure.lang ILookup)
           (clojure Stage))
  (:require [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))

(defn create [viewport batch]
  (proxy [Stage ILookup] [viewport batch]
    (valAt [k]
      (case k
        :stage/root     (.getRoot     ^Stage this)
        :stage/ctx      (.ctx         ^Stage this)
        :stage/viewport (.getViewport ^Stage this)))))

(defn apply-ctx! [stage f]
  (set-ctx! stage (f (:stage/ctx stage))))

(defn add-actor! [stage actor]
  (stage/addActor stage actor))
