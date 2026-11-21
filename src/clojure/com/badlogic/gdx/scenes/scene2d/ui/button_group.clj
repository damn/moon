(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.button-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup)))

(defn create []
  (ButtonGroup.))

(defn add! [^ButtonGroup button-group ^Button button]
  (.add button-group button))

(defn remove! [^ButtonGroup button-group ^Button button]
  (.remove button-group button))

(defn checked [^ButtonGroup button-group]
  (.getChecked button-group))

(defn set-max-check-count! [^ButtonGroup button-group n]
  (.setMaxCheckCount button-group (int n)))

(defn set-min-check-count! [^ButtonGroup button-group n]
  (.setMinCheckCount button-group (int n)))
