(ns gdl.scenes.scene2d.ui.button-group
  (:refer-clojure :exclude [new remove])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]))

(defn add! [& args]
  (apply button-group/add args))

(defn get-checked [& args]
  (apply button-group/getChecked args))

(defn new [& args]
  (apply button-group/new args))

(defn remove! [& args]
  (apply button-group/remove args))

(defn set-max-check-count! [& args]
  (apply button-group/setMaxCheckCount args))

(defn set-min-check-count! [& args]
  (apply button-group/setMinCheckCount args))
