(ns com.badlogic.gdx.scenes.scene2d.ui.table
  (:refer-clojure :exclude [new add])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn new []
  (Table.))

(defn add [^Table table ^Actor actor]
  (.add table actor))

(defn defaults [^Table table]
  (.defaults table))

(defn row [^Table table]
  (.row table))
