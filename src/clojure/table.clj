(ns clojure.table
  (:refer-clojure :exclude [new add])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]))

(defn add! [& args]
  (apply table/add! args))

(defn defaults [& args]
  (apply table/defaults args))

(defn new [& args]
  (apply table/new args))

(defn row! [& args]
  (apply table/row! args))
