(ns gdx.scenes.scene2d.stage
  (:require [gdx.scenes.scene2d.group :as group]
            [gdx.utils.viewport.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (gdx.scenes.scene2d Stage)))

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

(defn find-actor [^Stage stage name]
  (-> stage
      .getRoot
      (group/find-actor name)))

(defn mouseover-actor [^Stage stage position]
  (let [[x y] (-> stage :stage/viewport (viewport/unproject position))]
    (.hit stage x y true)))
