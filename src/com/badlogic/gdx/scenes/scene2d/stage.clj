(ns com.badlogic.gdx.scenes.scene2d.stage
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn hit [^Stage stage x y touchable?]
  (.hit stage x y touchable?))

(defn draw [^Stage stage]
  (.draw stage))

(defn add-actor! [^Stage stage actor]
  (.addActor stage actor))

(defn act [^Stage stage]
  (.act stage))

(defn get-root [^Stage stage]
  (.getRoot stage))

(defn get-viewport [^Stage stage]
  (.getViewport stage))
