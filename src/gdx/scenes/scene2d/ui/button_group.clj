(ns gdx.scenes.scene2d.ui.button-group
  (:require [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]))

(defn create
  [{:keys [max-check-count
           min-check-count]}]
  (doto (button-group/new)
    (button-group/setMaxCheckCount max-check-count)
    (button-group/setMinCheckCount min-check-count)))

(defn add! [button-group button]
  (button-group/add button-group button))

(defn remove! [button-group button]
  (button-group/remove button-group button))

(defn get-checked [button-group]
  (button-group/getChecked button-group))
