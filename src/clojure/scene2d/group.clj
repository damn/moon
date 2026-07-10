(ns clojure.scene2d.group
  (:refer-clojure :exclude [new])
  (:require [gdl.scenes.scene2d.group :as group]))

(defn add-actor! [& args]
  (apply group/add-actor! args))

(defn clear-children! [& args]
  (apply group/clear-children! args))

(defn find-actor [& args]
  (apply group/find-actor args))

(defn get-children [& args]
  (apply group/get-children args))

(defn new [& args]
  (apply group/new args))
