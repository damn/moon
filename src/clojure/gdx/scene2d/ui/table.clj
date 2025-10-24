(ns clojure.gdx.scene2d.ui.table
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add! [^Table table actor]
  (.add table ^Actor actor))

(defn cells [^Table table]
  (.getCells table))

(defn row! [^Table table]
  (.row table))
