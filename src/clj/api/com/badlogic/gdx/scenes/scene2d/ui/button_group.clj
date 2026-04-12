(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.button-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               ButtonGroup)))

(defn create []
  (ButtonGroup.))

(defn set-max-check-count! [^ButtonGroup button-group max-check-count]
  (.setMaxCheckCount button-group max-check-count))

(defn set-min-check-count! [^ButtonGroup button-group min-check-count]
  (.setMinCheckCount button-group min-check-count))

(defn add! [^ButtonGroup button-group ^Button button]
  (.add button-group button))

(defn remove! [^ButtonGroup button-group ^Button button]
  (.remove button-group button))

(defn checked [^ButtonGroup button-group]
  (.getChecked button-group))
