(ns gdl.scenes.scene2d.group
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn add-actor! [& args]
  (apply group/addActor args))

(defn clear-children! [& args]
  (apply group/clearChildren args))

(defn find-actor [& args]
  (apply group/findActor args))

(defn get-children [& args]
  (apply group/getChildren args))

(defn new [& args]
  (apply group/new args))
