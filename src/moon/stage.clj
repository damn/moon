(ns moon.stage
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport])
  (:import (moon Stage)))

; TODO unnecessary fns !
(defn ctx [^Stage stage]
  (.ctx stage))

(defn add-actor! [^Stage stage actor]
  (.addActor stage actor))

(defn root [^Stage stage]
  (.getRoot stage))

(defn viewport [^Stage stage]
  (.getViewport stage))

(defn mouseover-actor [^Stage stage position]
  (let [[x y] (viewport/unproject (viewport stage) position)]
    (.hit stage x y true)))
