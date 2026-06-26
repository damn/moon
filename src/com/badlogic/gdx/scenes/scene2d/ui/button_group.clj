(ns com.badlogic.gdx.scenes.scene2d.ui.button-group
  (:require [com.badlogic.gdx.scenes.scene2d.ui.button :as button])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn create []
  (ButtonGroup.))

(defn set-max-check-count! [^ButtonGroup group count]
  (.setMaxCheckCount group count))

(defn set-min-check-count! [^ButtonGroup group count]
  (.setMinCheckCount group count))

(defn add! [^ButtonGroup group button]
  (ButtonGroup/.add group (button/type-hint button)))

(defn remove! [^ButtonGroup group button]
  (ButtonGroup/.remove group (button/type-hint button)))

(defn get-checked [^ButtonGroup group]
  (ButtonGroup/.getChecked group))
