(ns clojure.gdx.scene2d.stage
  (:require [clojure.gdx.scene2d.group :as group]
            [clojure.graphics.viewport :as viewport])
  (:import (clojure.gdx Stage)))

(defn create [viewport batch]
  (Stage. viewport batch))

(defn ctx [^Stage stage]
  (.ctx stage))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))

(defn add-actor! [^Stage stage actor]
  (.addActor stage actor))

(defn find-actor [^Stage stage name]
  (-> stage
      .getRoot
      (group/find-actor name)))

(defn mouseover-actor [^Stage stage position]
  (let [[x y] (viewport/unproject (.getViewport stage) position)
        touchable? true]
    (.hit stage x y touchable?) ))

(defn viewport [^Stage stage]
  (.getViewport stage))

(defn act! [^Stage stage]
  (.act stage))

(defn draw! [^Stage stage]
  (.draw stage))
