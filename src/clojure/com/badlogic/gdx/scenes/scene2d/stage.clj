(ns clojure.com.badlogic.gdx.scenes.scene2d.stage
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.scenes.scene2d Actor Stage)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn create [^Viewport viewport ^Batch batch]
  (Stage. viewport batch))

(defn add-actor! [^Stage stage ^Actor actor]
  (.addActor stage actor))

(defn viewport [^Stage stage]
  (.getViewport stage))

(defn root [^Stage stage]
  (.getRoot stage))

(defn hit [^Stage stage x y]
  (.hit stage (float x) (float y) true))

(defn act! [^Stage stage]
  (.act stage))

(defn draw! [^Stage stage]
  (.draw stage))
