(ns com.badlogic.gdx.scenes.scene2d.ui.table
  (:refer-clojure :exclude [new add])
  (:import
           (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)
           ))

(defn add! [^Table table ^Actor actor]
  (Table/.add table actor))

(defn defaults [^Table table]
  (.defaults table))

(defn new []
  (Table.))

(defn row! [^Table table]
  (Table/.row table))
