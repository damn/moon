(ns clojure.button-group
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup)))

(defn add! [^ButtonGroup button-group ^Button button]
  (ButtonGroup/.add button-group button))

(defn get-checked [^ButtonGroup button-group]
  (ButtonGroup/.getChecked button-group))

(defn new []
  (ButtonGroup.))

(defn remove! [^ButtonGroup button-group ^Button button]
  (ButtonGroup/.remove button-group button))

(defn set-max-check-count! [^ButtonGroup button-group n]
  (ButtonGroup/.setMaxCheckCount button-group (int n)))

(defn set-min-check-count! [^ButtonGroup button-group n]
  (ButtonGroup/.setMinCheckCount button-group (int n)))
