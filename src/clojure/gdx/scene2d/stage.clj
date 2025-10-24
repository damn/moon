(ns clojure.gdx.scene2d.stage
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn root [^Stage stage]
  (.getRoot stage))

(defn viewport [^Stage stage]
  (.getViewport stage))

(defn add-actor! [^Stage stage actor]
  (.addActor stage actor))

(defn root [^Stage stage]
  (.getRoot stage))

(defn act! [^Stage stage]
  (.act stage))

(defn draw! [^Stage stage]
  (.draw stage))

(defn hit [^Stage stage [x y] touchable?]
  (.hit stage x y touchable?))

(defn clear! [^Stage stage]
  (.clear stage))
