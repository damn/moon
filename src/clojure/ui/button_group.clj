(ns clojure.ui.button-group
  (:refer-clojure :exclude [new remove])
  (:require [gdl.scenes.scene2d.ui.button-group :as button-group]))

(defn add! [& args]
  (apply button-group/add! args))

(defn get-checked [& args]
  (apply button-group/get-checked args))

(defn new [& args]
  (apply button-group/new args))

(defn remove! [& args]
  (apply button-group/remove! args))

(defn set-max-check-count! [& args]
  (apply button-group/set-max-check-count! args))

(defn set-min-check-count! [& args]
  (apply button-group/set-min-check-count! args))
